package dev.golgolex.golgocloud.rank.plugin;

import dev.golgolex.golgocloud.plugin.paper.CloudPaperPlugin;
import dev.golgolex.golgocloud.rank.plugin.config.PermissibleGroupCategoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RankModulePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CloudPaperPlugin.instance().instanceConfigurationService().addConfiguration(new PermissibleGroupCategoryConfiguration(
                CloudPaperPlugin.instance().instanceConfigurationService().configurationDirectory()
        ));
    }

    @Override
    public void onDisable() {

    }
}