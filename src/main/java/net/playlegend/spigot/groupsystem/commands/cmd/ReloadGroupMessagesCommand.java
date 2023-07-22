package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class ReloadGroupMessagesCommand extends AbstractCommand {
    public ReloadGroupMessagesCommand() {
        super("reloadgroupmessages", "Reloads the message config for the group system.", Collections.emptyList());
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args) throws NoPermissionException {
        if (!sender.hasPermission("playlegend.groups.reloadmessages")){
            throw new NoPermissionException("playlegend.groups.reloadmessages");
        }

        Message.reloadConfig();

        sender.sendMessage(new Message("commands.reloadgroupmessages.reloaded").get());

        return true;
    }
}
