package dev.golgolex.golgocloud.rank.plugin.inventory;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.common.user.CloudPlayer;
import dev.golgolex.golgocloud.rank.plugin.PermissibleGroupCategory;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.quala.translation.basic.Language;
import dev.golgolex.quala.translation.basic.TextUtil;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RanksCategoryInventory implements RankInventory {

    private final Player player;
    private final CloudPlayer cloudPlayer;
    private final PermissibleGroupCategory permissibleGroupCategory;
    private final Language language;
    private final Inventory inventory;
    private final Map<Integer, CloudPermissibleGroup> groupMap = new ConcurrentHashMap<>();
    private final ItemStack playerCOverview;

    public RanksCategoryInventory(@NotNull Player player,
                                  @NotNull CloudPlayer cloudPlayer,
                                  @NotNull PermissibleGroupCategory permissibleGroupCategory,
                                  @NotNull Language language,
                                  @NotNull ItemStack playerCOverview) {
        this.player = player;
        this.cloudPlayer = cloudPlayer;
        this.permissibleGroupCategory = permissibleGroupCategory;
        this.language = language;
        this.playerCOverview = playerCOverview;

        this.inventory = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}>{1}</color>",
                permissibleGroupCategory.color(), permissibleGroupCategory.name())));

        for (var i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        this.inventory.setItem(3, playerCOverview);

        var categoryDisplayItem = buildLeatherHead(permissibleGroupCategory.color());
        var categoryDisplayItemMeta = categoryDisplayItem.getItemMeta();
        categoryDisplayItemMeta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<dark_gray>»</dark_gray> <color:{0}><b>{1}</b></color>",
                permissibleGroupCategory.color(),
                permissibleGroupCategory.name())).decoration(TextDecoration.ITALIC, false));
        categoryDisplayItem.setItemMeta(categoryDisplayItemMeta);
        this.inventory.setItem(5, categoryDisplayItem);

        var groups = CloudAPI.instance()
                .cloudPermissionService()
                .cloudPermissibleGroups()
                .stream()
                .filter(permissibleGroup -> this.permissibleGroupCategory.groups().contains(permissibleGroup.uuid()))
                .toList();

        var slot = 9;
        for (var group : groups) {
            var rankDisplayItem = buildLeatherHead(group.properties().readString("color$"));
            var rankDisplayItemMeta = rankDisplayItem.getItemMeta();

            rankDisplayItemMeta.displayName(MiniMessage
                    .miniMessage()
                    .deserialize(TextUtil.buildMessage("<dark_gray>»</dark_gray> <color:{0}><b>{1}</b></color>",
                            group.properties().readString("color$"),
                            group.name()))
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            rankDisplayItem.setItemMeta(rankDisplayItemMeta);
            inventory.setItem(slot, rankDisplayItem);
            groupMap.put(slot, group);


            slot++;
            if (slot >= (this.inventory.getSize() - 9)) {
                break;
            }
        }

        InventoryListener.getInventoryMap().put(player.getUniqueId(), this);
    }

    private ItemStack buildLeatherHead(@NotNull String colorCode) {
        var itemStack = new ItemStack(Material.LEATHER_HELMET);
        var leatherMeta = (LeatherArmorMeta) itemStack.getItemMeta();

        leatherMeta.setColor(org.bukkit.Color.fromRGB(Integer.parseInt(colorCode.replace("#", ""), 16)));
        leatherMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        leatherMeta.addItemFlags(ItemFlag.HIDE_DYE);
        leatherMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        itemStack.setItemMeta(leatherMeta);
        return itemStack;
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!this.groupMap.containsKey(event.getSlot())) return;
        var group = this.groupMap.get(event.getSlot());

        player.closeInventory();
        player.openInventory(new AddOrSetRankInventory(
                player,
                cloudPlayer,
                language,
                group,
                playerCOverview
        ).inventory());
    }
}