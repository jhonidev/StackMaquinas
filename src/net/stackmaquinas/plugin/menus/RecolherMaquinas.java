package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecolherMaquinas {

    public static void openRecolherMenu(Player p) {
        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
        if(pm!=null) {
            int size = pm.getMaquinasToReceive().size();
            List<ItemStack> itemsToAdd = new ArrayList<>();
            for(MaquinaPlayer mp : pm.getMaquinasToReceive()) {

                List<String> lore = new ArrayList<>();
                List<String> loreMachine = mp.getMaquina().getLore();
                for(String xyz : StackCore.getInstance().getMenusYml().getStringList("recolher-maquinas.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',xyz));
                }
                List<String> rightLore = new ArrayList<>();
                lore.forEach(line -> {
                    if(line.equalsIgnoreCase("@lore_maquina")) {
                        for(String s : loreMachine) {
                            rightLore.add(ChatColor.translateAlternateColorCodes('&',s).replaceAll("@quantia",String.valueOf(mp.getMaquinaAmount())));
                        }
                    } else {
                        rightLore.add(line.replaceAll("@quantia",String.valueOf(mp.getMaquinaAmount())));
                    }
                });

                ItemStack is = new ItemBuilder(Material.getMaterial(mp.getMaquina().getBlock())
                ).setName(ChatColor.translateAlternateColorCodes('&',mp.getMaquina().getName())).setLore(rightLore).toItemStack();
                itemsToAdd.add(is);

            }

            new InfiniteInventory(itemsToAdd,p,
                    ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.recolher-maquinas"))
                    ,StackCore.getInstance().getMenusYml().getInt("rows.recolher-maquinas"),11,15,9,17);
            return;

        }
    }

}
