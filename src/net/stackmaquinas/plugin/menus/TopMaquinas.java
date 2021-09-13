package net.stackmaquinas.plugin.menus;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.regex.Pattern;

public class TopMaquinas {

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o2,
                               Map.Entry<String, Integer> o1)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    static Inventory topMaquinas = Bukkit.createInventory(null, StackCore.getInstance().getMenusYml().getInt("rows.top-maquinas")*9,
            ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("nome-dos-menus.top-maquinas"))
    );

    public static void setupTopList() {
        HashMap<String, Integer> topList = new HashMap<>();
        for(PlayerMaquina pm : StackCore.getPlayerMaquinaManager().getPlayerMaquinaSet()) {
            double amount = 0.0;
            for(MaquinaPlayer mp : pm.getPlayerMaquinas()) {
                amount = amount+mp.getMaquina().getOption().getMachinePrice();
            }
            topList.put(String.valueOf(pm.getPlayer()),(int)amount);
        }
        Map<String,Integer> toplist2 = sortByValue(topList);
        StackCore.topMaquinas.clear();
        int index = 0;
        for(Map.Entry<String,Integer> en : toplist2.entrySet()) {
            if(index==10) {return;}
            index++;
            StackCore.topMaquinas.put(index,en.getKey());
        }
    }

    public static void updateTopMenu() {
        setupTopList();
        for(int i=1;i<11;i++) {
            if(StackCore.topMaquinas.get(i) != null) {
                UUID u = UUID.fromString(StackCore.topMaquinas.get(i));
                PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(u);
                double amount = 0;
                for(MaquinaPlayer mp : pm.getPlayerMaquinas()) {
                    amount = amount+(mp.getMaquina().getOption().getMachinePrice()*mp.getMaquinaAmount().doubleValue());
                }
                ItemStack is = SkullCreator.itemFromUuid(u);
                List<String> lore = StackCore.getInstance().getMenusYml().getStringList("top-maquinas.lore");
                ArrayList<String> rightLore = new ArrayList<>();
                for(String s : lore) {
                    rightLore.add(ChatColor.translateAlternateColorCodes('&',s)
                            .replaceAll("@player",Bukkit.getOfflinePlayer(u).getName())
                    .replaceAll("@total", FormatMoney.format(amount, false))
                            .replaceAll("@lugar",String.valueOf(i)));
                }
                String title = ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("top-maquinas.nome")
                        .replaceAll("@player",Bukkit.getOfflinePlayer(u).getName())
                        .replaceAll("@total",FormatMoney.format(amount, false))
                .replaceAll("@lugar",String.valueOf(i)));
                ItemStack isx = ItemCreator.setSkullMeta(is,title,rightLore);
                if(i>7) {
                    topMaquinas.setItem(i+13,isx);
                } else {
                    topMaquinas.setItem(i+9,isx);
                }
            } else {
                ItemStack head = SkullCreator.createSkull();
                List<String> lore = StackCore.getInstance().getMenusYml().getStringList("top-maquinas.lore");
                ArrayList<String> rightLore = new ArrayList<>();
                for(String s : lore) {
                    rightLore.add(ChatColor.translateAlternateColorCodes('&',s)
                            .replaceAll("@player","Indefinido")
                            .replaceAll("@total","0")
                            .replaceAll("@lugar",String.valueOf(i)));
                }
                String title = ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("top-maquinas.nome")
                        .replaceAll("@player","Indefinido")
                        .replaceAll("@total","0")
                        .replaceAll("@lugar",String.valueOf(i)));
                ItemStack isx = ItemCreator.setSkullMeta(head,title,rightLore);
                if(i>7) {
                    topMaquinas.setItem(i+13,isx);
                } else {
                    topMaquinas.setItem(i+9,isx);
                }
            }
        }
        List<String> gobackLore = new ArrayList<>();
        for(String xyz : StackCore.getInstance().getMenusYml().getStringList("voltar-item.lore")) {
            gobackLore.add(ChatColor.translateAlternateColorCodes('&',xyz));
        }
        ItemStack goback = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getMenusYml().getString("voltar-item.material"))
        ).setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getMenusYml().getString("voltar-item.nome")))
                .setLore(gobackLore).toItemStack();
        topMaquinas.setItem(27,goback);
    }

    public static void openTopMenu(Player p) {
        p.openInventory(topMaquinas);
        return;
    }

}
