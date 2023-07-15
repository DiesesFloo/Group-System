package net.playlegend.spigot.groupsystem.commands;

import net.playlegend.spigot.groupsystem.commands.exceptions.NoPermissionException;
import net.playlegend.spigot.groupsystem.commands.exceptions.NotEnoughArgumentsException;
import net.playlegend.spigot.groupsystem.commands.exceptions.PlayerNotFoundException;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AbstractCommand extends Command {
    protected AbstractCommand(String name, String description, List<String> aliases) {
        super(name, description, "/" + name, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        try {
            return onExecute(sender, s, strings);
        } catch (NotEnoughArgumentsException e) {
            Message msg = new Message("general.not-enough-arguments");
            msg.setMessage(e.getMessage());
            sender.sendMessage(msg.get());
        } catch (NoPermissionException e) {
            sender.sendMessage(new Message("general.no-perm", e.getPermission()).get());
        } catch (PlayerNotFoundException e) {
            Message msg = new Message("general.player-not-found");
            msg.setUsername(e.getInput());
            sender.sendMessage(msg.get());
        }

        return false;
    }

    public abstract boolean onExecute(CommandSender sender, String cmd, String[] args)
            throws NotEnoughArgumentsException, NoPermissionException, PlayerNotFoundException;
    }
