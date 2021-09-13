package net.stackmaquinas.plugin.managers;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.database.MySQL;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class PlayerMaquinaManager {

    private final Set<PlayerMaquina> playerMaquinaSet;
    private final FileConfiguration playersFile;
    private final StackCore instance;

    public PlayerMaquinaManager() {
        this.playerMaquinaSet = new HashSet<>();
        this.instance = StackCore.getInstance();
        this.playersFile = StackCore.getInstance().getPlYml();
    }

    public void addPlayerMaquina(PlayerMaquina pm) {
        this.playerMaquinaSet.add(pm);
    }

    public PlayerMaquina getPlayerMaquina(UUID u) {
        return playerMaquinaSet.stream().filter(PlayerMaquina -> PlayerMaquina.getPlayer().equals(u)).findFirst().orElse(null);
    }

    public Set<PlayerMaquina> getPlayerMaquinaSet() { return this.playerMaquinaSet; }

    public void serialise() {
        ConfigurationSection cfg = playersFile.getConfigurationSection("players");
        if(cfg != null) {
            cfg.getKeys(false).forEach(s -> {
                if(s != null) {
                    UUID owner = UUID.fromString(s);
                    List<MaquinaPlayer> lista = new ArrayList<>();
                    List<MaquinaPlayer> maqToReceive = new ArrayList<>();
                    String maquinaKey = null;
                    String maquinaAmount = null;
                    int index=0;
                    for(String x : cfg.getStringList(s+".maquinas-to-receive")) {
                        String[] all = x.split(";");
                        maquinaKey = all[0];
                        maquinaAmount = all[1];
                        index++;
                    }
                    if (index != 0) {
                        Maquina m = StackCore.getMaquinaManager().getMaquina(maquinaKey);
                        List<UpgradeMaquina> upgradeMaquinaList = new ArrayList<>();
                        for (Upgrade u : m.getUpgradeList()) {
                            upgradeMaquinaList.add(
                                    new UpgradeMaquina(
                                            StackCore.getUpgradeManager(),
                                            u,
                                            1
                                    )
                            );
                        }
                        MaquinaPlayer mp = new MaquinaPlayer(
                                StackCore.getMaquinaPlayerManager(),
                                MaquinaPlayer.randomID(50),
                                m,
                                new BigInteger(maquinaAmount),
                                owner,
                                new BigInteger("0"),
                                System.currentTimeMillis()+(m.getOption().getDelayWave()*1000),
                                upgradeMaquinaList,
                                false
                        );
                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(owner);
                        if(pm==null) {
                            maqToReceive.add(mp);
                            new PlayerMaquina(this,owner,lista,maqToReceive,new BigInteger(cfg.getString(s+".limite")));
                        } else {
                            pm.addMaquinaToReceive(mp);
                        }
                        return;
                    }
                    PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(owner);
                    if(pm==null) {
                        new PlayerMaquina(this,owner,lista,maqToReceive,new BigInteger(cfg.getString(s+".limite")));
                    }
                }
            });
        }
    }

    public void deserialise() throws IOException {
        if(StackCore.getInstance().usingMySQL) {
            if(!playerMaquinaSet.isEmpty()) {
                playerMaquinaSet.forEach(MySQL::updatePlayer);
            }
        } else {
            if (!playerMaquinaSet.isEmpty()) {
                playersFile.set("players", null);
                playersFile.save(StackCore.getInstance().getPlFile());
                playerMaquinaSet.forEach(PlayerMaquina -> {
                    playersFile.set("players." + PlayerMaquina.getPlayer() + ".limite", PlayerMaquina.getLimite());
                    List<String> maquinasToReceive = new ArrayList<>();
                    for(MaquinaPlayer mp : PlayerMaquina.getMaquinasToReceive()) {
                        maquinasToReceive.add(mp.getMaquina().getKey() + ";" + mp.getMaquinaAmount());
                    }
                    playersFile.set("players." + PlayerMaquina.getPlayer() + ".maquinas-to-receive", maquinasToReceive);
                });
                playersFile.save(StackCore.getInstance().getPlFile());
                return;
            }
            playersFile.set("players", null);
            playersFile.save(StackCore.getInstance().getPlFile());
        }
    }

}
