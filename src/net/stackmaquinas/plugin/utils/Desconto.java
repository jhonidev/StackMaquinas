package net.stackmaquinas.plugin.utils;

import net.stackmaquinas.plugin.StackCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Desconto {

    public static int getDesconto(Player p) {
        String regex = "\\d+";
        List<Integer> descontos = new ArrayList<>();
        for(String s : StackCore.getInstance().getConfig().getStringList("options.descontos")) {
            String[] all = s.split(";");
            String permission = all[0];
            String desconto = all[1];
            if(p.hasPermission(permission)) {
                if(desconto.matches(regex)) {
                    descontos.add(Integer.valueOf(desconto));
                }
            }
        }
        if(descontos.size() > 0) {
            return Collections.max(descontos);

        }
        return 0;
    }

}
