package net.stackmaquinas.plugin.managers;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.database.MySQL;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class MaquinaPlayerManager {

    private final Set<MaquinaPlayer> maquinaPlayerManagerSet;
    private final FileConfiguration maquinasFile;
    private final StackCore instance;

    public MaquinaPlayerManager() {
        this.maquinaPlayerManagerSet = new HashSet<>();
        this.maquinasFile = StackCore.getInstance().getDbYml();
        this.instance = StackCore.getInstance();
    }


    public void serialise() {
        ConfigurationSection cfg = maquinasFile.getConfigurationSection("maquinas");
        if(cfg != null) {
            cfg.getKeys(false).forEach(s -> {
                if(s != null) {
                    List<UpgradeMaquina> upgradeMaquinaList = new ArrayList<>();
                    UUID owner = UUID.fromString(cfg.getString(s+".owner"));
                    PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(owner);
                    Maquina m = StackCore.getMaquinaManager().getMaquina(cfg.getString(s+".key"));
                    cfg.getStringList(s+".upgrades").forEach(x -> {
                        String[] all = x.split(";");
                        String upgradeTypeName = all[0];
                        int upgradeLevel = Integer.valueOf(all[1]);
                        UpgradeType ut = StackCore.getUpgradeManager().getUpgradeType(upgradeTypeName);
                        Upgrade u = StackCore.getUpgradeManager().getUpgrade(ut);
                        upgradeMaquinaList.add(new UpgradeMaquina(
                                StackCore.getUpgradeManager(),
                                u,
                                upgradeLevel
                        ));
                    });
                    MaquinaPlayer mp = new MaquinaPlayer(
                            this,
                            s,
                            m,
                            new BigInteger(cfg.getString(s+".amount")),
                            owner,
                            new BigInteger(cfg.getString(s+".totalDrops")),
                            cfg.getLong(s+".nextDrop"),
                            upgradeMaquinaList,
                            true
                    );
                    maquinaPlayerManagerSet.add(mp);
                    pm.addMaquina(mp);
                }
            });
        }
    }

    public void deserialise() throws IOException {
        if(StackCore.getInstance().usingMySQL) {
            if(!maquinaPlayerManagerSet.isEmpty()) {
                maquinaPlayerManagerSet.forEach(MaquinaPlayer -> {
                    if(MySQL.machineExists(MaquinaPlayer.getId())) {
                        MySQL.updateMachine(MaquinaPlayer);
                    } else {
                        try {
                            MySQL.createMachine(MaquinaPlayer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            if (!maquinaPlayerManagerSet.isEmpty()) {
                maquinasFile.set("maquinas", null);
                maquinasFile.save(StackCore.getInstance().getDbFile());
                maquinaPlayerManagerSet.forEach(MaquinaPlayer -> {
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".owner", MaquinaPlayer.getOwner().toString());
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".key", MaquinaPlayer.getMaquina().getKey());
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".amount", MaquinaPlayer.getMaquinaAmount());
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".totalDrops", MaquinaPlayer.getTotalDrops());
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".nextDrop", MaquinaPlayer.getNextDrop());
                    List<String> upgradeList = new ArrayList<>();
                    List<UpgradeMaquina> lista = MaquinaPlayer.getUpgradeMaquinaList();
                    for(UpgradeMaquina um : lista) {
                        upgradeList.add(um.getUpgrade().getUpgradeType().getName()+";"+um.getCurrentLevel());
                    }
                    maquinasFile.set("maquinas." + MaquinaPlayer.getId() + ".upgrades", upgradeList);
                });
                maquinasFile.save(StackCore.getInstance().getDbFile());
                return;
            }
            maquinasFile.set("maquinas", null);
            maquinasFile.save(StackCore.getInstance().getDbFile());
        }
    }



    public void addMaquinaObjeto(MaquinaPlayer mo) {
        this.maquinaPlayerManagerSet.add(mo);
    }
    public Set<MaquinaPlayer> getMaquinaObjetoSet() { return this.maquinaPlayerManagerSet; }
    public MaquinaPlayer getMaquinaPlayer(UUID u) {
        return maquinaPlayerManagerSet.stream().filter(MaquinaPlayer -> MaquinaPlayer.getOwner().equals(u)).findFirst().orElse(null);
    }

    public MaquinaPlayer getMaquinaPlayer(int id) {
        return maquinaPlayerManagerSet.stream().filter(MaquinaPlayer -> MaquinaPlayer.getId().equals(id)).findFirst().orElse(null);
    }

}
