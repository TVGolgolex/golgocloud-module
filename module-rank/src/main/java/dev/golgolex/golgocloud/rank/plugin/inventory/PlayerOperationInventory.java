package dev.golgolex.golgocloud.rank.plugin.inventory;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleUser;
import dev.golgolex.golgocloud.common.user.CloudPlayer;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.quala.translation.basic.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class PlayerOperationInventory implements RankInventory {

    private final Player player;
    private final CloudPlayer cloudPlayer;
    private final Language language;
    private final Inventory inventory;
    private final ItemStack playerCOverview;

    public PlayerOperationInventory(@NotNull Player player,
                                    @NotNull CloudPlayer cloudPlayer,
                                    @NotNull Language language,
                                    @NotNull ItemStack playerCOverview) {
        this.player = player;
        this.cloudPlayer = cloudPlayer;
        this.language = language;
        this.playerCOverview = playerCOverview;

        this.inventory = Bukkit.createInventory(null, 27, Component.text(player.getName()));

        for (var i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        this.inventory.setItem(10, playerCOverview);

        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-inventory-rank-operation");

        var resetOperationItemStack = new ItemStack(Material.BARRIER);
        var resetOperationItemMeta = resetOperationItemStack.getItemMeta();
        resetOperationItemMeta.displayName(MiniMessage.miniMessage().deserialize(translation.message("displayname-reset-player")));
        resetOperationItemStack.setItemMeta(resetOperationItemMeta);
        this.inventory.setItem(12, resetOperationItemStack);

        var removeGroupsOperationItemStack = new ItemStack(Material.REPEATING_COMMAND_BLOCK);
        var removeGroupsOperationItemMeta = removeGroupsOperationItemStack.getItemMeta();
        removeGroupsOperationItemMeta.displayName(MiniMessage.miniMessage().deserialize(translation.message("displayname-remove-permissible-group")));
        removeGroupsOperationItemStack.setItemMeta(removeGroupsOperationItemMeta);
        this.inventory.setItem(13, removeGroupsOperationItemStack);

        InventoryListener.getInventoryMap().put(player.getUniqueId(), this);
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 11 -> {
                var permissionUser = CloudAPI.instance().cloudPermissionService().permissibleUser(cloudPlayer.uniqueId());
                if (permissionUser == null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 10, 10);
                    return;
                }

                permissionUser.groupEntries().clear();
                for (var group : CloudAPI.instance().cloudPermissionService().cloudPermissibleGroups()) {
                    if (group.defaultGroup()) {
                        permissionUser.groupEntries().add(new CloudPermissibleUser.GroupEntry(group.uuid(), -1));
                        break;
                    }
                }

                CloudAPI.instance().cloudPermissionService().updatePermissibleUser(permissionUser);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 2);
            }
        }
    }
}
