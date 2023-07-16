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

        if (!sender.hasPermission("playlegend.groups.group.create")) {
            throw new NoPermissionException("playlegend.groups.group.create");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setgroup <player> <group> (<time>)");
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

        int days = 0;
        Timestamp timestamp = null;

        if (args.length >= 3) {
            try {
                days = Integer.parseInt(args[2]);

                timestamp = new Timestamp(System.currentTimeMillis() + ((long) days * 1000 * 60 * 60 * 24));
            } catch (NumberFormatException e) {
                sender.sendMessage(new Message("commands.setgroup.time-number").get());

                return false;
            }
        }


        service.setGroup(uuid, groupString, timestamp);

        Message msg = new Message("commands.setgroup.group-set", group);
        msg.setUsername(playerString);
        msg.setDays(days);

        sender.sendMessage(msg.get());

        return true;
    }
}
