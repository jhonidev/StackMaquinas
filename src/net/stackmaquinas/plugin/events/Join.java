package net.stackmaquinas.plugin.events;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.database.MySQL;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        Player p = e.getPlayer();
        if(StackCore.getInstance().usingMySQL) {
            if(!MySQL.hasAccount(p)) {
                MySQL.createAccount(e.getPlayer());
            }
        }
        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
        if(pm == null) {
            List<MaquinaPlayer> list1 = new ArrayList<>();
            List<MaquinaPlayer> list2 = new ArrayList<>();
            new PlayerMaquina(
                    StackCore.getPlayerMaquinaManager(),
                    p.getUniqueId(),
                    list1,
                    list2,
                    new BigInteger(StackCore.getInstance().getConfig().getString("options.limite-inicial"))
            );
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        StackCore.buyingMaquina.remove(e.getPlayer());
    }

}
