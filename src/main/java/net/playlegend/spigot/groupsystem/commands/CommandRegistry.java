package net.playlegend.spigot.groupsystem.commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandRegistry {

    final JavaPlugin plugin;

    public CommandRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAllCommands() {

    }

    private void register(Command command) {
        Objects.requireNonNull(getCommandMap()).register(plugin.getName(), command);
    }

    private CommandMap getCommandMap() {
        try {
            final Field bukkitCmdMap = Bukkit.getServer().getHelpMap().getClass().getDeclaredField("commandMap");
            bukkitCmdMap.setAccessible(true);

            return (CommandMap) bukkitCmdMap.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
