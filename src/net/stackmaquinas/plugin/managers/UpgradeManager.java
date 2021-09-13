package net.stackmaquinas.plugin.managers;

import net.stackmaquinas.plugin.StackCore;
import net.stackmaquinas.plugin.objetos.Maquina;
import net.stackmaquinas.plugin.objetos.upgrades.Upgrade;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeMaquina;
import net.stackmaquinas.plugin.objetos.upgrades.UpgradeType;

import java.util.HashSet;
import java.util.Set;

public class UpgradeManager {

    private final Set<UpgradeMaquina> upgradeMaquinaSet;
    private final Set<UpgradeType> upgradeTypeSet;
    private final Set<Upgrade> upgradeSet;
    private final StackCore instance;

    public UpgradeManager() {
        this.upgradeMaquinaSet = new HashSet<>();
        this.upgradeTypeSet = new HashSet<>();
        this.upgradeSet = new HashSet<>();
        this.instance = StackCore.getInstance();
    }

    public void addUpgradeMaquina(UpgradeMaquina um) {
        this.upgradeMaquinaSet.add(um);
    }
    public void addUpgradeType(UpgradeType ut) {
        this.upgradeTypeSet.add(ut);
    }
    public void addUpgrade(Upgrade u) {
        this.upgradeSet.add(u);
    }

    public UpgradeMaquina getUpgradeMaquina(Upgrade u) {
        return upgradeMaquinaSet.stream().filter(UpgradeMaquina -> UpgradeMaquina.getUpgrade().equals(u)).findFirst().orElse(null);
    }

    public Upgrade getUpgrade(UpgradeType ut) {
        return upgradeSet.stream().filter(Upgrade -> Upgrade.getUpgradeType().equals(ut)).findFirst().orElse(null);
    }

    public UpgradeType getUpgradeType(String name) {
        return upgradeTypeSet.stream().filter(UpgradeType -> UpgradeType.getName().equals(name)).findFirst().orElse(null);
    }

}
