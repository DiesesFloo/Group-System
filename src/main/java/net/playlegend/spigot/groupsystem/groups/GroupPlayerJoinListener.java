package net.playlegend.spigot.groupsystem.groups;

import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.tablist.TablistHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GroupPlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        try {
            if (!DatabaseRegistry.getDatabase().getService().userExists(uuid).get()) {
                DatabaseRegistry.getDatabase().getService().createUser(new UserGeneric(
                        uuid,
                        DatabaseRegistry.getDatabase().getService().getGroup("default").get().get(),
                        null));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

}
