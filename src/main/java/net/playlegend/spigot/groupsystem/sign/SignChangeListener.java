package net.playlegend.spigot.groupsystem.sign;

import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class SignChangeListener implements Listener {

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {
        if (!Objects.equals(event.getLine(0), "%group%")) {
            return;
        }

        Player player = event.getPlayer();
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        Optional<UserGeneric> userOptional;
        try {
            userOptional = service.getUser(player.getUniqueId()).get();
        } catch (InterruptedException | ExecutionException e) {
            return;
        }

        if (userOptional.isEmpty()) {
            return;
        }

        UserGeneric user = userOptional.get();
        String username = player.getName();

        event.setLine(0, getSignLine(1, user, username));
        event.setLine(1, getSignLine(2, user, username));
        event.setLine(2, getSignLine(3, user, username));
        event.setLine(3, getSignLine(4, user, username));
    }

    private String getSignLine(int number, UserGeneric user, String username) {
        Message msg = new Message("sign.line" + number);
        msg.setUsername(username);
        msg.setUser(user);
        msg.setUsePrefix(false);

        return msg.get();
    }

}
