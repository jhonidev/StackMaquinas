package net.stackmaquinas.plugin.database;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.MaquinaPlayer;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL {

    private static Connection conn;

    public static synchronized boolean connection() {
        String host = StackCore.getInstance().getConfig().getString("mysql.host");
        String port = StackCore.getInstance().getConfig().getString("mysql.porta");
        String user = StackCore.getInstance().getConfig().getString("mysql.usuario");
        String pass = StackCore.getInstance().getConfig().getString("mysql.senha");
        String name = StackCore.getInstance().getConfig().getString("mysql.banco");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, pass);
            createTable();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    static void createTable() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS StackMaquinas(`machineId` VARCHAR(255) NOT NULL, `machineOwner` VARCHAR(255) NOT NULL, `machineKey` VARCHAR(255) NOT NULL, `machineAmount` VARCHAR(255) NOT NULL, `totalDrops` VARCHAR(255) NOT NULL, `nextDrop` VARCHAR(255) NOT NULL, `upgrades` VARCHAR(255) NOT NULL)");
            PreparedStatement ps2 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS StackPlayers(`uuid` VARCHAR(225) NOT NULL, `limite` VARCHAR(255) NOT NULL, `machinesToReceive` VARCHAR(400) NOT NULL)");
            ps.executeUpdate();
            ps2.executeUpdate();
            ps.close();
            ps2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasAccount(Player p) {
        try {
            if (!conn.isClosed()) {
                PreparedStatement ps = getConnection().prepareStatement("SELECT uuid FROM StackPlayers WHERE uuid = ?;");
                ps.setString(1, String.valueOf(p.getUniqueId()));
                ResultSet rs = ps.executeQuery();
                boolean result = rs.next();
                ps.close();
                rs.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean machineExists(String s) {
        try {
            if (!conn.isClosed()) {
                PreparedStatement ps = getConnection().prepareStatement("SELECT machineId FROM StackMaquinas WHERE machineId = ?;");
                ps.setString(1, s);
                ResultSet rs = ps.executeQuery();
                boolean result = rs.next();
                ps.close();
                rs.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createAccount(Player p) throws IOException {
        try {
            if (!conn.isClosed()) {
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO StackPlayers(uuid,limite,machinesToReceive) VALUES (?, ?, ?)");
                ps.setString(1, String.valueOf(p.getUniqueId()));
                ps.setString(2, "0");
                ps.setString(3,"null");
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createMachine(MaquinaPlayer mp) throws IOException {
        try {
            if (!conn.isClosed()) {
                List<UpgradeMaquina> list = mp.getUpgradeMaquinaList();
                StringBuilder sb = new StringBuilder();
                for(UpgradeMaquina um : list) {
                    sb.append(um.getUpgrade().getUpgradeType().getName()).append("-").append(um.getCurrentLevel()).append(";");
                }
                sb.deleteCharAt(sb.length()-1);
                PreparedStatement ps = getConnection().prepareStatement("INSERT INTO StackMaquinas(machineId, machineOwner, machineKey, machineAmount, totalDrops, nextDrop, upgrades) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, mp.getId());
                ps.setString(2, mp.getOwner().toString());
                ps.setString(3, mp.getMaquina().getKey());
                ps.setString(4, String.valueOf(mp.getMaquinaAmount()));
                ps.setString(5, String.valueOf(mp.getTotalDrops()));
                ps.setString(6, String.valueOf(mp.getNextDrop()));
                ps.setString(7, sb.toString());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMachine(MaquinaPlayer mp) {
        try {
            if(!conn.isClosed()) {
                List<UpgradeMaquina> list = mp.getUpgradeMaquinaList();
                StringBuilder sb = new StringBuilder();
                for(UpgradeMaquina um : list) {
                    sb.append(um.getUpgrade().getUpgradeType().getName()).append("-").append(um.getCurrentLevel()).append(";");
                }
                sb.deleteCharAt(sb.length()-1);
                PreparedStatement ps = getConnection().prepareStatement("UPDATE StackMaquinas SET machineAmount = ?, totalDrops = ?, nextDrop = ?, upgrades = ? WHERE machineId = ?");
                ps.setString(1, String.valueOf(mp.getMaquinaAmount()));
                ps.setString(2, String.valueOf(mp.getTotalDrops()));
                ps.setString(3, String.valueOf(mp.getNextDrop()));
                ps.setString(4, sb.toString());
                ps.setString(5, mp.getId());
                ps.executeUpdate();
                ps.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayer(PlayerMaquina pm) {
        try {
            if(!conn.isClosed()) {
                List<MaquinaPlayer> toReceive = pm.getMaquinasToReceive();
                StringBuilder sb = new StringBuilder();
                if(toReceive.size() > 0) {
                    for(MaquinaPlayer  mp : toReceive) {
                        sb.append(mp.getMaquina().getKey()).append("-").append(mp.getMaquinaAmount()).append(";");
                    }
                    sb.deleteCharAt(sb.length()-1);
                } else {
                    sb.append("null");
                }
                PreparedStatement ps = getConnection().prepareStatement("UPDATE StackPlayers SET limite = ?, machinesToReceive = ? WHERE uuid = ?");
                ps.setString(1, String.valueOf(pm.getLimite()));
                ps.setString(2, sb.toString());
                ps.setString(3, pm.getPlayer().toString());
                ps.executeUpdate();
                ps.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void serialisePlayers() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if(!conn.isClosed()) {
                ps = getConnection().prepareStatement("SELECT * FROM StackPlayers");
                rs = ps.executeQuery();
                while(rs.next()) {
                    try {

                        List<MaquinaPlayer> lista = new ArrayList<>();
                        List<MaquinaPlayer> toReceive = new ArrayList<>();
                        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                        String maquinasToReceive = rs.getString("machinesToReceive");
                        if(!maquinasToReceive.equals("null")) {
                            String[] all = maquinasToReceive.split(";");
                            for(String s : all) {
                                String[] tudo = s.split("-");
                                String maquinaKey = tudo[0];
                                String quantidade = tudo[1];
                                Maquina m = StackCore.getMaquinaManager().getMaquina(maquinaKey);
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
                                        new BigInteger(quantidade),
                                        p.getUniqueId(),
                                        new BigInteger("0"),
                                        System.currentTimeMillis() + (m.getOption().getDelayWave()*1000),
                                        upgradeMaquinaList,
                                        false
                                );
                                toReceive.add(mp);
                            }
                        }

                        new PlayerMaquina(StackCore.getPlayerMaquinaManager(), UUID.fromString(rs.getString("uuid")), lista, toReceive, new BigInteger(rs.getString("limite")));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void serialiseMachines() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if(!conn.isClosed()) {
                ps = getConnection().prepareStatement("SELECT * FROM StackMaquinas");
                rs = ps.executeQuery();
                while(rs.next()) {
                    try {
                        /// id, owner, key, amount, totalDrops, secondsLeft, upgrades
                        UUID u = UUID.fromString(rs.getString("machineOwner"));
                        Maquina m = StackCore.getMaquinaManager().getMaquina(rs.getString("machineKey"));
                        PlayerMaquina pm = StackCore.getPlayerMaquinaManager().getPlayerMaquina(u);
                        if(m!=null) {

                            List<UpgradeMaquina> upgradeMaquinaList = new ArrayList<>();

                            String upgrades = rs.getString("upgrades");
                            if(upgrades != null) {
                                String[] upgrade = upgrades.split(";");
                                for(String s : upgrade) {
                                    String[] all = s.split("-");
                                    String upgradeType = all[0];
                                    String level = all[1];

                                    UpgradeType ut = StackCore.getUpgradeManager().getUpgradeType(upgradeType);
                                    Upgrade up = StackCore.getUpgradeManager().getUpgrade(ut);
                                    upgradeMaquinaList.add(new UpgradeMaquina(
                                            StackCore.getUpgradeManager(),
                                            up,
                                            Integer.parseInt(level)
                                    ));

                                }
                            }

                            MaquinaPlayer mpx = new MaquinaPlayer(
                                    StackCore.getMaquinaPlayerManager(),
                                    rs.getString("machineId"),
                                    m,
                                    new BigInteger(rs.getString("machineAmount")),
                                    u,
                                    new BigInteger(rs.getString("totalDrops")),
                                    rs.getLong("nextDrop"),
                                    upgradeMaquinaList,
                                    true
                                    );
                            pm.addMaquina(mpx);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

}
