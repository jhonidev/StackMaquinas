package net.stackmaquinas.plugin.objetos;

import net.stackmaquinas.plugin.managers.PlayerMaquinaManager;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class PlayerMaquina {

    private UUID player;
    private List<MaquinaPlayer> playerMaquinas;
    private List<MaquinaPlayer> maquinasToReceive;
    private BigInteger limite;
    private MaquinaPlayer gerenciando;

    public PlayerMaquina(PlayerMaquinaManager pmm, UUID player, List<MaquinaPlayer> playerMaquinas, List<MaquinaPlayer> maquinasToReceive, BigInteger limite) {
        this.player = player;
        this.playerMaquinas = playerMaquinas;
        this.maquinasToReceive = maquinasToReceive;
        this.limite = limite;
        this.gerenciando = null;
        pmm.addPlayerMaquina(this);
    }

    public UUID getPlayer() {
        return this.player;
    }

    public List<MaquinaPlayer> getPlayerMaquinas() {
        return this.playerMaquinas;
    }

    public void addMaquina(MaquinaPlayer mp) {
        this.playerMaquinas.add(mp);
    }
    public void removeMaquina(MaquinaPlayer mp) { this.playerMaquinas.remove(mp); }

    public BigInteger getLimite() { return this.limite; }

    public void setLimite(BigInteger i) { this.limite=i; }

    public List<MaquinaPlayer> getMaquinasToReceive() { return this.maquinasToReceive; }
    public void removeMaquinaToReceive(MaquinaPlayer mp) { this.maquinasToReceive.remove(mp); }
    public void addMaquinaToReceive(MaquinaPlayer mp) { this.maquinasToReceive.add(mp); }
    public void setGerenciando(MaquinaPlayer mp) { this.gerenciando = mp; }
    public MaquinaPlayer getGerenciando() { return this.gerenciando; }


}
