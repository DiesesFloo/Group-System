package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetDisplayNameCommand extends AbstractCommand {
    public SetDisplayNameCommand() {
        super("setdisplayname", "Sets the displayname of a group", List.of("setname"));
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NotEnoughArgumentsException, NoPermissionException, ServerException, GroupNotFoundException {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (!sender.hasPermission("playlegend.groups.group.setdisplayname")) {
            throw new NoPermissionException("playlegend.groups.group.setdisplayname");
        }

        if (args.length < 2) {
            throw new NotEnoughArgumentsException("/setdisplayname <key> <display_name>");
        }

        String key = args[0].toLowerCase();

        try {
            if (!service.groupExists(key).get()) {
                throw new GroupNotFoundException(key);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        String displayName = args[1];

        service.setDisplayName(key, displayName);

        Message msg = new Message("commands.setdisplayname.set");
        msg.setInput(displayName);
        sender.sendMessage(msg.get());

        return true;
    }
}
