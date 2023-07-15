package net.playlegend.spigot.groupsystem.message;

import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Timestamp;
import java.util.Optional;

public class Message {

    private static YamlConfiguration configuration;

    private String key;
    private UserGeneric user;
    private GroupGeneric group;
    private String username;

    public Message(String key) {
        this.key = key;
    }

    public Message(String key, UserGeneric user, String username) {
        this.key = key;
        this.user = user;
        this.username = username;
    }

    public Message(String key, GroupGeneric group) {
        this.key = key;
        this.group = group;
    }

    public Optional<String> get() {
        String message = configuration.getString(this.key);

        if (message == null) {
            return Optional.empty();
        }

        if (user != null) {

            GroupGeneric userGroup = user.getGroup();

            if (user.groupIsPermanent()) {
                message = message.replace("%user_group_until%", "PERMANENT");
            } else {
                message = message.replace("%user_group_until%", user.getGroupUntilTimeStamp().toString());
            }

            message = message.replace("%user_group_color%", "§" + user.getGroup().getColor());
            message = message.replace("%user_group_prefix%", userGroup.getPrefix());
            message = message.replace("%user_group_name%", userGroup.getDisplayName());
            message = message.replace("%username%", username);
        }

        if (group != null) {
            message = message.replace("%group_color%", "§" + group.getColor());
            message = message.replace("%group_prefix%", group.getPrefix());
            message = message.replace("%group_name%", group.getDisplayName());
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        return Optional.of(message);
    }

    public static void reloadConfig() {
        try {
            Optional<YamlConfiguration> configOptional = GroupSystemPlugin.getInstance().getConfigHandler().getMessagesConfig().get();

            configOptional.ifPresent(yamlConfiguration -> configuration = yamlConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
