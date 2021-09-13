package net.stackmaquinas.plugin.events;

import me.caneca.spookycash.Main;
import me.caneca.spookycash.plugin.Cash;
import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.menus.*;
import net.stackmaquinas.plugin.objetos.ItemDrop;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.utils.Desconto;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String gobackItem = ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("voltar-item.nome"));
        if (e.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.menu-principal")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-principal.gerenciar-maquinas.nome"))
            )) {
                MenuGerenciar.openGerenciarMenu(p);
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-principal.lista-maquinas.nome"))
            )) {
                ListaMaquinas.openListaMenu(p);
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-principal.top-maquinas.nome"))
            )) {
                TopMaquinas.openTopMenu(p);
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-principal.recolher-maquinas.nome"))
            )) {
                RecolherMaquinas.openRecolherMenu(p);
                return;
            }
        }
        if (e.getInventory().getName().contains(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.recolher-maquinas")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(gobackItem)) {
                MenuPrincipal.openInventory(p);
                return;
            }
            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
            List<MaquinaPlayer> maquinasToRemove = new ArrayList<>();
            List<MaquinaPlayer> maquinasToAdd = new ArrayList<>();
            for (MaquinaPlayer mp : pm.getMaquinasToReceive()) {
                String name = ChatColor.translateAlternateColorCodes('&', mp.getMaquina().getName());
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
                    if (pm.getPlayerMaquinas().size() == 0) {
                        MaquinaPlayer maquina = new MaquinaPlayer(StackCore.getMaquinaPlayerManager(),
                                mp.getId(),
                                mp.getMaquina(),
                                mp.getMaquinaAmount(),
                                mp.getOwner(),
                                mp.getTotalDrops(),
                                System.currentTimeMillis() + (mp.getMaquina().getOption().getDelayWave()*1000),
                                mp.getUpgradeMaquinaList(),
                                true);
                        pm.addMaquina(maquina);
                        pm.removeMaquinaToReceive(mp);
                        RecolherMaquinas.openRecolherMenu(p);
                        return;
                    }
                    boolean update = false;
                    MaquinaPlayer mpUpdate = null;
                    BigInteger amountUpdate = null;
                    for (MaquinaPlayer mpx : pm.getPlayerMaquinas()) {
                        if (mpx.getMaquina().getKey().equalsIgnoreCase(mp.getMaquina().getKey())) {
                            update = true;
                            mpUpdate = mpx;
                            amountUpdate = mpx.getMaquinaAmount().add(mp.getMaquinaAmount());
                            maquinasToRemove.add(mp);
                        }
                    }
                    if(update) {
                        mpUpdate.setMaquinaAmount(amountUpdate);
                    } else {
                        MaquinaPlayer maquina = new MaquinaPlayer(StackCore.getMaquinaPlayerManager(),
                                mp.getId(),
                                mp.getMaquina(),
                                mp.getMaquinaAmount(),
                                mp.getOwner(),
                                mp.getTotalDrops(),
                                System.currentTimeMillis() + (mp.getMaquina().getOption().getDelayWave()*1000),
                                mp.getUpgradeMaquinaList(),
                                true);
                        pm.addMaquina(maquina);
                        maquinasToRemove.add(mp);
                    }
                }
            }
            for(MaquinaPlayer mp : maquinasToRemove) {
                pm.removeMaquinaToReceive(mp);
            }
            RecolherMaquinas.openRecolherMenu(p);
        }
        if (e.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.top-maquinas")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(gobackItem)) {
                MenuPrincipal.openInventory(p);
                return;
            }
        }
        if (e.getInventory().getName().contains(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.gerenciar-maquinas")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(gobackItem)) {
                MenuPrincipal.openInventory(p);
                return;
            }
            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
            if (pm != null) {
                for (MaquinaPlayer mp : pm.getPlayerMaquinas()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', mp.getMaquina().getName()))) {
                        GerenciarMaquina.openGerenciarMenu(p, mp);
                        StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId()).setGerenciando(mp);
                        return;
                    }
                }
            }
        }
        if (e.getInventory().getName().contains(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.lista-maquinas")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(gobackItem)) {
                MenuPrincipal.openInventory(p);
                return;
            }
            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
            for (Maquina m : StackCore.getMaquinaManager().getMaquinaSet()) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', m.getName()))) {
                    List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("iniciando-compra");
                    for (String s : msgLore) {
                        p.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', s).replaceAll("@limite", String.valueOf(pm.getLimite())).replaceAll("@desconto", Desconto.getDesconto(p) + "%")
                        );
                    }
                    p.closeInventory();
                    if (!StackCore.buyingMaquina.containsKey(p)) {
                        StackCore.buyingMaquina.put(p, m);
                    }
                    return;
                }
            }
        }
        if (e.getInventory().getName().contains(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.lista-drops")))) {
            e.setCancelled(true);
            return;
        }
        if (e.getInventory().getName().contains(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.gerenciando-maquina")))) {
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            e.setCancelled(true);
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(gobackItem)) {
                MenuGerenciar.openGerenciarMenu(p);
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.recolher-drop.nome"))
            )) {

                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                MaquinaPlayer mp = pm.getGerenciando();
                Maquina m = mp.getMaquina();

                if(mp.getTotalDrops().doubleValue() > 0) {

                    if(m.getDrop().isGivingCommands()) {
                        for (String s : m.getDrop().getCommandsList()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("@player", p.getName()).replaceAll("@quantia", String.valueOf(mp.getTotalDrops().doubleValue())));
                        }
                    }

                    if(m.getDrop().isGivingItems()) {
                        double totalMoney = 0;
                        double totalCash = 0;
                        for(ItemDrop id : m.getDrop().getItemStackList()) {
                            if(id.getPagamento().equalsIgnoreCase("money")) {
                                totalMoney += id.getValor();
                            }
                            if(id.getPagamento().equalsIgnoreCase("cash")) {
                                totalCash += id.getValor();
                            }
                        }
                        totalMoney = totalMoney*mp.getTotalDrops().doubleValue();
                        totalCash = totalCash*mp.getTotalDrops().doubleValue();
                        if(totalMoney > 0) {
                            StackCore.getEconomy().depositPlayer(p,totalMoney);
                        }
                        if(totalCash > 0) {
                            Main.getInstance().setCash(p.getName(), Main.instance.getCash(p.getName())+totalCash);
                        }
                    }
                    mp.setTotalDrops(new BigInteger("0"));
                    GerenciarMaquina.openGerenciarMenu(p, mp);
                }

            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.lista-drops.nome"))
            )) {

                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                MaquinaPlayer mp = pm.getGerenciando();
                Maquina m = mp.getMaquina();

                ListaDrops.openListaDrops(p, m);

            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-velocidade.nome"))
            )) {
                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                MaquinaPlayer mp = pm.getGerenciando();
                for(UpgradeMaquina um : mp.getUpgradeMaquinaList()) {
                    double upgradePriceVelocidade = 0;
                    if(um.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("velocidade")) {
                        int maxLevel = um.getUpgrade().getMaxLevel();
                        int currentLevel = um.getCurrentLevel();
                        if(currentLevel >= maxLevel) {
                            return;
                        }
                        double percentage = um.getUpgrade().getCostGrowPercentage(); // 10.0
                        double final_value = um.getUpgrade().getStartCost(); // 100.0
                        for(int i=0;i < um.getCurrentLevel(); i++) {
                            double startCost = um.getUpgrade().getStartCost();
                            double valueToAdd = um.getUpgrade().getStartCost()/100*percentage;
                            if(i==0){final_value=final_value+valueToAdd+startCost; } else { final_value=final_value+valueToAdd; }
                        }
                        upgradePriceVelocidade=final_value;
                        if(um.getUpgrade().getPayment().equalsIgnoreCase("Money")) {
                            if(StackCore.getEconomy().has(p,upgradePriceVelocidade)) {
                                StackCore.getEconomy().withdrawPlayer(p,upgradePriceVelocidade);
                                um.setCurrentLevel(um.getCurrentLevel()+1);
                                GerenciarMaquina.openGerenciarMenu(p,mp);
                                return;
                            }
                        }
                        if(um.getUpgrade().getPayment().equalsIgnoreCase("cash")) {
                            // faz as coisas com o
                            if(Main.getInstance().getCash(p.getName()) >= upgradePriceVelocidade) {
                                Main.getInstance().setCash(p.getName(), Main.getInstance().getCash(p.getName())-upgradePriceVelocidade);
                                um.setCurrentLevel(um.getCurrentLevel()+1);
                                GerenciarMaquina.openGerenciarMenu(p,mp);
                                return;
                            }
                        }
                    }
                }
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                    ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-quantidade.nome"))
            )) {
                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                MaquinaPlayer mp = pm.getGerenciando();
                for(UpgradeMaquina um : mp.getUpgradeMaquinaList()) {
                    double upgradePriceQuantidade = 0;
                    if(um.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("quantidade")) {
                        int maxLevel = um.getUpgrade().getMaxLevel();
                        int currentLevel = um.getCurrentLevel();
                        if(currentLevel >= maxLevel) {
                            return;
                        }
                        double percentage = um.getUpgrade().getCostGrowPercentage(); // 10.0
                        double final_value = um.getUpgrade().getStartCost(); // 100.0
                        for(int i=0;i < um.getCurrentLevel(); i++) {
                            double startCost = um.getUpgrade().getStartCost();
                            double valueToAdd = um.getUpgrade().getStartCost()/100*percentage;
                            if(i==0){final_value=final_value+valueToAdd+startCost; } else { final_value=final_value+valueToAdd; }
                        }
                        upgradePriceQuantidade=final_value;
                        if(um.getUpgrade().getPayment().equalsIgnoreCase("Money")) {
                            if(StackCore.getEconomy().has(p,upgradePriceQuantidade)) {
                                StackCore.getEconomy().withdrawPlayer(p,upgradePriceQuantidade);
                                um.setCurrentLevel(um.getCurrentLevel()+1);
                                GerenciarMaquina.openGerenciarMenu(p,mp);
                                return;
                            }
                        }
                        if(um.getUpgrade().getPayment().equalsIgnoreCase("cash")) {
                            if(Main.getInstance().getCash(p.getName()) >= upgradePriceQuantidade) {
                                Main.getInstance().setCash(p.getName(), Main.getInstance().getCash(p.getName())-upgradePriceQuantidade);
                                um.setCurrentLevel(um.getCurrentLevel()+1);
                                GerenciarMaquina.openGerenciarMenu(p,mp);
                                return;
                            }
                        }
                    }
                }
            }
            if (e.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.top-maquinas")))) {
                if (e.getCurrentItem() == null) {
                    return;
                }
                if (e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }
                e.setCancelled(true);
                if (!e.getCurrentItem().hasItemMeta()) {
                    return;
                }
                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                List<MaquinaPlayer> maquinasToRemove = new ArrayList<>();
                for (MaquinaPlayer mp : pm.getMaquinasToReceive()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(
                            ChatColor.translateAlternateColorCodes('&', mp.getMaquina().getName())
                    )) {

                        for (MaquinaPlayer mpx : pm.getPlayerMaquinas()) {
                            if (mpx.getMaquina().getKey().equalsIgnoreCase(mp.getMaquina().getKey())) {
                                mpx.setMaquinaAmount(mpx.getMaquinaAmount().add(mp.getMaquinaAmount()));
                                maquinasToRemove.add(mp);
                                List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-adicionada");
                                for (String s : msgLore) {
                                    p.sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', s).replaceAll("@quantia", String.valueOf(mp.getMaquinaAmount()))
                                    );
                                }
                            }
                        }

                    }
                }
                for (MaquinaPlayer mp : maquinasToRemove) {
                    pm.removeMaquinaToReceive(mp);
                }
            }
        }
    }
}
