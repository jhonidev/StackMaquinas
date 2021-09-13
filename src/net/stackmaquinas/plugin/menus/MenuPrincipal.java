package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.utils.FormatMoney;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import net.stackmaquinas.plugin.utils.ItemCreator;
import net.stackmaquinas.plugin.utils.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
public class MenuPrincipal {

    static Inventory menuPrincipal = Bukkit.createInventory(null, StackCore.getInstance().getMenusYml().getInt("rows.menu-principal")*9,
            ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.menu-principal"))
            );

    public static void setupInventory() {
        List<String> loreGerenciar = new ArrayList<>();
        List<String> loreTop = new ArrayList<>();
        List<String> loreLista = new ArrayList<>();
        List<String> loreRecolher = new ArrayList<>();
        for(String x : StackCore.getInstance().getMenusYml().getStringList("menu-principal.gerenciar-maquinas.lore")) {
            loreGerenciar.add(ChatColor.translateAlternateColorCodes('&',x));
        }
        for(String x : StackCore.getInstance().getMenusYml().getStringList("menu-principal.top-maquinas.lore")) {
            loreTop.add(ChatColor.translateAlternateColorCodes('&',x));
        }
        for(String x : StackCore.getInstance().getMenusYml().getStringList("menu-principal.lista-maquinas.lore")) {
            loreLista.add(ChatColor.translateAlternateColorCodes('&',x));
        }
        for(String x : StackCore.getInstance().getMenusYml().getStringList("menu-principal.recolher-maquinas.lore")) {
            loreRecolher.add(ChatColor.translateAlternateColorCodes('&',x));
        }
        ItemStack recolher = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-principal.recolher-maquinas.material")), 0)
                .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.recolher-maquinas.nome")))
                .setLore(loreRecolher)
                .toItemStack();
        ItemStack gerenciar = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-principal.gerenciar-maquinas.material")), 1)
                .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.gerenciar-maquinas.nome")))
                .setLore(loreGerenciar)
                .toItemStack();
        ItemStack topMaquinas = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-principal.top-maquinas.material")), 1)
                .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.top-maquinas.nome")))
                .setLore(loreTop)
                .toItemStack();
        ItemStack listaMaquinas = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("menu-principal.lista-maquinas.material")), 1)
                .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.lista-maquinas.nome")))
                .setLore(loreLista)
                .toItemStack();

        ItemStack headz = SkullCreator.createSkull();
        ItemStack head = ItemCreator.setSkullMeta(headz,"",null);

        menuPrincipal.setItem(StackCore.getInstance().getMenusYml().getInt("menu-principal.head.slot"),head);
        menuPrincipal.setItem(StackCore.getInstance().getMenusYml().getInt("menu-principal.recolher-maquinas.slot"),recolher);
        menuPrincipal.setItem(StackCore.getInstance().getMenusYml().getInt("menu-principal.gerenciar-maquinas.slot"),gerenciar);
        menuPrincipal.setItem(StackCore.getInstance().getMenusYml().getInt("menu-principal.lista-maquinas.slot"),listaMaquinas);
        menuPrincipal.setItem(StackCore.getInstance().getMenusYml().getInt("menu-principal.top-maquinas.slot"),topMaquinas);
    }

    public static void openInventory(Player p) {
        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
        ItemStack is = SkullCreator.itemFromUuid(p.getUniqueId());
        ArrayList<String> lore = new ArrayList<>();
        List<String> loreRecolher = new ArrayList<>();
        for(String s : StackCore.getInstance().getMenusYml().getStringList("menu-principal.head.lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&',s).replaceAll("@player",p.getName()).replaceAll("@limite", FormatMoney.format(pm.getLimite().doubleValue(), true)));
        }
        for(String x : StackCore.getInstance().getMenusYml().getStringList("menu-principal.recolher-maquinas.lore")) {
            loreRecolher.add(ChatColor.translateAlternateColorCodes('&',x));
        }
        ItemStack head = ItemCreator.setSkullMeta(is,ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.head.nome")),lore);
        ItemStack recolher = new ItemBuilder(Material.CHEST, 0)
                .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("menu-principal.recolher-maquinas.nome")))
                .setLore(loreRecolher)
                .toItemStack();
        int size = pm.getMaquinasToReceive().size();
        recolher.setAmount(size);
        menuPrincipal.setItem(10,head);
        menuPrincipal.setItem(11,recolher);
        p.openInventory(menuPrincipal);
    }

}
