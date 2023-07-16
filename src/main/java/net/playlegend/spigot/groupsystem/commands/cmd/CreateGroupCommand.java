package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.NoPermissionException;
import net.playlegend.spigot.groupsystem.commands.exceptions.NotEnoughArgumentsException;
import net.playlegend.spigot.groupsystem.commands.exceptions.ServerException;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class CreateGroupCommand extends AbstractCommand {
    public CreateGroupCommand() {
        super(
                "creategroup",
                "Create a group in the group/perm system",
                Arrays.asList("cg", "cgroup")
        );
    }

    // /creategroup <key> <name> <color> <priority> <prefix>
    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args)
            throws NotEnoughArgumentsException, NoPermissionException, ServerException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.create")) {
            throw new NoPermissionException("playlegend.groups.group.create");
        }

        if (args.length < 5) {
            throw new NotEnoughArgumentsException("/creategroup <key> <name> <color> <priority> <prefix>");
        }

        String key = args[0].toLowerCase();
        try {
            if (service.groupExists(key).get()){
                Message msg  = new Message("commands.creategroup.exists");
                msg.setInput(key);
                sender.sendMessage(msg.get());

                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        String displayName = args[1];

        String colorCodeString = args[2];
        if (!colorCodeString.matches("&([0-f]|[k-o]|r)")) {
            Message msg = new Message("commands.creategroup.no-color-code");
            msg.setInput(colorCodeString);
            sender.sendMessage(msg.get());

            return false;
        }
        char colorChar = colorCodeString.charAt(1);

        int priority;
        try {
            priority = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new Message("commands.creategroup.priority-number").get());

            return false;
        }

        StringBuilder prefix = new StringBuilder();
        for (int i = 4; i < args.length; i++) {
            prefix.append(args[i]);
            prefix.append(" ");
        }

        GroupGeneric group = new GroupGeneric(key, priority, displayName, prefix.toString(), Collections.emptySet(), colorChar);

        DatabaseRegistry.getDatabase().getService().createGroup(
                group
        );

        sender.sendMessage(new Message("commands.creategroup.created", group).get());

        return true;
    }
}
