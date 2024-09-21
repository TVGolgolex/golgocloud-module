package dev.golgolex.golgocloud.rank.plugin.listener;

import dev.golgolex.golgocloud.rank.plugin.inventory.RankInventory;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class InventoryListener implements Listener {

    @Getter
    private static Map<UUID, RankInventory> inventoryMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        inventoryMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!inventoryMap.containsKey(event.getWhoClicked().getUniqueId())) return;
        inventoryMap.get(event.getWhoClicked().getUniqueId()).onClick(event);
    }

}
