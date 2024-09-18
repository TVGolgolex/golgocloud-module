package dev.golgolex.golgocloud.rank.plugin.inventory;

import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.golgocloud.rank.plugin.config.PermissibleGroupCategoryConfiguration;
import dev.golgolex.quala.translation.basic.TextUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public final class CategoryOverviewInventory {

    private final Player player;
    private final UUID target;
    private final Inventory inventory;

    public CategoryOverviewInventory(@NotNull Player player, @NotNull UUID target) {
        this.player = player;
        this.target = target;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("&7Overview"));

        CloudPaperPlugin.instance().instanceConfigurationService().configurationOptional("permissible-groups-categories").ifPresent(permissibleGroupCategories -> {
            var config = (PermissibleGroupCategoryConfiguration) permissibleGroupCategories;

            for (var category : config.categories()) {
                var itemStack = new ItemStack(Material.LEATHER_HELMET);
                var leatherMeta = (LeatherArmorMeta) itemStack.getItemMeta();

                leatherMeta.displayName(MiniMessage.miniMessage().deserialize(TextUtil.buildMessage("<color:{0}> {1}</color>", category.color(), category.name())));
                leatherMeta.setColor(org.bukkit.Color.fromRGB(Integer.parseInt(category.color().replace("#", ""), 16)));
                leatherMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                itemStack.setItemMeta(leatherMeta);
                this.inventory.setItem(category.invSlot(), itemStack);
            }
        });

    }
}
