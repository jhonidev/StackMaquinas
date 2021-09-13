package net.stackmaquinas.plugin.objetos.upgrades;

import net.stackmaquinas.plugin.managers.UpgradeManager;

public class Upgrade {

    private UpgradeType upgradeType;
    private boolean active;
    private int maxLevel;
    private double startCost;
    private double costGrowPercentage;
    private String payment;

    public Upgrade(UpgradeManager um,UpgradeType type, int maxLevel, double startCost, double costGrowPercentage, String payment) {
        this.upgradeType = type;
        this.maxLevel = maxLevel;
        this.startCost = startCost;
        this.costGrowPercentage = costGrowPercentage;
        this.active = true;
        this.payment = payment;
        um.addUpgrade(this);
    }

    public UpgradeType getUpgradeType() {
        return this.upgradeType;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public double getStartCost() {
        return this.startCost;
    }

    public double getCostGrowPercentage() {
        return this.costGrowPercentage;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
    public String getPayment() { return this.payment; }

}
