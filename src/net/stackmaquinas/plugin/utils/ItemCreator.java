package net.stackmaquinas.plugin.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ItemCreator {

    public static ItemStack setSkullMeta(ItemStack is, String nome, ArrayList<String> lore) {
        ItemMeta im = (SkullMeta) is.getItemMeta();
        im.setDisplayName(nome);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

}
