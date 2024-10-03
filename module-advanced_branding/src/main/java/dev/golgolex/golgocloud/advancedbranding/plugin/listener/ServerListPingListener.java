package dev.golgolex.golgocloud.advancedbranding.plugin.listener;



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

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.google.common.reflect.TypeToken;
import dev.golgolex.golgocloud.advancedbranding.plugin.AdvancedBrandingPlugin;
import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.common.serverbranding.ServerBrandStyle;
import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.quala.common.Quala;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ServerListPingListener implements Listener {

    private final AdvancedBrandingPlugin advancedBrandingPlugin;

    public ServerListPingListener(AdvancedBrandingPlugin advancedBrandingPlugin) {
        this.advancedBrandingPlugin = advancedBrandingPlugin;
    }

    @EventHandler
    public void onPaperPing(PaperServerListPingEvent event) {
        if (event.isCancelled()) return;

        var host = "";

        if (event.getClient().getVirtualHost() != null &&
                event.getClient().getVirtualHost().getHostName() != null) {
            var hostname = event.getClient().getVirtualHost().getHostName();
            var list = Arrays.stream(hostname.split("\\.")).toList();
            try {
                var domainName = list.get((list.size() - 2));
                var tld = list.getLast();
                host = domainName + "." + tld;
            } catch (Exception ignore) {
            }
        }

        ServerBrandStyle serverBrandStyle = null;
        if (!host.isEmpty()) {
            var finalHost = host;
            serverBrandStyle = CloudAPI.instance().serverBrandingService()
                    .loadedBrands()
                    .stream()
                    .filter(serverBrandStyles -> serverBrandStyles.domain().equalsIgnoreCase(finalHost))
                    .findFirst()
                    .orElse(null);
        }

        if (serverBrandStyle == null) {
            serverBrandStyle = CloudAPI.instance().serverBrandingService().anyDefault();
        }

        var config = advancedBrandingPlugin.getAdvancedConfiguration().readJsonDocument(serverBrandStyle.name().toLowerCase());
        var iconPath = config.readString("server-icon-file-path");
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                var serverIcon = Bukkit.loadServerIcon(new File(iconPath));
                event.setServerIcon(serverIcon);
            } catch (Exception ignore) {
            }
        }

        CloudAPI.instance().cloudGroupProvider().cloudGroup(CloudPaperPlugin.instance().thisGroupName())
                .ifPresent(cloudGroup -> {
                    String topLine;
                    String bottomLine;
                    if (cloudGroup.maintenance()) {
                        topLine = this.randomEntryFormList(config.readObject("maintenance-motd-line-top", new TypeToken<List<String>>() {
                        }.getType()));
                        bottomLine = this.randomEntryFormList(config.readObject("maintenance-motd-line-bottom", new TypeToken<List<String>>() {
                        }.getType()));
                        event.setProtocolVersion(2);
                        event.setVersion(this.doReplacing(this.randomEntryFormList(config.readObject("maintenance-protocol-line", new TypeToken<List<String>>() {
                        }.getType()))));
                    } else {
                        topLine = this.randomEntryFormList(config.readObject("motd-line-top", new TypeToken<List<String>>() {
                        }.getType()));
                        bottomLine = this.randomEntryFormList(config.readObject("motd-line-bottom", new TypeToken<List<String>>() {
                        }.getType()));
                        List<String> onlineProtocols = config.readObject("protocol-line", new TypeToken<List<String>>() {
                        }.getType());
                        if (!onlineProtocols.isEmpty()) {
                            event.setProtocolVersion(2);
                            event.setVersion(this.doReplacing(this.randomEntryFormList(onlineProtocols)));
                        }
                    }
                    event.motd(this.getComponent(topLine)
                            .append(Component.text("\n")
                                    .append(this.getComponent(bottomLine))));
                    event.setNumPlayers(CloudAPI.instance().cloudPlayerProvider().onlineCloudPlayers().size());
                });

    }

    private String doReplacing(@NotNull String raw) {
        return raw.replace("%serviceId%", CloudPaperPlugin.instance().thisServiceId())
                .replace("%serviceGroup%", CloudPaperPlugin.instance().thisGroupName())
                .replace("%Global_OnlinePlayers%", String.valueOf(CloudAPI.instance().cloudPlayerProvider().onlineCloudPlayers().size()))
                .replace("%Server_OnlinePlayers%", String.valueOf(Bukkit.getOnlinePlayers().size()));
    }

    private Component getComponent(@NotNull String s) {
        s = this.doReplacing(s);
        Component component;
        try {
            component = MiniMessage.miniMessage().deserialize(s);
        } catch (Exception ignore) {
            component = Component.text(s);
        }
        return component;
    }

    private String randomEntryFormList(@NotNull List<String> list) {
        return list.get(Quala.RANDOM.nextInt(list.size()));
    }
}
