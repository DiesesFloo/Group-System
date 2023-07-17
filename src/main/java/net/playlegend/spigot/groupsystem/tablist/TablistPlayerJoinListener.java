package net.playlegend.spigot.groupsystem.tablist;

import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TablistPlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void handleJoin(PlayerJoinEvent event) {
        TablistHandler handler = GroupSystemPlugin.getInstance().getTablistHandler();
        Player player = event.getPlayer();

        handler.setTabPrefix(player).join();
    }

}
