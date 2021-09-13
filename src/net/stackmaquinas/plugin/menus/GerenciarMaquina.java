package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GerenciarMaquina {

    public static void openGerenciarMenu(Player p, MaquinaPlayer mp) {
        Inventory gerenciarinv = Bukkit.createInventory(null,StackCore.getInstance().getMenusYml().getInt("rows.gerenciando-maquina")*9,
                ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("nome-dos-menus.gerenciando-maquina")));
        int upgradeAmountQuantidade = 0;
        int upgradeAmountVelocidade = 0;
        int maxLevelVelocidade = 0;
        int maxLevelQuantidade = 0;
        double upgradePriceVelocidade = 0;
        double upgradePriceQuantidade = 0;
        String upgradePaymentQuantidade = null;
        String upgradePaymentVelocidade = null;
        for(UpgradeMaquina u : mp.getUpgradeMaquinaList()) {
            if(u.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("quantidade")) {
                maxLevelQuantidade = u.getUpgrade().getMaxLevel();
                upgradeAmountQuantidade = u.getCurrentLevel();
                double percentage = u.getUpgrade().getCostGrowPercentage();
                double final_value = 0;
                for(int i=0;i < u.getCurrentLevel(); i++) {
                    double startCost = u.getUpgrade().getStartCost();
                    double valueToAdd = u.getUpgrade().getStartCost()/100*percentage;
                    if(i==0){final_value=final_value+valueToAdd+startCost; } else { final_value=final_value+valueToAdd; }
                }
                upgradePriceQuantidade=final_value;
                if(u.getUpgrade().getPayment().equalsIgnoreCase("money")) {
                    upgradePaymentQuantidade = "Money";
                }
                if(u.getUpgrade().getPayment().equalsIgnoreCase("cash")) {
                    upgradePaymentQuantidade = "Cash";
                }
            }
            if(u.getUpgrade().getUpgradeType().getName().equalsIgnoreCase("velocidade")) {
                maxLevelVelocidade = u.getUpgrade().getMaxLevel();
                upgradeAmountVelocidade = u.getCurrentLevel();
                double percentage = u.getUpgrade().getCostGrowPercentage(); // 10.0
                double final_value = u.getUpgrade().getStartCost(); // 100.0
                for(int i=0;i < u.getCurrentLevel(); i++) {
                    double startCost = u.getUpgrade().getStartCost();
                    double valueToAdd = u.getUpgrade().getStartCost()/100*percentage;
                    if(i==0){final_value=final_value+valueToAdd+startCost; } else { final_value=final_value+valueToAdd; }
                }
                upgradePriceVelocidade=final_value;
                if(u.getUpgrade().getPayment().equalsIgnoreCase("money")) {
                    upgradePaymentVelocidade = "Money";
                }
                if(u.getUpgrade().getPayment().equalsIgnoreCase("cash")) {
                    upgradePaymentVelocidade = "Cash";
                }
            }
        }
        List<String> loreInfos = new ArrayList<>();
        List<String> loreRecolher = new ArrayList<>();
        List<String> loreLista = new ArrayList<>();
        List<String> loreVelocidade = new ArrayList<>();
        List<String> loreQuantidade = new ArrayList<>();
        int multiplicador = mp.getMaquina().getOption().getStartDrop()*upgradeAmountQuantidade;
        BigInteger multiply = BigInteger.valueOf(multiplicador);
        for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.informacoes.lore")) {
            loreInfos.add(ChatColor.translateAlternateColorCodes('&',s)
            .replaceAll("@drop",mp.getMaquina().getDrop().getDropName())
            .replaceAll("@delay_segundos",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getDelayWave() - upgradeAmountVelocidade+1)))
            .replaceAll("@quantia_drop", FormatMoney.format(mp.getMaquinaAmount().multiply(multiply).doubleValue(), true))
            .replaceAll("@nivel_velocidade",String.valueOf(upgradeAmountVelocidade))
            .replaceAll("@nivel_quantidade",String.valueOf(upgradeAmountQuantidade))
            .replaceAll("@maquina_quantidade",FormatMoney.format(mp.getMaquinaAmount().doubleValue(), true)));
        }
        for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.recolher-drop.lore")) {
            loreRecolher.add(ChatColor.translateAlternateColorCodes('&',s)
                    .replaceAll("@drop",mp.getMaquina().getDrop().getDropName())
                    .replaceAll("@total",FormatMoney.format(mp.getTotalDrops().doubleValue(), true)));
        }
        for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.lista-drops.lore")) {
            loreLista.add(ChatColor.translateAlternateColorCodes('&',s));
        }
        if(upgradeAmountVelocidade >= maxLevelVelocidade) {
            for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.upgrade-velocidade.lore-nivel-maximo")) {
                loreVelocidade.add(ChatColor.translateAlternateColorCodes('&',s)
                        .replaceAll("@atual",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getDelayWave()-upgradeAmountVelocidade*
                                StackCore.getInstance().getConfig().getInt("maquinas."+mp.getMaquina().getKey() + ".opcoes.upgrades.upgrade-velocidade.segundos-removidos")+1))));
            }
        } else {
            for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.upgrade-velocidade.lore")) {
                loreVelocidade.add(ChatColor.translateAlternateColorCodes('&',s)
                        .replaceAll("@atual",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getDelayWave()-upgradeAmountVelocidade*
                                StackCore.getInstance().getConfig().getInt("maquinas."+mp.getMaquina().getKey() + ".opcoes.upgrades.upgrade-velocidade.segundos-removidos")+1)))
                        .replaceAll("@apos_evolucao",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getDelayWave()-upgradeAmountVelocidade)))
                        .replaceAll("@custo", FormatMoney.format(upgradePriceVelocidade, false))
                        .replaceAll("@forma_pagamento",upgradePaymentVelocidade));
            }
        }
        if(upgradeAmountQuantidade >= maxLevelQuantidade) {
            for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.upgrade-quantidade.lore-nivel-maximo")) {
                loreQuantidade.add(ChatColor.translateAlternateColorCodes('&',s)
                        .replaceAll("@atual",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getStartDrop()*upgradeAmountQuantidade))));
            }
        } else {
            for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-gerenciando.upgrade-quantidade.lore")) {
                loreQuantidade.add(ChatColor.translateAlternateColorCodes('&',s)
                        .replaceAll("@atual",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getStartDrop()*upgradeAmountQuantidade)))
                        .replaceAll("@apos_evolucao",String.valueOf(Integer.valueOf(mp.getMaquina().getOption().getStartDrop()*upgradeAmountQuantidade*
                                StackCore.getInstance().getConfig().getInt("maquinas."+mp.getMaquina().getKey() + ".opcoes.upgrades.upgrade-quantidade.aumenta-quantidade")+1)))
                        .replaceAll("@custo", FormatMoney.format(upgradePriceQuantidade, false))
                        .replaceAll("@forma_pagamento",upgradePaymentQuantidade));
            }
        }
        ItemStack infos = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-gerenciando.informacoes.material")), 1).setName(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.informacoes.nome")))
                .setLore(loreInfos
                )
                .toItemStack();
        ItemStack recolher = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-gerenciando.recolher-drop.material")), 1).setName(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.recolher-drop.nome")))
                .setLore(loreRecolher)
                .toItemStack();
        ItemStack drops = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-gerenciando.lista-drops.material")), 1).setName(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.lista-drops.nome")))
                .setLore(loreLista)
                .toItemStack();
        ItemStack nivel_speed = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-velocidade.material")), 1).setName(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-velocidade.nome")))
                .setLore(loreVelocidade)
                .toItemStack();
        ItemStack nivel_drop = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-quantidade.material")), 1).setName(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("menu-gerenciando.upgrade-quantidade.nome")))
                .setLore(loreQuantidade)
                .toItemStack();
        List<String> gobackLore = new ArrayList<>();
        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("voltar-item.lore")) {
            gobackLore.add(ChatColor.translateAlternateColorCodes('&',xyz));
        }
        ItemStack goback = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("voltar-item.material"))
        ).setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("voltar-item.nome")))
                .setLore(gobackLore).toItemStack();
        gerenciarinv.setItem(StackCore.getInstance().getMenusYml().getInt("menu-gerenciando.informacoes.slot"),infos);
        gerenciarinv.setItem(StackCore.getInstance().getMenusYml().getInt("menu-gerenciando.recolher-drop.slot"),recolher);
        gerenciarinv.setItem(StackCore.getInstance().getMenusYml().getInt("menu-gerenciando.lista-drops.slot"),drops);
        gerenciarinv.setItem(StackCore.getInstance().getMenusYml().getInt("menu-gerenciando.upgrade-velocidade.slot"),nivel_speed);
        gerenciarinv.setItem(StackCore.getInstance().getMenusYml().getInt("menu-gerenciando.upgrade-quantidade.slot"),nivel_drop);
        gerenciarinv.setItem(27,goback);
        p.openInventory(gerenciarinv);
    }

}
