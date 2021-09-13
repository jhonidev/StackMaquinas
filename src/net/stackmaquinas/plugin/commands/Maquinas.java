package net.stackmaquinas.plugin.commands;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.menus.MenuPrincipal;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Maquinas implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("maquinas")) {
            String regex = "\\d+";
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if(!p.hasPermission("stackmaquinas.admin")) {
                    MenuPrincipal.openInventory(p);
                    return true;
                }
                if (args.length == 4) {
                    if (args[0].equalsIgnoreCase("give")) {
                        if(args[1].equalsIgnoreCase("limite")) {
                            String palvo = args[2];
                            String quantia = args[3];

                            if(!quantia.matches(regex)) {
                                p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                                p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina give limite <player> <quantia>");
                                return true;
                            }

                            int quantidade = Integer.parseInt(quantia);
                            if(quantidade < 1) {
                                p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                                p.sendMessage("§cUitlize: /maquina give limite <player> <quantia>");
                                return true;
                            }

                            OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);

                            if(alvo == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }

                            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                            if(pm == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }

                            List<String> lore = new ArrayList<>();
                            for(String s : StackCore.getInstance().getConfig().getStringList("item-limite.lore")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&',s).replaceAll("@quantia",quantia));
                            }

                            ItemStack is = new ItemBuilder(Material.getMaterial(StackCore.getInstance().getConfig().getString("item-limite.material")),1)
                                    .setName(ChatColor.translateAlternateColorCodes('&',StackCore.getInstance().getConfig().getString("item-limite.nome")))
                                    .setLore(lore)
                                    .toItemStack();

                            if(alvo.getPlayer().getInventory().firstEmpty() == -1) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador está com o inventário cheio.");
                                return true;
                            }
                            alvo.getPlayer().getInventory().addItem(is);
                            p.sendMessage("§6[FastlyMaquinas] §fO limite foi enviado.");
                            return true;

                        }

                        String machineKey = args[1];
                        String palvo = args[2];
                        String quantia = args[3];

                        if(!quantia.matches(regex)) {
                            p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                            p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina give <maquina> <player> <quantia>");
                            return true;
                        }

                        int quantidade = Integer.parseInt(quantia);
                        if(quantidade < 1) {
                            p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                            p.sendMessage("§6[FastlyMaquinas] §fUitlize: /maquina give <maquina> <player> <quantia>");
                            return true;
                        }

                        OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);
                        if(alvo == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                            return true;
                        }

                        Maquina m = StackCore.getMaquinaManager().getMaquina(machineKey);
                        if(m == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fA máquina informada não existe.");
                            return true;
                        }

                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                        if(pm==null) {
                            p.sendMessage("§6[FastlyMaquinas] §fFalha ao localizar jogador!");
                            return true;
                        }

                        for(MaquinaPlayer mp : pm.getMaquinasToReceive()) {
                            if(mp.getMaquina().getKey().equalsIgnoreCase(machineKey)) {
                                BigInteger totalQuantia = mp.getMaquinaAmount().add(new BigInteger(quantia));
                                mp.setMaquinaAmount(totalQuantia);
                                p.sendMessage("§6[FastlyMaquinas] §fMáquina enviada!");
                                List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-recebida");
                                for (String s : msgLore) {
                                    alvo.getPlayer().sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', s).replaceAll("@quantia", quantia)
                                    );
                                }
                                return true;
                            }
                        }

                        List<UpgradeMaquina> upgradeMaquinaList = new ArrayList<>();
                        for (Upgrade u : m.getUpgradeList()) {
                            upgradeMaquinaList.add(
                                    new UpgradeMaquina(
                                            StackCore.getUpgradeManager(),
                                            u,
                                            1
                                    )
                            );
                        }
                        MaquinaPlayer mp = new MaquinaPlayer(
                                StackCore.getMaquinaPlayerManager(),
                                MaquinaPlayer.randomID(50),
                                m,
                                new BigInteger(quantia),
                                alvo.getUniqueId(),
                                new BigInteger("0"),
                                m.getOption().getDelayWave(),
                                upgradeMaquinaList,
                                false
                        );
                        pm.addMaquinaToReceive(mp);
                        p.sendMessage("§6[FastlyMaquinas] §fMáquina enviada!");
                        List<String> msgLore = StackCore.getInstance().getMsgYml().getStringList("maquina-recebida");
                        for (String s : msgLore) {
                            alvo.getPlayer().sendMessage(
                                    ChatColor.translateAlternateColorCodes('&', s).replaceAll("@quantia", quantia)
                            );
                        }
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("set")) {
                        if(args[1].equalsIgnoreCase("limite")) {
                            String palvo = args[2];
                            String quantia = args[3];

                            if(!quantia.matches(regex)) {
                                p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                                p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina set limite <player> <quantia>");
                                return true;
                            }

                            int quantidade = Integer.parseInt(quantia);
                            if(quantidade < 1) {
                                p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                                p.sendMessage("§cUitlize: /maquina set limite <player> <quantia>");
                                return true;
                            }

                            OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);

                            if(alvo == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }

                            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                            if(pm == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }
                            pm.setLimite(new BigInteger(quantia));
                            p.sendMessage("§6[FastlyMaquinas] §fLimite setado!");
                            return true;
                        }

                        String machineKey = args[1];
                        String palvo = args[2];
                        String quantia = args[3];

                        if(!quantia.matches(regex)) {
                            p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                            p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina set <maquina> <player> <quantia>");
                            return true;
                        }

                        int quantidade = Integer.parseInt(quantia);
                        if(quantidade < 1) {
                            p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                            p.sendMessage("§6[FastlyMaquinas] §fUitlize: /maquina set <maquina> <player> <quantia>");
                            return true;
                        }

                        OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);
                        if(alvo == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                            return true;
                        }

                        Maquina m = StackCore.getMaquinaManager().getMaquina(machineKey);
                        if(m == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fA máquina informada não existe.");
                            return true;
                        }

                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                        if(pm==null) {
                            p.sendMessage("§6[FastlyMaquinas] §fFalha ao localizar jogador!");
                            return true;
                        }

                        for(MaquinaPlayer mp : pm.getPlayerMaquinas()) {
                            if(mp.getMaquina().getKey().equalsIgnoreCase(m.getKey())) {
                                mp.setMaquinaAmount(new BigInteger(quantia));
                                p.sendMessage("§6[FastlyMaquinas] §fMáquinas setadas!");
                                return true;
                            }
                        }

                        List<UpgradeMaquina> upgradeMaquinaList = new ArrayList<>();
                        for (Upgrade u : m.getUpgradeList()) {
                            upgradeMaquinaList.add(
                                    new UpgradeMaquina(
                                            StackCore.getUpgradeManager(),
                                            u,
                                            1
                                    )
                            );
                        }
                        MaquinaPlayer mp = new MaquinaPlayer(
                                StackCore.getMaquinaPlayerManager(),
                                MaquinaPlayer.randomID(50),
                                m,
                                new BigInteger(quantia),
                                alvo.getUniqueId(),
                                new BigInteger("0"),
                                m.getOption().getDelayWave(),
                                upgradeMaquinaList,
                                true
                        );
                        pm.addMaquina(mp);
                        p.sendMessage("§6[FastlyMaquinas] §fMáquinas setadas!");
                        return true;

                    }
                    if(args[0].equalsIgnoreCase("remove")) {
                        if(args[1].equalsIgnoreCase("limite")) {
                            String palvo = args[2];
                            String quantia = args[3];

                            if(!quantia.matches(regex)) {
                                p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                                p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina remove limite <player> <quantia>");
                                return true;
                            }

                            int quantidade = Integer.parseInt(quantia);
                            if(quantidade < 1) {
                                p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                                p.sendMessage("§cUitlize: /maquina remove limite <player> <quantia>");
                                return true;
                            }

                            OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);

                            if(alvo == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }

                            PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                            if(pm == null) {
                                p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                                return true;
                            }
                            double limite = pm.getLimite().doubleValue();
                            BigInteger quant = new BigInteger(quantia);
                            if(limite < quantidade) {
                                pm.setLimite(new BigInteger("0"));
                            } else {
                                pm.setLimite(pm.getLimite().subtract(quant));
                            }
                            p.sendMessage("§6[FastlyMaquinas] §fLimite setado!");
                            return true;
                        }

                        String machineKey = args[1];
                        String palvo = args[2];
                        String quantia = args[3];

                        if(!quantia.matches(regex)) {
                            p.sendMessage("§6[FastlyMaquinas] §fVocê não utilizou o comando da forma correta.");
                            p.sendMessage("§6[FastlyMaquinas] §fUtilize: /maquina remove <maquina> <player> <quantia>");
                            return true;
                        }

                        int quantidade = Integer.parseInt(quantia);
                        if(quantidade < 1) {
                            p.sendMessage("§6[FastlyMaquinas] §fA quantia deve ser maior que 0!");
                            p.sendMessage("§6[FastlyMaquinas] §fUitlize: /maquina remove <maquina> <player> <quantia>");
                            return true;
                        }

                        OfflinePlayer alvo = Bukkit.getOfflinePlayer(palvo);
                        if(alvo == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fO jogador não foi encontrado.");
                            return true;
                        }

                        Maquina m = StackCore.getMaquinaManager().getMaquina(machineKey);
                        if(m == null) {
                            p.sendMessage("§6[FastlyMaquinas] §fA máquina informada não existe.");
                            return true;
                        }

                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(alvo.getUniqueId());
                        if(pm==null) {
                            p.sendMessage("§6[FastlyMaquinas] §fFalha ao localizar jogador!");
                            return true;
                        }

                        List<MaquinaPlayer> maquinasToRemove = new ArrayList<>();
                        for(MaquinaPlayer mp : pm.getPlayerMaquinas()) {
                            if(mp.getMaquina().getKey().equalsIgnoreCase(m.getKey())) {
                                double amount = mp.getMaquinaAmount().doubleValue();
                                if(amount < quantidade) {
                                    maquinasToRemove.add(mp);
                                } else {
                                    mp.setMaquinaAmount(mp.getMaquinaAmount().subtract(new BigInteger(quantia)));
                                }
                                p.sendMessage("§6[FastlyMaquinas] §fMáquinas setadas!");
                            }
                        }

                        if(maquinasToRemove.size() > 0) {
                            for(MaquinaPlayer mpz : maquinasToRemove) {
                                pm.removeMaquina(mpz);
                            }
                            return true;
                        }

                        p.sendMessage("§6[FastlyMaquinas] §fO jogador não possui essa máquina!");
                        return true;

                    }

                    p.sendMessage("§6[FastlyMaquinas] Lista de comandos:");
                    p.sendMessage("§e/maquina give <maquina> <player> <quantia>");
                    p.sendMessage("§e/maquina set <maquina> <player> <quantia>");
                    p.sendMessage("§e/maquina remove <maquina> <player> <quantia>");
                    p.sendMessage("");
                    p.sendMessage("§e/maquina give limite <player> <quantia>");
                    p.sendMessage("§e/maquina set limite <player> <quantia>");
                    p.sendMessage("§e/maquina remove limite <player> <quantia>");
                    return true;

                }

                MenuPrincipal.openInventory(p);
                return true;

            }
        }
        return false;
    }
}
