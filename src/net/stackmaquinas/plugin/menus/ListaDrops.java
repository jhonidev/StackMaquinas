package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.ItemDrop;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.utils.Desconto;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ListaDrops {

    public static void openListaDrops(Player p, Maquina m) {
        List<ItemStack> itemsToAdd = new ArrayList<>();
        List<ItemDrop> id = m.getDrop().getItemStackList();

        if(m.getDrop().isGivingCommands()) {
            List<String> lore = StackCore.getInstance().getConfig().getStringList("maquinas." + m.getKey() + ".drop.papel-comandos");
            List<String> rightLore = new ArrayList<>();
            lore.forEach(s -> {
                if(s.equalsIgnoreCase("@lista_comandos")) {
                    rightLore.addAll(m.getDrop().getCommandsList());
                } else {
                    rightLore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            });

            ItemStack is = new ItemBuilder(
                    Material.PAPER, 1)
                    .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("papel-comandos.nome")))
                    .setLore(rightLore)
                    .toItemStack();
            itemsToAdd.add(is);
        }

        if(id.size() > 0) {
            for(ItemDrop is : id) {
                ItemStack item = is.getItem();
                itemsToAdd.add(item);
            }
        }

        new InfiniteInventory(itemsToAdd,p,
                ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.lista-drops"))
                ,StackCore.getInstance().getMenusYml().getInt("rows.lista-drops"),11,15,9,17);
    }

}
