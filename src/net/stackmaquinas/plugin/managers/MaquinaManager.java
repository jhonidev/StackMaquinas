package net.stackmaquinas.plugin.managers;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.PlayerMaquina;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MaquinaManager {

    private final Set<Maquina> maquinaSet;
    private final StackCore instance;

    public MaquinaManager() {
        this.maquinaSet = new HashSet<>();
        this.instance = StackCore.getInstance();
    }

    public void addMaquina(Maquina m) {
        this.maquinaSet.add(m);
    }

    public Maquina getMaquina(String key) {
        return maquinaSet.stream().filter(Maquina -> Maquina.getKey().equals(key)).findFirst().orElse(null);
    }

    public Maquina getMaquina(int id) {
        return maquinaSet.stream().filter(Maquina -> Maquina.getId() == id).findFirst().orElse(null);
    }

    public Set<Maquina> getMaquinaSet() { return this.maquinaSet; }

}
