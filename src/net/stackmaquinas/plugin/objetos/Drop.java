package net.stackmaquinas.plugin.objetos;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Drop {

    private boolean giveCommands;
    private boolean giveItems;
    private List<ItemDrop> itemDropList;
    private List<String> commandsList;
    private String dropName;

    public Drop(boolean commands, boolean items, List<ItemDrop> itemDropList, List<String> commandsList, String dropName) {
        this.giveCommands = commands;
        this.giveItems = items;
        this.itemDropList = itemDropList;
        this.commandsList = commandsList;
        this.dropName = dropName;
    }

    public boolean isGivingCommands() {
        return this.giveCommands;
    }

    public boolean isGivingItems() {
        return this.giveItems;
    }

    public List<ItemDrop> getItemStackList() {
        return this.itemDropList;
    }

    public List<String> getCommandsList() {
        return this.commandsList;
    }

    public String getDropName() {
        return this.dropName;
    }

}
