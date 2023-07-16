package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeleteGroupCommand extends AbstractCommand {
    public DeleteGroupCommand() {
        super("deletegroup", "Delete a group from the database", List.of("dgroup"));
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NotEnoughArgumentsException, NoPermissionException, ServerException, GroupNotFoundException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.create")) {
            throw new NoPermissionException("playlegend.groups.group.create");
        }

        if (args.length == 0) {
            throw new NotEnoughArgumentsException("/deletegroup <key>");
        }

        String key = args[0].toLowerCase();

        try {
            if (!service.groupExists(key).get()) {
                throw new GroupNotFoundException(key);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        service.deleteGroup(key);

        Message msg = new Message("commands.deletegroup.deleted");
        msg.setInput(key);
        sender.sendMessage(msg.get());

        return false;
    }
}
