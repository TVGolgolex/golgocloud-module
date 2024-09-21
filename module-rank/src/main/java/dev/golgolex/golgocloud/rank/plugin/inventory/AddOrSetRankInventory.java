package dev.golgolex.golgocloud.rank.plugin.inventory;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.common.user.CloudPlayer;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.quala.translation.basic.Language;
import dev.golgolex.quala.translation.basic.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class AddOrSetRankInventory implements RankInventory {

    private final Player player;
    private final CloudPlayer cloudPlayer;
    private final Language language;
    private final Inventory inventory;
    private final CloudPermissibleGroup cloudPermissibleGroup;
    private final ItemStack playerCOverview;

    public AddOrSetRankInventory(@NotNull Player player,
                                 @NotNull CloudPlayer cloudPlayer,
                                 @NotNull Language language,
                                 @NotNull CloudPermissibleGroup cloudPermissibleGroup,
                                 @NotNull ItemStack playerCOverview) {
        this.player = player;
        this.cloudPlayer = cloudPlayer;
        this.language = language;
        this.cloudPermissibleGroup = cloudPermissibleGroup;
        this.playerCOverview = playerCOverview;

        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-inventory-rank-add_or_set");

        this.inventory = Bukkit.createInventory(null, 27, MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}>{1}</color>",
                cloudPermissibleGroup.properties().readString("color$"),
                cloudPermissibleGroup.name())));

        for (var i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        var addItemStack = new ItemStack(Material.LIME_DYE);
        var addItemStackMeta = addItemStack.getItemMeta();

        addItemStackMeta.displayName(MiniMessage.miniMessage().deserialize(translation.message("displayname-rank-add")).decoration(TextDecoration.ITALIC, false));

        var addItemStackLore = new ArrayList<Component>();
        for (var loreLine = 1; loreLine < Integer.MAX_VALUE; loreLine++) {
            if (!translation.containsMessage("lore-line-rank-add-" + loreLine)) break;
            addItemStackLore.add(MiniMessage.miniMessage().deserialize(translation.message("lore-line-rank-add-" + loreLine)).decoration(TextDecoration.ITALIC, false));
        }

        addItemStackMeta.lore(addItemStackLore);
        addItemStack.setItemMeta(addItemStackMeta);

        var setItemStack = new ItemStack(Material.YELLOW_DYE);
        var setItemStackMeta = setItemStack.getItemMeta();

        setItemStackMeta.displayName(MiniMessage.miniMessage().deserialize(translation.message("displayname-rank-set")).decoration(TextDecoration.ITALIC, false));

        var setItemStackLore = new ArrayList<Component>();
        for (var loreLine = 1; loreLine < Integer.MAX_VALUE; loreLine++) {
            if (!translation.containsMessage("lore-line-rank-set-" + loreLine)) break;
            setItemStackLore.add(MiniMessage.miniMessage().deserialize(translation.message("lore-line-rank-set-" + loreLine)).decoration(TextDecoration.ITALIC, false));
        }

        setItemStackMeta.lore(setItemStackLore);
        setItemStack.setItemMeta(setItemStackMeta);

        this.inventory.setItem(11, addItemStack);
        this.inventory.setItem(13, playerCOverview);
        this.inventory.setItem(15, setItemStack);

        InventoryListener.getInventoryMap().put(player.getUniqueId(), this);
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getSlot() == 11 || event.getSlot() == 15)) {
            return;
        }

        player.closeInventory();
        player.openInventory(new RanksTimeManiplationInventory(player,
                cloudPlayer,
                cloudPermissibleGroup,
                language,
                playerCOverview,
                event.getSlot() == 11)
                .inventory());
    }
}
