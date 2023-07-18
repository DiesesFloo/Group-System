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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GroupPlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        try {
            if (!service.userExists(uuid).get()) {
                service.createUser(new UserGeneric(
                        uuid,
                        DatabaseRegistry.getDatabase().getService().getGroup("default").get().get(),
                        null));

                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return;
        }

        Optional<UserGeneric> userOptional;

        try {
            userOptional = service.getUser(uuid).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        if (userOptional.isEmpty()) {
            return;
        }

        UserGeneric user = userOptional.get();

        if (System.currentTimeMillis() > user.getGroupUntilTimeStamp().getTime()) {
            service.setGroup(uuid, "default", null);
        }

    }

}
