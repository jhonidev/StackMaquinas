package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.utils.Desconto;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ListaMaquinas {

    public static void openListaMenu(Player p) {
        List<ItemStack> itemsToAdd = new ArrayList<>();
        int size = StackCore.getMaquinaManager().getMaquinaSet().size();
        for(int i = 1; i < size + 1; i++) {
            Maquina m = StackCore.getMaquinaManager().getMaquina(i);
            if(m!=null) {
                List<String> lore = new ArrayList<>();
                List<String> rightLore = new ArrayList<>();
                if(m.needPermToBuy()) {
                    if(p.hasPermission(m.getPermisison())) {
                        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("lista-maquinas.lore-com-perm")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&',xyz).replaceAll("@preco", FormatMoney.format(m.getOption().getMachinePrice(), false)).replaceAll("@desconto", String.valueOf(Desconto.getDesconto(p))+"%"));
                        }
                    } else {
                        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("lista-maquinas.lore-sem-perm")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&',xyz).replaceAll("@preco", FormatMoney.format(m.getOption().getMachinePrice(), false)).replaceAll("@desconto", String.valueOf(Desconto.getDesconto(p))+"%"));
                        }
                    }
                }
                lore.forEach(s -> {
                    if(s.equalsIgnoreCase("@lore_maquina")) {
                        for(String xyz : m.getLore()) {
                            rightLore.add(ChatColor.translateAlternateColorCodes('&',xyz));
                        }
                    } else {
                        rightLore.add(s);
                    }
                });
                ItemStack is = new ItemBuilder(
                        Material.getMaterial(m.getBlock()), 1)
                        .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("lista-maquinas.nome").replaceAll("@nome_maquina",ChatColor.translateAlternateColorCodes('&',m.getName()))))
                        .setLore(rightLore)
                        .toItemStack();
                itemsToAdd.add(is);
            }
        }
        new InfiniteInventory(itemsToAdd,p,
                ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.lista-maquinas"))
                ,StackCore.getInstance().getMenusYml().getInt("rows.lista-maquinas"),11,15,9,17);
    }

}
