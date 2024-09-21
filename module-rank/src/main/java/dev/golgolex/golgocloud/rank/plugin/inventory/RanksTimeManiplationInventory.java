package dev.golgolex.golgocloud.rank.plugin.inventory;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleUser;
import dev.golgolex.golgocloud.common.user.CloudPlayer;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.quala.translation.basic.Language;
import dev.golgolex.quala.translation.basic.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class RanksTimeManiplationInventory implements RankInventory {

    private final Player player;
    private final CloudPlayer cloudPlayer;
    private final CloudPermissibleGroup cloudPermissibleGroup;
    private final Language language;
    private final Inventory inventory;
    private final ItemStack playerCOverview;
    private final Map<String, Integer> timings = new ConcurrentHashMap<>();
    private final boolean add;

    public RanksTimeManiplationInventory(@NotNull Player player,
                                         @NotNull CloudPlayer cloudPlayer,
                                         @NotNull CloudPermissibleGroup cloudPermissibleGroup,
                                         @NotNull Language language,
                                         @NotNull ItemStack playerCOverview,
                                         boolean add) {
        this.player = player;
        this.cloudPlayer = cloudPlayer;
        this.cloudPermissibleGroup = cloudPermissibleGroup;
        this.language = language;
        this.playerCOverview = playerCOverview;
        this.add = add;

        this.inventory = Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}>{1}</color>",
                cloudPermissibleGroup.properties().readString("color$"), cloudPermissibleGroup.name())));

        for (var i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        this.inventory.setItem(10, buildTimeUnitItem(language, "seconds"));
        this.inventory.setItem(11, buildTimeUnitItem(language, "minutes"));
        this.inventory.setItem(12, buildTimeUnitItem(language, "hours"));
        this.inventory.setItem(13, buildTimeUnitItem(language, "days"));
        this.inventory.setItem(14, buildTimeUnitItem(language, "weeks"));
        this.inventory.setItem(15, buildTimeUnitItem(language, "months"));
        this.inventory.setItem(16, buildTimeUnitItem(language, "years"));

        this.inventory.setItem(22, playerCOverview);

        var checkItem = new ItemStack(Material.PLAYER_HEAD);
        var checkItemMeta = checkItem.getItemMeta();
        {
            var profile = Bukkit.createProfile(UUID.randomUUID().toString().split("-")[0]);
            profile.setProperty(new ProfileProperty(
                    "textures",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0="));
            SkullMeta skullMeta = (SkullMeta) checkItem.getItemMeta();
            skullMeta.setPlayerProfile(profile);
            checkItemMeta.displayName(Component.text("§a✔"));
            checkItem.setItemMeta(skullMeta);
        }
        this.inventory.setItem(48, checkItem);

        var abortItem = new ItemStack(Material.PLAYER_HEAD);
        var abortItemMeta = abortItem.getItemMeta();
        {
            var profile = Bukkit.createProfile(UUID.randomUUID().toString().split("-")[0]);
            profile.setProperty(new ProfileProperty(
                    "textures",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="));
            SkullMeta skullMeta = (SkullMeta) abortItemMeta;
            skullMeta.setPlayerProfile(profile);
            abortItemMeta.displayName(Component.text("§c✖"));
            abortItem.setItemMeta(skullMeta);
        }
        this.inventory.setItem(51, abortItem);

        this.update();
        InventoryListener.getInventoryMap().put(player.getUniqueId(), this);
    }

    public void update() {
        this.inventory.setItem(28, buildDisplay("seconds"));
        this.inventory.setItem(29, buildDisplay("minutes"));
        this.inventory.setItem(30, buildDisplay("hours"));
        this.inventory.setItem(31, buildDisplay("days"));
        this.inventory.setItem(32, buildDisplay("weeks"));
        this.inventory.setItem(33, buildDisplay("months"));
        this.inventory.setItem(34, buildDisplay("years"));
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        var unit = "";
        switch (event.getSlot()) {
            case 10 -> unit = "seconds";
            case 11 -> unit = "minutes";
            case 12 -> unit = "hours";
            case 13 -> unit = "days";
            case 14 -> unit = "weeks";
            case 15 -> unit = "months";
            case 16 -> unit = "years";
            case 48 -> {
                var millis = 0L;

                for (var entry : this.timings.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("seconds")) {
                        millis += TimeUnit.SECONDS.toMillis(entry.getValue());
                    } else if (entry.getKey().equalsIgnoreCase("minutes")) {
                        millis += TimeUnit.MINUTES.toMillis(entry.getValue());
                    } else if (entry.getKey().equalsIgnoreCase("hours")) {
                        millis += TimeUnit.HOURS.toMillis(entry.getValue());
                    } else if (entry.getKey().equalsIgnoreCase("days")) {
                        millis += TimeUnit.DAYS.toMillis(entry.getValue());
                    } else if (entry.getKey().equalsIgnoreCase("weeks")) {
                        millis += TimeUnit.DAYS.toMillis(entry.getValue() * 7L);
                    } else if (entry.getKey().equalsIgnoreCase("months")) {
                        millis += TimeUnit.DAYS.toMillis(entry.getValue() * 30L);
                    } else if (entry.getKey().equalsIgnoreCase("years")) {
                        millis += TimeUnit.DAYS.toMillis(entry.getValue() * 365L);
                    }
                }

                var permissionUser = CloudAPI.instance().cloudPermissionService().permissibleUser(cloudPlayer.uniqueId());
                if (permissionUser == null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 10, 10);
                    return;
                }

                if (!add) {
                    permissionUser.groupEntries().clear();
                }

                permissionUser.groupEntries().add(new CloudPermissibleUser.GroupEntry(cloudPermissibleGroup.uuid(),
                        System.currentTimeMillis() + millis));
                CloudAPI.instance().cloudPermissionService().updatePermissibleUser(permissionUser);

                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 2);
                player.closeInventory();
                return;
            }
            case 51 -> player.closeInventory();
        }

        if (unit.isEmpty() && unit.isBlank())
            return;

        var time = this.timings.getOrDefault(unit, 0);
        switch (event.getClick()) {
            case LEFT -> time = (time - 1);
            case SHIFT_LEFT -> time = (time - 10);
            case RIGHT -> time = (time + 1);
            case SHIFT_RIGHT -> time = (time + 10);
        }

        this.timings.put(unit, time < 0 ? 0 : time);
        this.update();
    }

    private ItemStack buildDisplay(String unit) {
        var amount = this.timings.getOrDefault(unit, 0);

        var itemStack = new ItemStack((amount < 1 ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE), amount < 1 ? 1 : amount);
        var itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text((amount < 1 ? "§c" : "§a") + amount));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack buildTimeUnitItem(Language language, String key) {
        var itemStack = new ItemStack(Material.CLOCK);
        var itemMeta = itemStack.getItemMeta();
        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-inventory-rank-add_remove_time");
        var unitTranslation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "global-time-units");

        itemMeta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<dark_gray>»</dark_gray> <color:#ffe525><b>{0}</b></color>",
                unitTranslation.message(key))).decoration(TextDecoration.ITALIC, false));

        var loreLines = new ArrayList<Component>();
        for (var loreLine = 1; loreLine < Integer.MAX_VALUE; loreLine++) {
            if (!translation.containsMessage("lore-line-time-add-" + loreLine)) break;
            loreLines.add(MiniMessage.miniMessage().deserialize(translation.message("lore-line-time-add-" + loreLine)).decoration(TextDecoration.ITALIC, false));
        }

        itemMeta.lore(loreLines);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
