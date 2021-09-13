package net.stackmaquinas.plugin.objetos.upgrades;

import net.stackmaquinas.plugin.managers.UpgradeManager;

public class UpgradeMaquina {

    private Upgrade upgrade;
    private int currentLevel;

    public UpgradeMaquina(UpgradeManager um, Upgrade upgrade, int currentLevel) {
        this.upgrade = upgrade;
        this.currentLevel = currentLevel;
        um.addUpgradeMaquina(this);
    }

    public Upgrade getUpgrade() {
        return this.upgrade;
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentLevel(int i) {
        this.currentLevel = i;
    }

}
