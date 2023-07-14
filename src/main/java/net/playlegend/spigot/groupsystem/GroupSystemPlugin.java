package net.playlegend.spigot.groupsystem;

import net.playlegend.spigot.groupsystem.config.ConfigHandler;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.listener.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GroupSystemPlugin extends JavaPlugin {

    private static JavaPlugin plugin;
    private ConfigHandler configHandler;

    @Override
    public void onEnable() {
        plugin = this;

        configHandler = new ConfigHandler();

        configHandler.createConfigs();
        configHandler.updateValuesOfConfig();

        DatabaseRegistry.getDatabase().connect();

        DatabaseRegistry.getDatabase().getService().createGroupsTable();
        DatabaseRegistry.getDatabase().getService().createUsersTable();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
