package dev.golgolex.golgocloud.rank.module.commands;

import dev.golgolex.golgocloud.base.CloudBase;
import dev.golgolex.golgocloud.common.permission.CloudPermissibleUser;
import dev.golgolex.quala.command.*;
import org.jline.reader.Candidate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(command = "permissions", description = "handle the permissions", aliases = "perms")
public class PermissionsCommand {

    @DefaultCommand
    public void onDefault(CommandSender sender) {
        sender.sendMessage("&1permissions &3list &2<&3groups&2/&3users&2>");
        sender.sendMessage("&1permissions &3manipulate user &2<&3name&2> &2<&3set/add&2> &2<&3group&2> &2<&3time&2> &2<&3timeUnit&2>");
        sender.sendMessage("&1permissions &3info user &2<&3name&2>");
    }

    @SubCommand(args = {"list", "<type>"}, ignoreArgs = "list")
    public void onList(CommandSender sender, String type) {
        switch (type.toUpperCase()) {
            case "GROUPS":
                sender.sendMessage("&1Groups&2: (&3" + CloudBase.instance().cloudPermissionService().cloudPermissibleGroups().size() + "&2");

                for (var groups : CloudBase.instance().cloudPermissionService().cloudPermissibleGroups()) {
                    sender.sendMessage("&1Name&2: '&3" + groups.name() + "&2'");
                    sender.sendMessage("&1ID&2: '&3" + groups.uuid() + "&2'");
                    sender.sendMessage("&1Sort&2: '&3" + groups.sort() + "&2'");
                    sender.sendMessage("&1Default group&2: '&3" + groups.defaultGroup() + "&2'");
                    sender.sendMessage("&1Properties&2: (&3" + groups.properties().jsonObject().size() + "&2)");

                    sender.sendMessage("&1Included permission groups&2: (&3" + groups.groups().size() + "&2");
                    for (var groupId : groups.groups()) {
                        var group = CloudBase.instance().cloudPermissionService().permissibleGroup(groupId);
                        if (group == null) continue;
                        sender.sendMessage("&2- '&1" + group.uuid() + "&2' &2'&3" + group.name() + "&2'");
                    }

                    sender.sendMessage("&1Permissions&2: (&3" + groups.permissions().size() + "&2)");
                    for (var permission : groups.permissions()) {
                        sender.sendMessage("&2- '&1" + permission.key() + "&2' &2'&3" + permission.permission() + "&2'");
                    }
                }
                break;
            case "USERS":
                sender.sendMessage("&1Users&2: (&3" + CloudBase.instance().cloudPermissionService().cloudPermissibleGroups().size() + "&2");
                for (var users : CloudBase.instance().cloudPermissionService().cloudPermissibleUsers()) {
                    sender.sendMessage("&1UUID&2: '&3" + users.uuid() + "&2'");
                    sender.sendMessage("&1Properties&2: (&3" + users.properties().jsonObject().size() + "&2)");

                    sender.sendMessage("&1Highest permission group&2: '&3" + users.highest(CloudBase.instance().cloudPermissionService()).name() + "&2'");

                    sender.sendMessage("&1Permission groups: (&3" + users.groupEntries().size() + "&2)");
                    for (var groupEntry : users.groupEntries()) {
                        var group = CloudBase.instance().cloudPermissionService().permissibleGroup(groupEntry.groupId());
                        if (group == null) continue;
                        sender.sendMessage("&2- '&1" + group.uuid() + "&2' &2'&3" + group.name() + "&2' &1for &2'&3" + groupEntry.duration() + "&2'");
                    }

                    sender.sendMessage("&1Permissions&2: (&3" + users.permissions().size() + "&2)");
                    for (var permission : users.permissions()) {
                        sender.sendMessage("&2- '&1" + permission.key() + "&2' &2'&3" + permission.permission() + "&2'");
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type.toUpperCase());
        }
    }

    @SubCommand(args = {"manipulate", "user", "<name>", "<set/add>", "<group>"}, ignoreArgs = {"manipulate", "user"})
    public void onManipulate(CommandSender sender, String name, String setOrAdd, String group) {
        this.onManipulate(sender, name, setOrAdd, group, null, null);
    }

    @SubCommand(args = {"manipulate", "user", "<name>", "<set/add>", "<group>", "<time>", "<timeUnit>"}, ignoreArgs = {"manipulate", "user"})
    public void onManipulate(CommandSender sender, String name, String setOrAdd, String group, String time, String timeUnit) {
        var cloudPlayer = CloudBase.instance().playerProvider().cloudPlayer(name);
        if (cloudPlayer == null) {
            sender.sendMessage("&6Player not found!");
            return;
        }

        var permissibleGroup = CloudBase.instance().cloudPermissionService().permissibleGroup(group);
        if (permissibleGroup == null) {
            sender.sendMessage("&6Group not found!");
            return;
        }

        var permissionUser = CloudBase.instance().cloudPermissionService().permissibleUser(cloudPlayer.uniqueId());
        if (permissionUser == null) {
            sender.sendMessage("&6User not found!");
            return;
        }

        long timeLong = 0;
        String timeString;
        if (time == null && timeUnit == null) {
            timeLong = -1;
            timeString = "forever";
        } else {
            var timeUnitEnum = Arrays.stream(TimeUnit.values()).filter(timeUnit1 -> timeUnit1.name().equalsIgnoreCase(timeUnit)).findFirst().orElse(null);
            if (timeUnitEnum == null) {
                sender.sendMessage("&6Time unit not found!");
                return;
            }

            try {
                if (time != null) {
                    timeLong = Long.parseLong(time);
                }
            } catch (Exception exception) {
                sender.sendMessage("&6No valid time format!");
                return;
            }
            timeString = timeLong + " " + timeUnitEnum.name().toLowerCase();
            timeLong = System.currentTimeMillis() + timeUnitEnum.toMillis(timeLong);
        }

        if (setOrAdd.equalsIgnoreCase("set")) {
            permissionUser.groupEntries().clear();
        }

        permissionUser.groupEntries().add(new CloudPermissibleUser.GroupEntry(
                permissibleGroup.uuid(),
                timeLong
        ));

        CloudBase.instance().cloudPermissionService().updatePermissibleUser(permissionUser);
        sender.sendMessage("&1The user &2'&3" + cloudPlayer.username() + "&2' &1was updated&2. &1New permissible group &2'&3" + permissibleGroup.name() + "&2'&1, &2'&3" + timeString + "&2'");
    }

    @SubCommandCompleter(completionPattern = {"manipulate", "user", "<name>", "<set/add>", "<group>", "<time>", "<timeUnit>"}, subCommand = "manipulate")
    public void complete(int subIndex, List<Candidate> completions) {
        switch (subIndex) {
            case 3 -> completions.addAll(CloudBase.instance()
                    .playerProvider()
                    .cloudPlayers()
                    .stream()
                    .map(cloudPlayer -> new Candidate(cloudPlayer.username())).toList());
            case 4 -> completions.addAll(Arrays.asList(new Candidate("set"),
                    new Candidate("add")));
            case 5 -> completions.addAll(CloudBase.instance()
                    .cloudPermissionService()
                    .cloudPermissibleGroups()
                    .stream()
                    .map(permissibleGroup -> new Candidate(permissibleGroup.name())).toList());
            case 7 -> {
                completions.addAll(Arrays.stream(TimeUnit.values())
                        .map(timeUnit -> new Candidate(timeUnit.name()))
                        .toList());
            }
        }
    }

    /*@SubCommandCompleter(completionPattern = {"manipulate", "<name>", "<group>"}, subCommand = "manipulate")
    public void complete(int subIndex, List<Candidate> completions) {
        switch (subIndex) {
            case 2 -> completions.addAll(CloudBase.instance()
                    .playerProvider()
                    .cloudPlayers()
                    .stream()
                    .map(cloudPlayer -> new Candidate(cloudPlayer.username())).toList());
            case 3 -> completions.addAll(CloudBase.instance()
                    .cloudPermissionService()
                    .cloudPermissibleGroups()
                    .stream()
                    .map(permissibleGroup -> new Candidate(permissibleGroup.name())).toList());
        }
    }*/
}
