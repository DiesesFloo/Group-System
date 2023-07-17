package net.playlegend.spigot.groupsystem.listener;

import com.sun.tools.javac.Main;
import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.tablist.TablistHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        DatabaseService service = DatabaseRegistry.getDatabase().getService();
        TablistHandler handler = GroupSystemPlugin.getInstance().getTablistHandler();

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

        handler.setTabPrefix(player).join();

    }

}
