package dev.golgolex.golgocloud.rank.plugin;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.golgocloud.rank.plugin.commands.RankCommand;
import dev.golgolex.golgocloud.rank.plugin.config.PermissibleGroupCategoryConfiguration;
import dev.golgolex.golgocloud.rank.plugin.listener.InventoryListener;
import dev.golgolex.quala.translation.basic.listener.TranslationsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RankModulePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CloudAPI.instance().nettyClient().connectionFuture().thenAccept(_ -> {
            CloudPaperPlugin.instance().instanceConfigurationService().addConfiguration(new PermissibleGroupCategoryConfiguration(
                    CloudPaperPlugin.instance().instanceConfigurationService().configurationDirectory()
            ));

            TranslationsManager.registerListener(RankLanguage.class, CloudAPI.instance().translationAPI());
        });
        CloudPaperPlugin.instance().paperCommandService().registerCommand(new RankCommand());

        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {

    }
}