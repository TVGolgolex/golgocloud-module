package dev.golgolex.golgocloud.advancedbranding.module.configuration;



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

import dev.golgolex.golgocloud.base.CloudBase;
import dev.golgolex.golgocloud.base.configuration.ServerBrandingConfiguration;
import dev.golgolex.quala.common.json.JsonDocument;
import dev.golgolex.quala.config.json.JsonConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdvancedBrandingConfiguration extends JsonConfiguration {

    public ServiceAdvancedBrandingConfiguration(@NotNull String groupName, @NotNull File configurationDirectory) {
        super(groupName + "-branding-config", configurationDirectory);
    }

    @Override
    public JsonDocument defaultConfiguration() {
        var document = new JsonDocument();
        this.recheckConfig(document);
        return document;
    }

    public void recheckConfig(@NotNull JsonDocument jsonDocument) {
        CloudBase.instance().configurationService().configurationOptional("server-branding").ifPresent(configurationClass -> {
            var serverBrandingConfiguration = (ServerBrandingConfiguration) configurationClass;
            for (var style : serverBrandingConfiguration.serverBrandStyles()) {
                if (jsonDocument.contains(style.name().toLowerCase())) continue;
                jsonDocument.write(style.name().toLowerCase(),
                        new JsonDocument()
                                .write("server-icon-file-path", "")
                                .write("motd-line-top", new ArrayList<>(List.of("GolgoCloud")))
                                .write("motd-line-bottom", new ArrayList<>(List.of("Online MOTD line")))
                                .write("protocol-line", new ArrayList<>())
                                .write("maintenance-motd-line-top", new ArrayList<>(List.of("GolgoCloud")))
                                .write("maintenance-motd-line-bottom", new ArrayList<>(List.of("Maintenance MOTD line")))
                                .write("maintenance-protocol-line", new ArrayList<>(List.of("Maintenance protocol")))
                );
            }
        });
    }
}
