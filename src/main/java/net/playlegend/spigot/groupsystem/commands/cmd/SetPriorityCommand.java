package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetPriorityCommand extends AbstractCommand {
    public SetPriorityCommand() {
        super("setpriority", "Sets the priority of a group", List.of("setgrouppriority", "setprio", "setgroupprio"));
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NotEnoughArgumentsException, NoPermissionException, ServerException, GroupNotFoundException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.setpriority")) {
            throw new NoPermissionException("playlegend.groups.group.setpriority");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setpriority <key> <priority>");
        }

        String key = args[0].toLowerCase();

        try {
            if (!service.groupExists(key).get()) {
                throw new GroupNotFoundException(key);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        int priority;
        try {
            priority = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new Message("commands.setpriority.priority-number").get());

            return false;
        }

        service.setPriority(key, priority);

        Message msg = new Message("commands.setpriority.set");
        msg.setInput(String.valueOf(priority));
        sender.sendMessage(msg.get());

        return true;
    }
}
