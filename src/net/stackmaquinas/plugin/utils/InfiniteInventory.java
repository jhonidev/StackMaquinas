package net.stackmaquinas.plugin.utils;

import net.stackmaquinas.plugin.StackCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InfiniteInventory {

    private Inventory inv;
    public int currentPage = 0;
    public int totalPages = 0;
    public static HashMap<UUID, InfiniteInventory> users = new HashMap<>();
    public List<Inventory> pages = new ArrayList<>();

    public InfiniteInventory(List<ItemStack> items, Player p, String name, int rows, int firstItemSlot, int lastItemSlot, int slotPreviousButton, int slotNextButton) {
        String rightName = name.replaceAll("&","§");
        inv = Bukkit.createInventory(null,9*rows,rightName + " - Pag. 1");
        List<String> gobackLore = new ArrayList<>();
        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("voltar-item.lore")) {
            gobackLore.add(ChatColor.translateAlternateColorCodes('&',xyz));
        }
        ItemStack goback = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("voltar-item.material"))
        ).setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("voltar-item.nome")))
                .setLore(gobackLore).toItemStack();
        inv.setItem(27,goback);
        int index = firstItemSlot;
        int totalItemsPerInventory = lastItemSlot-firstItemSlot+1;
        int totalItens = 0;
        int finalItemAmount = items.size();
        totalPages = finalItemAmount/totalItemsPerInventory;
        List<ItemStack> itemsToRemove = new ArrayList<>();
        if(items.size() > totalItemsPerInventory) {
            if(currentPage == 0) {
                ItemStack next = new ItemBuilder(
                        Material.INK_SACK, 1)
                        .setName("§aPróxima página")
                        .setLore("")
                        .setDyeColor(DyeColor.PURPLE)
                        .toItemStack();
                inv.setItem(slotNextButton,next);
            }
            for(ItemStack is : items) {
                finalItemAmount++;
                if(totalItens == totalItemsPerInventory) {
                    pages.add(inv);
                    if(pages.size() == (int)totalPages)  {
                        inv = createInv(rows, rightName+" - Pag. " + Integer.valueOf(pages.size()+1), slotPreviousButton, slotNextButton, true, false);
                    } else {
                        inv = createInv(rows, rightName+" - Pag. " + Integer.valueOf(pages.size()+1), slotPreviousButton, slotNextButton, true, true);
                    }
                    index=firstItemSlot;
                    inv.setItem(index,is);
                    index++;
                    totalItens=1;
                } else {
                    inv.setItem(index,is);
                    index++;
                    totalItens++;
                }
                itemsToRemove.remove(is);
            }
            for(ItemStack is : itemsToRemove) {
                items.remove(is);
            }
        } else {
            for (ItemStack is : items) {
                inv.setItem(index, is);
                index++;
            }
        }
        pages.add(inv);
        p.openInventory(pages.get(currentPage));
        users.put(p.getUniqueId(), this);
    }


    public Inventory createInv(int rows, String name, int slotPreviousButton, int slotNextButton, boolean prevButton, boolean nextButton) {
        String rightName = name.replaceAll("&","§");
        Inventory inv = Bukkit.createInventory(null, 9 * rows, rightName);;

        ItemStack previous = new ItemBuilder(
                Material.INK_SACK, 1)
                .setName("§cPágina anterior")
                .setLore("")
                .setDyeColor(DyeColor.ORANGE)
                .toItemStack();
        ItemStack next = new ItemBuilder(
                Material.INK_SACK, 1)
                .setName("§aPróxima página")
                .setLore("")
                .setDyeColor(DyeColor.PURPLE)
                .toItemStack();
        List<String> gobackLore = new ArrayList<>();
        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("voltar-item.lore")) {
            gobackLore.add(ChatColor.translateAlternateColorCodes('&',xyz));
        }
        ItemStack goback = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("voltar-item.material"))
        ).setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("voltar-item.nome")))
                .setLore(gobackLore).toItemStack();

        if(prevButton) { inv.setItem(slotPreviousButton,previous); }
        if(nextButton) { inv.setItem(slotNextButton,next); }
        inv.setItem(27,goback);
        return inv;
    }

}