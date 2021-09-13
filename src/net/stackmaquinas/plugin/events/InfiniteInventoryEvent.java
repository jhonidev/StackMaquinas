package net.stackmaquinas.plugin.events;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.utils.InfiniteInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InfiniteInventoryEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if(!InfiniteInventory.users.containsKey(p.getUniqueId())) return;
        InfiniteInventory inv = InfiniteInventory.users.get(p.getUniqueId());
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getItemMeta() == null) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("proxima-pagina")))) {
            e.setCancelled(true);
            if(inv.currentPage >= inv.pages.size()-1){
                return;
            }
            inv.currentPage += 1;
            p.openInventory(inv.pages.get(inv.currentPage));
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getMenusYml().getString("pagina-anterior")))) {
            e.setCancelled(true);
            if(inv.currentPage > 0){
                inv.currentPage -= 1;
                p.openInventory(inv.pages.get(inv.currentPage));
                return;
            }
        }
    }

}
