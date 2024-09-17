package dev.golgolex.golgocloud.rank.plugin.config;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.configuration.ConfigurationClass;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.rank.plugin.PermissibleGroupCategory;
import dev.golgolex.quala.common.json.JsonDocument;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PermissibleGroupCategoryConfiguration extends ConfigurationClass {
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
        )));
    }
}
