package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.message.Message;
import net.playlegend.spigot.groupsystem.mojang.UUIDFetcher;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SetGroupCommand extends AbstractCommand {
    public SetGroupCommand() {
        super("setgroup", "Sets the group of a player", Collections.emptyList());
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args)
            throws NotEnoughArgumentsException, NoPermissionException, PlayerNotFoundException, ServerException, GroupNotFoundException {

        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.set")) {
            throw new NoPermissionException("playlegend.groups.group.set");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setgroup <player> <group> (<days> <hours> <minutes>)");
        }

        String playerString = args[0];
        UUID uuid = UUIDFetcher.getUUID(playerString);

        if (uuid == null) {
            throw new PlayerNotFoundException(playerString);
        }

        try {
            if (!service.userExists(uuid).get()) {
                throw new PlayerNotFoundException(playerString);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        String groupString = args[1];

        try {
            if (!service.groupExists(groupString.toLowerCase()).get())
                throw new GroupNotFoundException(groupString);
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        if (!sender.hasPermission("playlegend.groups.group.set." + groupString)) {
            throw new NoPermissionException("playlegend.groups.group.set." + groupString);
        }

        GroupGeneric group;

        try {
            Optional<GroupGeneric> groupOptional = service.getGroup(groupString).get();

            if (groupOptional.isEmpty()) {
                throw new GroupNotFoundException(groupString);
            }

            group = groupOptional.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        int minutes = 0;
        Timestamp timestamp = null;

        if (args.length >= 3) {
            try {
                minutes += Integer.parseInt(args[2]) * 24 * 60;
            } catch (NumberFormatException e) {
                sender.sendMessage(new Message("commands.setgroup.time-number").get());

                return false;
            }
        }

        if (args.length >= 4) {
            try {
                minutes += Integer.parseInt(args[3]) * 60;
            } catch (NumberFormatException e) {
                sender.sendMessage(new Message("commands.setgroup.time-number").get());

                return false;
            }
        }

        if (args.length >= 5) {
            try {
                minutes += Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(new Message("commands.setgroup.time-number").get());

                return false;
            }
        }

        timestamp = new Timestamp(System.currentTimeMillis() + ((long) minutes *60*1000));

        service.setGroup(uuid, groupString, timestamp);

        Message msg = new Message("commands.setgroup.group-set", group);
        msg.setUsername(playerString);

        int stringDays = minutes/60/24;
        int stringHours = (minutes-stringDays*60*24)/60;
        int stringMinutes = minutes-stringDays*60*24-stringHours*60;

        msg.setTime(minutes > 0 ? stringDays + "d, " + stringHours + "h, " + stringMinutes + "m" : null);

        sender.sendMessage(msg.get());

        return true;
    }
}
