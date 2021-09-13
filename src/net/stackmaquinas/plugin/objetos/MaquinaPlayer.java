package net.stackmaquinas.plugin.objetos;

import net.stackmaquinas.plugin.managers.MaquinaPlayerManager;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class MaquinaPlayer {

    public static String randomID(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private final String id;
    private Maquina maquina;
    private BigInteger maquinaAmount;
    private UUID owner;
    private BigInteger totalDrops;
    private long nextDrop;
    private List<UpgradeMaquina> upgradeMaquina;

    public MaquinaPlayer(MaquinaPlayerManager mpm, String id, Maquina maquina, BigInteger maquinaAmount, UUID owner, BigInteger totalDrops, long nextDrop, List<UpgradeMaquina> upgradeMaquina, boolean manager) {
        this.id = id;
        this.maquina = maquina;
        this.maquinaAmount = maquinaAmount;
        this.owner = owner;
        this.totalDrops = totalDrops;
        this.nextDrop = nextDrop;
        this.upgradeMaquina = upgradeMaquina;
        if(manager) { mpm.addMaquinaObjeto(this); }
    }

    public Maquina getMaquina() {
        return this.maquina;
    }

    public BigInteger getMaquinaAmount() {
        return this.maquinaAmount;
    }

    public BigInteger getTotalDrops() {
        return this.totalDrops;
    }

    public long getNextDrop() {
        return this.nextDrop;
    }

    public List<UpgradeMaquina> getUpgradeMaquinaList() {
        return this.upgradeMaquina;
    }

    public String getId() { return this.id; }

    public UUID getOwner() {
        return this.owner;
    }

    public void setTotalDrops(BigInteger i) { this.totalDrops = i; }

    public void setNextDrop(long l) {
        this.nextDrop = l;
    }

    public void setMaquinaAmount(BigInteger i) { this.maquinaAmount = i; }

}
