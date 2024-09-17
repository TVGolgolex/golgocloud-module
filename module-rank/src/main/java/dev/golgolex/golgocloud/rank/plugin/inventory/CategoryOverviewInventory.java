package dev.golgolex.golgocloud.rank.plugin.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CategoryOverviewInventory {

    private final Player player;
    private final Inventory inventory;

    public CategoryOverviewInventory(@NotNull Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("&7Overview"));

        

    }
}
