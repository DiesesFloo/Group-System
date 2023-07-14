package net.playlegend.spigot.groupsystem.listener;

import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.groups.UserGeneric;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        try {
            if (!DatabaseRegistry.getDatabase().getService().userExists(uuid).get()) {
                DatabaseRegistry.getDatabase().getService().createUser(new UserGeneric(uuid, Collections.emptySet()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
