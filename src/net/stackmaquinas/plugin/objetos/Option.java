package net.stackmaquinas.plugin.objetos;

public class Option {

    private int delayWave;
    private int startDrop;
    private double machinePrice;
    private boolean isSelling;

    public Option(int delayWave, int startDrop, double machinePrice, boolean isSelling) {
        this.delayWave = delayWave;
        this.startDrop = startDrop;
        this.machinePrice = machinePrice;
        this.isSelling = isSelling;
    }

    public int getDelayWave() {
        return this.delayWave;
    }

    public int getStartDrop() {
        return this.startDrop;
    }

    public double getMachinePrice() {
        return this.machinePrice;
    }

    public boolean isSelling() {
        return this.isSelling;
    }

}
