package dev.golgolex.golgocloud.advancedbranding.plugin;



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

import dev.golgolex.golgocloud.advancedbranding.plugin.listener.ServerListPingListener;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.messaging.listener.ChannelMessageListener;
import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.quala.common.json.JsonDocument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class AdvancedBrandingPlugin extends JavaPlugin {

    private JsonDocument advancedConfiguration;

    @Override
    public void onEnable() {
        CloudAPI.instance().messagingHandler().runListener(new ChannelMessageListener(
                "golgocloud:internal:modules",
                "receive-advanced-branding-configurations",
                jsonDocument -> {
                    if (jsonDocument.contains("requester-id") && jsonDocument.readString("requester-id")
                            .equalsIgnoreCase(CloudPaperPlugin.instance().thisServiceUUID().toString())) {
                        this.advancedConfiguration = jsonDocument.readJsonDocument(CloudPaperPlugin.instance().thisGroupName().toLowerCase());
                        this.getLogger().log(Level.INFO, "Received updated AdvancedConfiguration");
                    }
                }));

        this.reloadConfigurations();

        Bukkit.getPluginManager().registerEvents(new ServerListPingListener(this), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public void reloadConfigurations() {
        CloudAPI.instance().messagingHandler().openChannelMessage("golgocloud:internal:modules", "request-advanced-branding-configurations",
                new JsonDocument("requester-id", CloudPaperPlugin.instance().thisServiceUUID().toString())
                        .write("request-configuration", CloudPaperPlugin.instance().thisGroupName()));
    }
}
