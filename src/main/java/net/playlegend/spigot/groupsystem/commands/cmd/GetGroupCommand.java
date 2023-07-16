package net.playlegend.spigot.groupsystem.commands.cmd;

import net.playlegend.spigot.groupsystem.commands.AbstractCommand;
import net.playlegend.spigot.groupsystem.commands.exceptions.*;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.message.Message;
import net.playlegend.spigot.groupsystem.mojang.UUIDFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GetGroupCommand extends AbstractCommand {
    public GetGroupCommand() {
        super("getgroup", "Get the current group information of a player", List.of("getg"));
    }

    @Override
    public boolean onExecute(CommandSender sender, String cmd, String[] args)
            throws NotEnoughArgumentsException, NoPermissionException, PlayerNotFoundException, ServerException {

        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                throw new NotEnoughArgumentsException("/getgroup (<player>)");
            }

            try {
                Optional<UserGeneric> userOptional = service.getUser(player.getUniqueId()).get();

                if (userOptional.isEmpty()) {
                    throw new PlayerNotFoundException(player.getDisplayName());
                }

                UserGeneric user = userOptional.get();

                Message msg = new Message("commands.getgroup.self");
                msg.setUser(user);

                player.sendMessage(msg.get());

                return true;
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
            }
        }

        if (!sender.hasPermission("playlegend.groups.group.get.others")){
            throw new NoPermissionException("playlegend.groups.group.get.others");
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

        try {
            Optional<UserGeneric> userOptional = service.getUser(uuid).get();

            if (userOptional.isEmpty()) {
                throw new PlayerNotFoundException(playerString);
            }

            UserGeneric user = userOptional.get();

            Message msg = new Message("commands.getgroup.other");
            msg.setUsername(playerString);
            msg.setUser(user);
            sender.sendMessage(msg.get());

        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException("Type: '" + e.getCause() + "'; Message: '" + e.getMessage() + "'");
        }

        return true;
    }
}
