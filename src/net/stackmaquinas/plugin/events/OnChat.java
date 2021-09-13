package net.stackmaquinas.plugin.events;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.utils.Desconto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class OnChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(!StackCore.buyingMaquina.containsKey(p)) {
            return;
        }
        e.setCancelled(true);
        if(e.getMessage().contains("cancelar")) {
            StackCore.buyingMaquina.remove(p);
            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("processo-cancelado");
            for(String s : msgLore) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',s)
                );
            }
            return;
        }

        String regex = "\\d+";
        if(!e.getMessage().matches(regex)) {
            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("quantia-incorreta");
            for(String s : msgLore) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',s)
                );
            }
            return;
        }

        int quantia = Integer.parseInt(e.getMessage());
        if(quantia < 1) {
            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("quantia-incorreta");
            for(String s : msgLore) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',s)
                );
            }
            return;
        }

        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());

        if(quantia > pm.getLimite().doubleValue()) {
            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maior-que-limite");
            for(String s : msgLore) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',s).replaceAll("@limite",String.valueOf(pm.getLimite()))
                );
            }
            return;
        }

        Maquina m = StackCore.buyingMaquina.get(p);
        int desconto = Desconto.getDesconto(p);
        double machinePrice = m.getOption().getMachinePrice()*quantia;
        double priceToRemove = machinePrice/100*desconto;
        double price = m.getOption().getMachinePrice()*quantia;

        if(desconto != 0) {
            if(StackCore.getEconomy().has(p,price-priceToRemove)) {
                StackCore.getEconomy().withdrawPlayer(p,price-priceToRemove);
            } else {
                List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("dinheiro-insuficiente");
                for(String s : msgLore) {
                    p.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',s)
                    );
                }
                return;
            }
        } else {
            if(StackCore.getEconomy().has(p,price)) {
                StackCore.getEconomy().withdrawPlayer(p,price);
            } else {
                List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("dinheiro-insuficiente");
                for(String s : msgLore) {
                    p.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',s)
                    );
                }
                return;
            }
        }

        if(pm.getPlayerMaquinas().size() == 0) {
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
                    new BigInteger(e.getMessage()),
                    p.getUniqueId(),
                    new BigInteger("0"),
                    m.getOption().getDelayWave(),
                    upgradeMaquinaList,
                    true
            );
            pm.addMaquina(mp);
            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-comprada");
            for(String s : msgLore) {
                p.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',s).replaceAll("@quantia",String.valueOf(quantia)).replaceAll("@nome_maquina",ChatColor.translateAlternateColorCodes('&',m.getName()))
                );
            }
            StackCore.buyingMaquina.remove(p);
            return;
        }

        for(MaquinaPlayer mp : pm.getPlayerMaquinas()) {
            if(mp.getMaquina().equals(StackCore.buyingMaquina.get(p))) {
                mp.setMaquinaAmount(mp.getMaquinaAmount().add(new BigInteger(e.getMessage())));
                List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-comprada");
                for(String s : msgLore) {
                    p.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',s).replaceAll("@quantia",String.valueOf(quantia)).replaceAll("@nome_maquina",ChatColor.translateAlternateColorCodes('&',m.getName()))
                    );
                }
                StackCore.buyingMaquina.remove(p);
                return;
            }
        }

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
                new BigInteger(e.getMessage()),
                p.getUniqueId(),
                new BigInteger("0"),
                m.getOption().getDelayWave(),
                upgradeMaquinaList,
                true
        );
        pm.addMaquina(mp);
        List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-comprada");
        for(String s : msgLore) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',s).replaceAll("@quantia",String.valueOf(quantia)).replaceAll("@nome_maquina",ChatColor.translateAlternateColorCodes('&',m.getName()))
            );
        }
        StackCore.buyingMaquina.remove(p);
    }

}
