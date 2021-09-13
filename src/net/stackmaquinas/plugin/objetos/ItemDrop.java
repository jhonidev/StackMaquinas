package net.stackmaquinas.plugin.objetos;

import org.bukkit.inventory.ItemStack;

public class ItemDrop {

    private ItemStack item;
    private Double valor;
    private String pagamento;

    public ItemDrop(ItemStack item, Double valor, String pagamento) {
        this.item = item;
        this.valor = valor;
        this.pagamento = pagamento;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Double getValor() {
        return this.valor;
    }

    public String getPagamento() {
        return this.pagamento;
    }

}
