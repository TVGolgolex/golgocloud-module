package dev.golgolex.golgocloud.rank.plugin.inventory;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.user.CloudPlayer;
import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.golgocloud.rank.plugin.config.PermissibleGroupCategoryConfiguration;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.golgocloud.rank.util.TimeConverter;
import dev.golgolex.quala.translation.basic.Language;
import dev.golgolex.quala.translation.basic.TextUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Getter
public final class CategoryOverviewInventory implements RankInventory {

    private final Player player;
    private final UUID target;
    private CloudPlayer targetCloudPlayer;
    private Language language;
    private ItemStack playerItemStack;
    private Inventory inventory;

    public CategoryOverviewInventory(@NotNull Player player,
                                     @NotNull UUID target) {
        this.player = player;
        this.target = target;

        CloudPaperPlugin.instance().instanceConfigurationService().configurationOptional("permissible-groups-categories").ifPresent(permissibleGroupCategories -> {
            var config = (PermissibleGroupCategoryConfiguration) permissibleGroupCategories;
            this.inventory = Bukkit.createInventory(null, config.configuration().readInteger("inventory-size"), Component.text("§7Overview"));

            for (var i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }

            var cloudPlayer = CloudAPI.instance().cloudPlayerProvider().cloudPlayer(target);
            var permissionUser = CloudAPI.instance().cloudPermissionService().permissibleUser(cloudPlayer.uniqueId());
            var highestPermissibleGroup = permissionUser.highest(CloudAPI.instance().cloudPermissionService());
            var playerItemStack = new ItemStack(Material.PLAYER_HEAD);
            var playerItemMeta = playerItemStack.getItemMeta();
            var language = CloudAPI.instance().translationAPI().getLanguage(it ->
                    CloudAPI.instance()
                            .cloudPlayerProvider()
                            .cloudPlayer(player.getUniqueId())
                            .language()
                            .equalsIgnoreCase(it.name()));
            var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-inventory-category-overview");

            playerItemMeta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<dark_gray>»</dark_gray> <color:{0}><b>{1}</b></color>",
                    highestPermissibleGroup.properties().readString("color$"),
                    cloudPlayer.username())).decoration(TextDecoration.ITALIC, false));

            var lore = new ArrayList<>(Arrays.asList(
                    Component.text("§8§m                  §r"),
                    MiniMessage.miniMessage().deserialize(translation.message("lore-highest-permissible-group",
                                    highestPermissibleGroup.properties().readString("color$"), highestPermissibleGroup.name()))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty()
            ));

            for (var groupEntry : permissionUser.groupEntries()) {
                var group = CloudAPI.instance().cloudPermissionService().permissibleGroup(groupEntry.groupId());
                if (group == null) continue;

                lore.add(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}>»</color> <color:{0}><b>{1}</b></color> <dark_gray>-</dark_gray> <gray>{2}</gray>",
                                group.properties().readString("color$"),
                                group.name(),
                                TimeConverter.convertMillisToDateTime(groupEntry.duration())))
                        .decoration(TextDecoration.ITALIC, false));
            }

            playerItemMeta.lore(lore);

            if (cloudPlayer.meta().contains("player-skin-data")) {
                var playerSkinData = cloudPlayer.meta().readJsonDocument("player-skin-data");
                var profile = Bukkit.createProfile(UUID.randomUUID().toString().split("-")[0]);
                profile.setProperty(new ProfileProperty("textures", playerSkinData.readString("value"), playerSkinData.readString("signature")));
                SkullMeta skullMeta = (SkullMeta) playerItemMeta;
                skullMeta.setPlayerProfile(profile);
                playerItemStack.setItemMeta(skullMeta);
            }

            inventory.setItem(4, playerItemStack);

            for (var category : config.categories()) {
                var itemStack = new ItemStack(Material.LEATHER_HELMET);
                var leatherMeta = (LeatherArmorMeta) itemStack.getItemMeta();

                leatherMeta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}>{1}</color>", category.color(), category.name())).decoration(TextDecoration.ITALIC, false));
                leatherMeta.setColor(org.bukkit.Color.fromRGB(Integer.parseInt(category.color().replace("#", ""), 16)));
                leatherMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                leatherMeta.addItemFlags(ItemFlag.HIDE_DYE);
                leatherMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

                itemStack.setItemMeta(leatherMeta);
                this.inventory.setItem(category.invSlot(), itemStack);
            }

            this.targetCloudPlayer = cloudPlayer;
            this.language = language;
            this.playerItemStack = playerItemStack;
        });

        InventoryListener.getInventoryMap().put(player.getUniqueId(), this);
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getSlot() == 4) {
            player.closeInventory();
            player.openInventory(new PlayerOperationInventory(player,
                    targetCloudPlayer,
                    language,
                    playerItemStack
            ).inventory());
            return;
        }

        CloudPaperPlugin.instance().instanceConfigurationService().configurationOptional("permissible-groups-categories").ifPresent(permissibleGroupCategories -> {
            var config = (PermissibleGroupCategoryConfiguration) permissibleGroupCategories;

            for (var category : config.categories()) {
                if (event.getSlot() == category.invSlot()) {
                    player.closeInventory();
                    player.openInventory(new RanksCategoryInventory(player,
                            targetCloudPlayer,
                            category,
                            language,
                            playerItemStack)
                            .inventory());
                }
            }
        });

    }
}
