package net.playlegend.spigot.groupsystem.listener;

import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PlayerChatMessageListener implements Listener {

    @EventHandler
    public void handleMessage(AsyncPlayerChatEvent event) {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();
        Player player = event.getPlayer();
        String prefix = "&7";

        try {
            Optional<String> prefixOptional = service.getPrefix(player.getUniqueId()).get();

            if (prefixOptional.isPresent()) {
                prefix = prefixOptional.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            return;
        }

        String message = event.getMessage().replace("%", "%%");
        Message msg = new Message("chat-format");
        msg.setUsePrefix(false);
        msg.setMessage(message);
        msg.setPlayerPrefix(prefix);
        msg.setUsername(player.getName());

        event.setFormat(msg.get());

    }

}
