package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class SetColorCommand extends AbstractCommand {
    public SetColorCommand() {
        super("setcolor", "Sets the color of a group", Collections.emptyList());
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NotEnoughArgumentsException, NoPermissionException, ServerException, GroupNotFoundException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.setcolor")) {
            throw new NoPermissionException("playlegend.groups.group.setcolor");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setcolor <key> <color_code>");
        }

        String key = args[0].toLowerCase();

        try {
            if (!service.groupExists(key).get()) {
                throw new GroupNotFoundException(key);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        String colorString = args[1];

        if (!colorString.matches("&([0-f]|[k-o]|r)")) {
            Message msg = new Message("commands.setcolor.no-color-code");
            msg.setInput(colorString);
            sender.sendMessage(msg.get());

            return false;
        }

        char colorCode = colorString.charAt(1);

        service.setColor(key, colorCode);

        Message msg = new Message("commands.setcolor.set");
        msg.setInput(colorString);
        sender.sendMessage(msg.get());

        return true;
    }
}
