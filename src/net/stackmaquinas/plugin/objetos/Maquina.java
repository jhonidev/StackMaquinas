package net.stackmaquinas.plugin.objetos;

import net.stackmaquinas.plugin.managers.MaquinaManager;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;

import java.util.List;

public class Maquina {

    private final String key;
    private final int id;
    private String name;
    private List<String> lore;
    private String block;
    private Drop drop;
    private List<Upgrade> upgradeList;
    private Option option;
    private boolean permToBuy;
    private String permisison;

    public Maquina(MaquinaManager mm, String key, String name, List<String> lore, String block, Drop drop, List<Upgrade> upgradeList, Option option, int id, boolean permToBuy, String permisison) {
        this.key = key;
        this.name = name;
        this.lore = lore;
        this.block = block;
        this.drop = drop;
        this.upgradeList = upgradeList;
        this.option = option;
        this.id = id;
        this.permToBuy = permToBuy;
        this.permisison = permisison;
        mm.addMaquina(this);
    }

    public String getKey() {
        return this.key;
    }
    public String getName() { return this.name; }
    public List<String> getLore() { return this.lore;}
    public String getBlock() { return this.block;}
    public Drop getDrop() { return this.drop;}
    public Option getOption() { return this.option; }
    public List<Upgrade> getUpgradeList() { return this.upgradeList; }
    public int getId() { return this.id; }
    public boolean needPermToBuy() { return this.permToBuy; }
    public String getPermisison() { return this.permisison; }

}
