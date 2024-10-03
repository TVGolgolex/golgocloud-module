package dev.golgolex.golgocloud.advancedbranding.module;



/*
 * Copyright 2023-2024 golgocloud-module contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.golgolex.golgocloud.advancedbranding.module.configuration.ServiceAdvancedBrandingConfiguration;
import dev.golgolex.golgocloud.base.CloudBase;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.quala.common.json.JsonDocument;
import dev.golgolex.quala.config.json.JsonConfigurationService;
import dev.golgolex.quala.module.Module;
import dev.golgolex.quala.module.ModuleLogger;
import dev.golgolex.quala.module.ModuleProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class AdvancedBrandingModule extends Module {

    private final JsonConfigurationService jsonConfigurationService;

    public AdvancedBrandingModule(@NotNull ModuleProperties moduleProperties,
                                  @NotNull File modulesDirectory,
                                  @Nullable ModuleLogger moduleLogger) {
        super(moduleProperties, modulesDirectory, moduleLogger);
        this.jsonConfigurationService = new JsonConfigurationService(dataDirectory());
    }

    @Override
    public void initialize(@NotNull Object[] objects) {
        this.updateGroups();
    }

    @Override
    public void activate(@NotNull Object[] objects) {
        CloudBase.instance().messagingHandler().runListener("golgocloud:internal:modules", "request-advanced-branding-configurations",
                jsonDocument -> {
                    if (!jsonDocument.contains("requester-id")) {
                        this.moduleLogger().print(ModuleLogger.LogLevel.ERROR, "No requester id for reply found.");
                        return;
                    }

                    if (!jsonDocument.contains("request-configuration")) {
                        this.moduleLogger().print(ModuleLogger.LogLevel.ERROR, "No request-configuration found.");
                        return;
                    }

                    var requesterId = UUID.fromString(jsonDocument.readString("requester-id"));
                    var requestConfiguration = jsonDocument.readString("request-configuration");

                    this.jsonConfigurationService.configurationOptional(requestConfiguration.toLowerCase() + "-branding-config"
                    ).ifPresentOrElse(jsonConfiguration -> {
                        var serviceAdvancedBrandingConfiguration = (ServiceAdvancedBrandingConfiguration) jsonConfiguration;
                        CloudBase.instance()
                                .messagingHandler()
                                .openChannelMessage("golgocloud:internal:modules", "receive-advanced-branding-configurations",
                                        new JsonDocument("requester-id", requesterId)
                                                .write(requestConfiguration.toLowerCase(), serviceAdvancedBrandingConfiguration.configuration()));
                    }, () -> this.moduleLogger().print(ModuleLogger.LogLevel.ERROR, "No configuration request-configuration " + requestConfiguration + " for found."));
                });
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void refresh() {
        this.updateGroups();
    }

    private void updateGroups() {
        for (var cloudGroup : CloudBase.instance().groupProvider().cloudGroups()) {
            this.jsonConfigurationService.configurationOptional(cloudGroup.name().toLowerCase() + "-branding-config").ifPresentOrElse(_ ->
                            this.moduleLogger().print(ModuleLogger.LogLevel.INFO, "&1Loaded advanced branding &2'&3" + cloudGroup.name() + "&2'"),
                    () -> this.jsonConfigurationService.addConfiguration(new ServiceAdvancedBrandingConfiguration(
                            cloudGroup.name().toLowerCase(),
                            this.jsonConfigurationService.configurationDirectory()
                    )));
        }
    }
}
