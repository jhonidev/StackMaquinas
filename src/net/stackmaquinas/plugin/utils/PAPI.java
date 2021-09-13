package net.stackmaquinas.plugin.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import org.bukkit.entity.Player;

public class PAPI extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "fastlymaquinas";
    }

    @Override
    public String getAuthor() {
        return "Fastly";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public String onPlaceholderRequest(Player p, String s) {
        if(p==null) {
            return "0";
        } else if(s.equals("limite")) {
            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(p.getUniqueId());
            return FormatMoney.format(pm.getLimite().doubleValue(), true);
        } else {
            return "0";
        }
    }

}
