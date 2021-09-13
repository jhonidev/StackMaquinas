package net.stackmaquinas.plugin.events;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.utils.Desconto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.List;

public class RightClick implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (p.getItemInHand().getType() == Material.getMaterial(StackCore.getInstance().getConfig().getString("item-limite.material"))) {
                if (p.getItemInHand().hasItemMeta()) {
                    if (p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(
                            ChatColor.translateAlternateColorCodes('&', StackCore.getInstance().getConfig().getString("item-limite.nome"))
                    )) {

                        List<String> itemLore = p.getItemInHand().getItemMeta().getLore();
                        String quantia = ChatColor.stripColor(itemLore.toString());
                        quantia = quantia.replaceAll("\\D", "");

                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
                        if (pm != null) {


                            List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("limite-adicionado");
                            for (String s : msgLore) {
                                p.sendMessage(
                                        ChatColor.translateAlternateColorCodes('&', s).replaceAll("@quantia", quantia)
                                );
                            }

                            ItemStack is = p.getItemInHand();
                            int quantiaLimite = Integer.parseInt(quantia)*is.getAmount();
                            BigInteger acrescentar = BigInteger.valueOf(quantiaLimite);
                            if(p.isSneaking()) {
                                pm.setLimite(pm.getLimite().add(acrescentar));
                                p.getInventory().remove(is);
                            } else {
                                if(is.getAmount() > 1) {
                                    is.setAmount(is.getAmount()-1);
                                } else {
                                    p.getInventory().remove(is);
                                }
                                pm.setLimite(pm.getLimite().add(new BigInteger(quantia)));
                            }
                        }
                    }
                }
            }
        }
    }

}
