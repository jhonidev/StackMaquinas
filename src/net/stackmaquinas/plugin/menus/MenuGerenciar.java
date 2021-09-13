package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuGerenciar {

    public static void openGerenciarMenu(Player p) {
        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
        List<ItemStack> itemsToAdd = new ArrayList<>();
        if(pm!=null) {
            List<MaquinaPlayer> mp = pm.getPlayerMaquinas();
            if(mp!=null) {
                for(MaquinaPlayer maquina : mp) {
                    String machineName = ChatColor.translateAlternateColorCodes('&',
                            StackCore.getInstance().getMenusYml().getString("gerenciar-maquinas.nome").replaceAll("@nome_maquina",maquina.getMaquina().getName()));
                    List<String> lore = new ArrayList<>();
                    List<String> loreMachine = maquina.getMaquina().getLore();
                    for(String xyz : StackCore.getInstance().getMenusYml().getStringList("gerenciar-maquinas.lore")) {
                        lore.add(ChatColor.translateAlternateColorCodes('&',xyz.replaceAll("@quantidade", FormatMoney.format(maquina.getMaquinaAmount().doubleValue(), true))));
                    }
                    List<String> rightLore = new ArrayList<>();
                    lore.forEach(line -> {
                        if(line.equalsIgnoreCase("@lore_maquina")) {
                            for(String s : loreMachine) {
                                rightLore.add(ChatColor.translateAlternateColorCodes('&',s));
                            }
                        } else {
                            rightLore.add(line);
                        }
                    });
                    ItemStack is = new ItemBuilder(
                            Material.getMaterial(maquina.getMaquina().getBlock()), 1)
                            .setName(machineName)
                            .setLore(rightLore)
                            .toItemStack();
                    itemsToAdd.add(is);
                }
                new InfiniteInventory(itemsToAdd,p,
                        ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.gerenciar-maquinas"))
                        ,StackCore.getInstance().getMenusYml().getInt("rows.gerenciar-maquinas"),11,15,9,17);
            }
        }
    }

}
