package net.playlegend.spigot.groupsystem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.commands.CommandRegistry;
import net.playlegend.spigot.groupsystem.config.ConfigHandler;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.GroupChangeListener;
import net.playlegend.spigot.groupsystem.listener.PlayerChatMessageListener;
import net.playlegend.spigot.groupsystem.groups.GroupPlayerJoinListener;
import net.playlegend.spigot.groupsystem.listener.PlayerJoinListener;
import net.playlegend.spigot.groupsystem.listener.PlayerQuitListener;
import net.playlegend.spigot.groupsystem.sign.SignChangeListener;
import net.playlegend.spigot.groupsystem.tablist.TablistHandler;
import net.playlegend.spigot.groupsystem.tablist.TablistPlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class GroupSystemPlugin extends JavaPlugin {

    @Getter static JavaPlugin plugin;
    @Getter static GroupSystemPlugin instance;
    ConfigHandler configHandler;
    TablistHandler tablistHandler;

    @Override
    public void onEnable() {
        plugin = this;
        instance = this;
        tablistHandler = new TablistHandler();

        startConfigHandler();
        startDatabase();

        new CommandRegistry(this).registerAllCommands();

        registerEvents();
    }

    private void startDatabase() {
        DatabaseRegistry.getDatabase().connect();
        DatabaseService service = DatabaseRegistry.getDatabase().getService();

        service.createUsersTable();
        service.createGroupsTable();
        service.createDefaultGroup();
    }

    private void startConfigHandler() {
        configHandler = new ConfigHandler();
        configHandler.createConfigs();
        configHandler.updateValuesOfConfig();
    }

    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GroupPlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerChatMessageListener(), this);
        pluginManager.registerEvents(new GroupChangeListener(), this);
        pluginManager.registerEvents(new SignChangeListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new TablistPlayerJoinListener(), this);
    }
}
