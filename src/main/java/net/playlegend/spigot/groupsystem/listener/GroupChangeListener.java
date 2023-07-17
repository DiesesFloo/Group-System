package net.playlegend.spigot.groupsystem.listener;

import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.event.GroupChangeEvent;
import net.playlegend.spigot.groupsystem.tablist.TablistHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class GroupChangeListener implements Listener {

    @EventHandler
    public void handleGroupChange(GroupChangeEvent event) {
        Player player = event.getPlayer();
        TablistHandler handler = GroupSystemPlugin.getInstance().getTablistHandler();

        handler.updateTablistPrefixOf(player).join();
    }

}
