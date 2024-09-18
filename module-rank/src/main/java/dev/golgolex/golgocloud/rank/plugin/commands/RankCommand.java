package dev.golgolex.golgocloud.rank.plugin.commands;

import dev.golgolex.golgocloud.cloudapi.CloudAPI;
import dev.golgolex.golgocloud.rank.plugin.RankModulePlugin;
import dev.golgolex.golgocloud.rank.plugin.inventory.CategoryOverviewInventory;
import dev.golgolex.quala.command.*;
import org.bukkit.Bukkit;

@Command(command = "rank", permission = "dev.golgolex.cloud.rank")
public final class RankCommand {

    @DefaultCommand
    public void onDefaultCommand(CommandSender commandSender) {
        if (!(commandSender instanceof PlayerCommandSender playerCommandSender)) {
            return;
        }

        var language = CloudAPI.instance()
                .translationAPI()
                .getLanguage(it ->
                        CloudAPI.instance()
                                .cloudPlayerProvider()
                                .cloudPlayer(playerCommandSender.player().getUniqueId())
                                .language()
                                .equalsIgnoreCase(it.name()));
        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-command-rank-usage");

        commandSender.sendMessage(translation.message("command-usage-title"));
        for (var line = 1; line < Integer.MAX_VALUE; line++) {
            if (!translation.containsMessage("command-usage-line-" + line)) return;
            commandSender.sendMessage(translation.message("command-usage-line-" + line));
        }
    }

    @SubCommand(args = {"manipulate", "<player>"}, ignoreArgs = "manipulate")
    public void onManipulateCommand(CommandSender commandSender, String player) {
        if (!(commandSender instanceof PlayerCommandSender playerCommandSender)) {
            return;
        }

        var language = CloudAPI.instance()
                .translationAPI()
                .getLanguage(it ->
                        CloudAPI.instance()
                                .cloudPlayerProvider()
                                .cloudPlayer(playerCommandSender.player().getUniqueId())
                                .language()
                                .equalsIgnoreCase(it.name()));
        var translation = CloudAPI.instance().translationAPI().repositoryOf(language, "cloud-module-rank", "ingame-command-rank-manipulate");

        var target = CloudAPI.instance().cloudPlayerProvider().cloudPlayer(player);
        if (target == null) {
            commandSender.sendMessage(translation.message("player-not-found", player));
            return;
        }

        Bukkit.getScheduler().runTask(RankModulePlugin.getProvidingPlugin(RankModulePlugin.class), () -> playerCommandSender.player().openInventory(new CategoryOverviewInventory(playerCommandSender.player(), target.uniqueId()).getInventory()));
    }

}
