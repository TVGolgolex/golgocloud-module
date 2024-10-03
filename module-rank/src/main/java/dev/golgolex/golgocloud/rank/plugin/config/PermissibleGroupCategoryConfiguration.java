package dev.golgolex.golgocloud.rank.plugin.config;

import com.google.common.reflect.TypeToken;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.rank.plugin.PermissibleGroupCategory;
import dev.golgolex.quala.common.json.JsonDocument;
import dev.golgolex.quala.config.json.JsonConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PermissibleGroupCategoryConfiguration extends JsonConfiguration {
    public PermissibleGroupCategoryConfiguration(@NotNull File configurationDirectory) {
        super("permissible-groups-categories", configurationDirectory);
    }

    @Override
    public JsonDocument defaultConfiguration() {
        return new JsonDocument("categories", new ArrayList<>(List.of(
                new PermissibleGroupCategory("default", "green", 9, new ArrayList<>(Collections.singletonList(CloudAPI.instance().cloudPermissionService().cloudPermissibleGroups()
                        .stream()
                        .filter(CloudPermissibleGroup::defaultGroup)
                        .findFirst()
                        .orElseThrow().uuid())))
        ))).write("inventory-size", 3 * 9);
    }

    public List<PermissibleGroupCategory> categories() {
        return this.configuration().readObject("categories", new TypeToken<List<PermissibleGroupCategory>>() {
        }.getType());
    }
}
