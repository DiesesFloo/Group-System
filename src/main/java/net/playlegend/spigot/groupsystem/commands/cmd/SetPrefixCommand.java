package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class SetPrefixCommand extends AbstractCommand {
    public SetPrefixCommand() {
        super("setprefix", "Sets the prefix of a group", Collections.emptyList());
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NotEnoughArgumentsException, NoPermissionException, ServerException, GroupNotFoundException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.setprefix")) {
            throw new NoPermissionException("playlegend.groups.group.setprefix");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setprefix <key> <prefix>");
        }

        String key = args[0].toLowerCase();

        try {
            if (!service.groupExists(key).get()) {
                throw new GroupNotFoundException(key);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        StringBuilder prefix = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            prefix.append(args[i]);

            if (i < (args.length - 1)) {
                prefix.append(" ");
            }
        }

        service.setPrefix(key, prefix.toString());

        Message msg = new Message("commands.setprefix.set");
        msg.setInput(prefix.toString());
        sender.sendMessage(msg.get());

        return true;
    }
}
