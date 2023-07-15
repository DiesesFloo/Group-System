package net.playlegend.spigot.groupsystem.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.database.DatabaseRegistry;
import net.playlegend.spigot.groupsystem.database.MySQLDatabase;
import net.playlegend.spigot.groupsystem.message.Message;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigHandler {

    final JavaPlugin plugin = GroupSystemPlugin.getPlugin();
    final String messagesFileName = "messages.yml";
    final String mySQLFileName = "mysql.yml";

    final ExecutorService pool = Executors.newCachedThreadPool();

    public void createConfigs() {
            getMessagesConfig().join();
            getMySQLConfig().join();
    }

    public void updateValuesOfConfig() {
        YamlConfiguration mySQLConfig;

        try {
            Optional<YamlConfiguration> mySQLConfigOptional = getMySQLConfig().get();

            if (mySQLConfigOptional.isEmpty()) {
                return;
            }

            mySQLConfig = mySQLConfigOptional.get();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String host = mySQLConfig.getString("host");
        int port = mySQLConfig.getInt("port");
        String database = mySQLConfig.getString("database");
        String username = mySQLConfig.getString("username");
        String password = mySQLConfig.getString("password");

        DatabaseRegistry.setDatabase(new MySQLDatabase(host, port, username, password, database));

        Message.reloadConfig();
    }

    public CompletableFuture<Optional<YamlConfiguration>> getMessagesConfig() {
        return getConfig(messagesFileName);
    }

    public CompletableFuture<Optional<YamlConfiguration>> getMySQLConfig() {
        return getConfig(mySQLFileName);
    }

    public CompletableFuture<Optional<YamlConfiguration>> getConfig(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            File configFile = new File(plugin.getDataFolder(), filename);

            if (!configFile.exists()) {
                plugin.saveResource(filename, false);
            }

            YamlConfiguration config = new YamlConfiguration();

            try {
                config.load(configFile);

                return Optional.of(config);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();

                return Optional.empty();
            }
        }, pool);
    }

    public void saveMessagesConfig(YamlConfiguration configuration) {
        saveConfig(configuration, messagesFileName);
    }

    public void saveMySQLConfig(YamlConfiguration configuration) {
        saveConfig(configuration, mySQLFileName);
    }

    public void saveConfig(YamlConfiguration configuration, String filename) {
        File configFile = new File(plugin.getDataFolder(), filename);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                configuration.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, pool);

        future.join();
    }

}
