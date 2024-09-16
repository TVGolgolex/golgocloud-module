package dev.golgolex.golgocloud.rank.module.commands;

import dev.golgolex.golgocloud.base.CloudBase;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleGroup;
import dev.golgolex.golgocloud.common.permission.PermissionStructure;
import dev.golgolex.quala.command.Command;
import dev.golgolex.quala.command.CommandSender;
import dev.golgolex.quala.command.DefaultCommand;
import dev.golgolex.quala.command.SubCommand;

@Command(command = "permissions", description = "handle the permissions", aliases = "perms")
public class PermissionsCommand {

    @DefaultCommand
    public void onDefault(CommandSender sender) {
        sender.sendMessage("&1permissions &2'&3list&2' &2<&1groups&2/&1users&2>");
    }

    @SubCommand(args = {"list", "<type>"}, ignoreArgs = "list")
    public void onList(CommandSender sender, String type) {
        switch (type.toUpperCase()) {
            case "GROUPS":
                sender.sendMessage("&1Groups&2:");

                for (var groups : CloudBase.instance().cloudPermissionService().cloudPermissibleGroups()) {
                    sender.sendMessage("&1Name&2: '&3" + groups.name() + "&2'");
                    sender.sendMessage("&1ID&2: '&3" + groups.uuid() + "&2'");
                    sender.sendMessage("&1Sort&2: '&3" + groups.sort() + "&2'");
                    sender.sendMessage("&1Default group&2: '&3" + groups.defaultGroup() + "&2'");

                    sender.sendMessage("&1Properties&2: (");

                    sender.sendMessage("&1Permissions&2:");
                    for (var permission : groups.permissions()) {
                        sender.sendMessage("&2- '&1" + permission.key() + "&2' &2'&3" + permission.permission() + "&2'");
                    }
                }

                break;
            case "USERS":
                break;
        }
    }
}
