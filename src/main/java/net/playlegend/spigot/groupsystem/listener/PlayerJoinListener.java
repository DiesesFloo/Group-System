package net.playlegend.spigot.groupsystem.listener;

import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        try {
            if (!DatabaseRegistry.getDatabase().getService().userExists(uuid).get()) {
                DatabaseRegistry.getDatabase().getService().createUser(new UserGeneric(
                        uuid,
                        DatabaseRegistry.getDatabase().getService().getGroup("default").get().get(),
                        null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
