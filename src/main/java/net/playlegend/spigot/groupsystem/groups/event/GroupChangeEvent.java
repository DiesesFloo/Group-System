package net.playlegend.spigot.groupsystem.groups.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupChangeEvent extends Event {
    static final HandlerList HANDLER_LIST = new HandlerList();
    final Player player;
    final String key;

    public GroupChangeEvent(Player player, String key) {
        this.player = player;
        this.key = key;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public String getGroupKey() {
        return key;
    }

    public Player getPlayer() {
        return player;
    }
}
