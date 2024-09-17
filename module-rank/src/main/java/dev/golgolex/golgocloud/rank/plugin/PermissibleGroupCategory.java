package dev.golgolex.golgocloud.rank.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record PermissibleGroupCategory(@NotNull String name, @NotNull String color, int invSlot, @NotNull List<UUID> groups) {
}
