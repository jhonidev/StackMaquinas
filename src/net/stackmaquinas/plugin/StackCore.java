package net.stackmaquinas.plugin;

import net.milkbowl.vault.economy.Economy;
import net.stackmaquinas.plugin.commands.Maquinas;
import net.stackmaquinas.plugin.database.MySQL;
import net.stackmaquinas.plugin.events.*;
import net.stackmaquinas.plugin.managers.MaquinaManager;
import net.stackmaquinas.plugin.managers.MaquinaPlayerManager;
import net.stackmaquinas.plugin.managers.PlayerMaquinaManager;
import net.stackmaquinas.plugin.managers.UpgradeManager;
import net.stackmaquinas.plugin.menus.MenuPrincipal;
import net.stackmaquinas.plugin.menus.TopMaquinas;
import net.stackmaquinas.plugin.objetos.*;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeType;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import net.stackmaquinas.plugin.utils.PAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StackCore extends JavaPlugin {

    public static StackCore instance;
    private final File dbFile = new File(getDataFolder(), "db.yml");
    private final FileConfiguration dbYml = YamlConfiguration.loadConfiguration(dbFile);
    private final File plFile = new File(getDataFolder(), "players.yml");
    private final FileConfiguration plYml = YamlConfiguration.loadConfiguration(plFile);
    private final File menusFile = new File(getDataFolder(), "menus.yml");
    private FileConfiguration menusYml = YamlConfiguration.loadConfiguration(menusFile);
    private final File msgFile = new File(getDataFolder(), "mensagens.yml");
    private FileConfiguration msgYml = YamlConfiguration.loadConfiguration(msgFile);
    public static HashMap<Integer, String> topMaquinas = new HashMap<>();
    private static MaquinaManager mm;
    private static MaquinaPlayerManager mpm;
    private static PlayerMaquinaManager pmm;
    private static UpgradeManager um;
    public static HashMap<Player, Maquina> buyingMaquina = new HashMap<>();
    private static Economy econ = null;
    public boolean usingMySQL = false;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupRequired();
            if(this.getConfig().getBoolean("mysql.usar")) {
                if(!MySQL.connection()) {
                    Bukkit.getConsoleSender().sendMessage("§c[FastlyMaquinas] Falha ao conectar no MySQL.");
                    Bukkit.getConsoleSender().sendMessage("§c[FastlyMaquinas] Plugin desabilitado.");
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                } else {
                    usingMySQL = true;
                }
            }
            if(!setupEconomy()) {
                Bukkit.getConsoleSender().sendMessage("[FastlyMaquinas] Vault e/ou plugin de economia não encontrado!");
                Bukkit.getConsoleSender().sendMessage("[FastlyMaquinas] Desabilitando plugin..");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            setupCore();
            TopMaquinas.updateTopMenu();
            new BukkitRunnable() {
                @Override
                public void run() {
                    TopMaquinas.updateTopMenu();
                    try {
                        saveBackup();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(this, 6000, 6000);
            new BukkitRunnable() {
                @Override
                public void run() {
                    giveDrops();
                }
            }.runTaskTimer(this, 20, 20);
    }

    public void setupRequired() {
        if(!menusFile.exists()) {
            saveResource("menus.yml",false);
            menusYml = YamlConfiguration.loadConfiguration(menusFile);
        }
        if(!msgFile.exists()) {
            saveResource("mensagens.yml",false);
            msgYml = YamlConfiguration.loadConfiguration(msgFile);
        }
    }

    public void saveBackup() throws IOException {
        mpm.deserialise();
        pmm.deserialise();
        Bukkit.getConsoleSender().sendMessage("§6[FastlyMaquinas] §fBackup realizado.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void setupCore() {
        getCommand("maquinas").setExecutor(new Maquinas());
        Bukkit.getPluginManager().registerEvents(new Join(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new InfiniteInventoryEvent(), this);
        Bukkit.getPluginManager().registerEvents(new RightClick(),this);
        Bukkit.getPluginManager().registerEvents(new OnChat(), this);
        mm = new MaquinaManager();
        mpm = new MaquinaPlayerManager();
        pmm = new PlayerMaquinaManager();
        um = new UpgradeManager();
        new PAPI().register();

        UpgradeType ut1 = new UpgradeType(um,"velocidade","speed");
        UpgradeType ut2 = new UpgradeType(um,"quantidade","amount");

        ConfigurationSection cfg = this.getConfig().getConfigurationSection("maquinas");
        if(cfg != null) {
            int id = 1;
            for(String s : cfg.getKeys(false)) {
                if (s != null) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[FastlyMaquinas] Loading machine..");
                    List<Upgrade> upgradeList = new ArrayList<>();
                    boolean upgradeSpeed = cfg.getBoolean(s+".opcoes.upgrades.upgrade-velocidade.usar");
                    boolean upgradeAmount = cfg.getBoolean(s+".opcoes.upgrades.upgrade-quantidade.usar");

                    if(upgradeSpeed) {
                        upgradeList.add(
                                new Upgrade(
                                        um,
                                        ut1,
                                        cfg.getInt(s+".opcoes.upgrades.upgrade-velocidade.nivel-maximo"),
                                        cfg.getDouble(s+".opcoes.upgrades.upgrade-velocidade.custo.custo-inicial"),
                                        cfg.getDouble(s+".opcoes.upgrades.upgrade-velocidade.custo.aumento-custo"),
                                        cfg.getString(s+".opcoes.upgrades.upgrade-velocidade.custo.pagamento")
                                )
                        );
                    }

                    if(upgradeAmount) {
                        upgradeList.add(
                                new Upgrade(
                                        um,
                                        ut2,
                                        cfg.getInt(s+".opcoes.upgrades.upgrade-quantidade.nivel-maximo"),
                                        cfg.getDouble(s+".opcoes.upgrades.upgrade-quantidade.custo.custo-inicial"),
                                        cfg.getDouble(s+".opcoes.upgrades.upgrade-quantidade.custo.aumento-custo"),
                                        cfg.getString(s+".opcoes.upgrades.upgrade-quantidade.custo.pagamento")
                                )
                        );
                    }

                    Option opt = new Option(
                            cfg.getInt(s+".opcoes.delay-wave"),
                            cfg.getInt(s+".opcoes.drop-inicial"),
                            cfg.getDouble(s+".preco"),
                            cfg.getBoolean(s+".permitir-venda")
                    );

                    boolean dropCommands = cfg.getBoolean(s+".drop.comandos");
                    boolean dropItems = cfg.getBoolean(s+".drop.items");
                    List<ItemDrop> listaDrop = new ArrayList<>();
                    List<String> listaCommands = new ArrayList<>();
                    if(dropItems) {
                        ConfigurationSection itemList = this.getConfig().getConfigurationSection("maquinas." + s + ".drop.lista-items");
                        if(itemList != null) {
                            for(String itemId : itemList.getKeys(false)) {
                                if(itemId != null) {
                                    String name = ChatColor.translateAlternateColorCodes('&',cfg.getString(s + ".drop.lista-items." + itemId + ".nome"));
                                    List<String> lore2 = cfg.getStringList(s + ".drop.lista-items." + itemId + ".lore");
                                    List<String> lore = new ArrayList<>();
                                    for(String y : lore2) {
                                        lore.add(ChatColor.translateAlternateColorCodes('&',y).replaceAll("@valor", FormatMoney.format(cfg.getDouble(s+".drop.lista-items." + itemId + ".valor-venda"), false)).replaceAll("@pagamento", cfg.getString(s+".drop.lista-items." + itemId + ".pagamento-venda")));
                                    }

                                    ItemStack is = new ItemBuilder(Material.getMaterial(cfg.getString(s + ".drop.lista-items." + itemId + ".material")), cfg.getInt(s + ".drop.lista-items." + itemId + ".quantidade"))
                                            .setName(name).setLore(lore).toItemStack();

                                    listaDrop.add(new ItemDrop(is, cfg.getDouble(s+".drop.lista-items." + itemId + ".valor-venda"), cfg.getString(s+".drop.lista-items." + itemId + ".pagamento-venda")));
                                }
                            }
                        }
                    }

                    if(dropCommands) {
                        listaCommands.addAll(cfg.getStringList(s + ".drop.lista-comandos"));
                    }

                    Drop drop = new Drop(
                            dropCommands,
                            dropItems,
                            listaDrop,
                            listaCommands,
                            cfg.getString(s+".nome-drop")
                    );

                    List<String> lore2 = new ArrayList<>();
                    List<String> lore = cfg.getStringList(s+".lore");
                    for(String sx : lore) {
                        lore2.add(ChatColor.translateAlternateColorCodes('&',sx));
                    }
                    new Maquina(mm,s,ChatColor.translateAlternateColorCodes('&',cfg.getString(s+".nome")),lore2,cfg.getString(s+".bloco"),drop,upgradeList,opt,id,cfg.getBoolean(s+".permissao-para-comprar"),cfg.getString(s+".permissao"));
                    id++;
                }
            }
        }
        if(!usingMySQL) {
            pmm.serialise();
            mpm.serialise();
        } else {
            MySQL.serialisePlayers();
            MySQL.serialiseMachines();
        }
        MenuPrincipal.setupInventory();
    }

    public void onDisable() {
        try {
            mpm.deserialise();
            pmm.deserialise();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void giveDrops() {
        for(MaquinaPlayer mp : getMaquinaPlayerManager().getMaquinaObjetoSet()) {
            if(System.currentTimeMillis() >= mp.getNextDrop()) {
                int upgradeAmountQuantidade = 0;
                int upgradeAmountVelocidade = 0;
                for(UpgradeMaquina u : mp.getUpgradeMaquinaList()) {
                    if(u.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("quantidade")) {
                        upgradeAmountQuantidade = u.getCurrentLevel();
                    }
                    if(u.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("velocidade")) {
                        upgradeAmountVelocidade = u.getCurrentLevel()-1;
                    }
                }
                int delayWave = (mp.getMaquina().getOption().getDelayWave()-upgradeAmountVelocidade)*1000;
                int startDrop = mp.getMaquina().getOption().getStartDrop()*upgradeAmountQuantidade * StackCore.getInstance().getConfig().getInt("maquinas." + mp.getMaquina().getKey() + ".opcoes.upgrades.upgrade-quantidade.aumenta-quantidade");
                BigInteger startDropBig = BigInteger.valueOf(startDrop);
                BigInteger acrescentar = mp.getMaquinaAmount().multiply(startDropBig);
                BigInteger totalDrops = mp.getTotalDrops().add(acrescentar);
                mp.setNextDrop(System.currentTimeMillis()+delayWave);
                mp.setTotalDrops(totalDrops);
                return;
            }
        }
    }

    public static StackCore getInstance() {
        return instance;
    }
    public FileConfiguration getDbYml() {
        return dbYml;
    }
    public File getDbFile() {
        return dbFile;
    }
    public FileConfiguration getPlYml() {
        return plYml;
    }
    public File getPlFile() {
        return plFile;
    }
    public FileConfiguration getMenusYml() {
        return menusYml;
    }
    public File getMenusFile() {
        return menusFile;
    }
    public FileConfiguration getMsgYml() {
        return msgYml;
    }
    public File getMsgFile() {
        return msgFile;
    }
    public static MaquinaManager getMaquinaManager() { return mm; }
    public static MaquinaPlayerManager getMaquinaPlayerManager() { return mpm; }
    public static PlayerMaquinaManager getPlayerMaquinaManager() { return pmm; }
    public static UpgradeManager getUpgradeManager() { return um; }
    public static Economy getEconomy() {
        return econ;
    }

}
