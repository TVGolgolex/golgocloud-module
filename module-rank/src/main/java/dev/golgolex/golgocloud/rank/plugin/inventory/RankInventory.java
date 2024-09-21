package dev.golgolex.golgocloud.rank.plugin.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface RankInventory {

    Inventory inventory();

    void onClick(InventoryClickEvent event);

}
