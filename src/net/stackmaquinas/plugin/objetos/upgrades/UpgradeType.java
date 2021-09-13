package net.stackmaquinas.plugin.objetos.upgrades;

import net.stackmaquinas.plugin.managers.UpgradeManager;

public class UpgradeType {

    private String name;
    private String function;

    public UpgradeType(UpgradeManager um, String name, String function) {
        this.name = name;
        this.function = function;
        um.addUpgradeType(this);
    }

    public String getName() {
        return this.name;
    }

    public String getFunction() {
        return this.function;
    }

}
