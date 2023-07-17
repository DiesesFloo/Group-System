package net.playlegend.spigot.groupsystem.message;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.GroupSystemPlugin;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
public class Message {

    static YamlConfiguration configuration;

    final String key;
    boolean usePrefix = true;
    UserGeneric user;
    GroupGeneric group;
    String username;
    String message;
    String permission;
    String input;
    String playerPrefix;
    int days = 0;

    public Message(String key) {
        this.key = key;
    }

    public Message(String key, UserGeneric user, String username, int days) {
        this.key = key;
        this.user = user;
        this.username = username;
        this.days = days;
    }

    public Message(String key, UserGeneric user, String username, String message) {
        this.key = key;
        this.user = user;
        this.username = username;
        this.message = message;
    }

    public Message(String key, GroupGeneric group) {
        this.key = key;
        this.group = group;
    }

    public Message(String key, String permission) {
        this.key = key;
        this.permission = permission;
    }

    public String get() {
        String message = configuration.getString(this.key);

        if (message == null) {
            return "Message is missing, please contact the PlayLegend-team (Key:" + key + ")";
        }

        if (usePrefix) {
            String prefix = configuration.getString("prefix");

            if (prefix == null) {
                return "Prefix is missing, please contact the PlayLegend-team";
            }

            message = prefix + message;
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
        }

        if (username != null) {
            message = message.replace("%username%", username);
        }

        if (group != null) {
            message = message.replace("%group_color%", "§" + group.getColor());
            message = message.replace("%group_prefix%", group.getPrefix());
            message = message.replace("%group_name%", group.getDisplayName());
        }

        if (permission != null) {
            message = message.replace("%permission%", permission);
        }

        if (input != null) {
            message = message.replace("%input%", input);
        }

        if (this.playerPrefix != null) {
            message = message.replace("%prefix%", this.playerPrefix);
        }

        if (days != 0) {
            message = message.replace("%days%", days + "d");
        } else {
            message = message.replace("%days%", "PERMANENT");
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if (this.message != null) {
            message = message.replace("%message%", this.message);
        }

        return message;
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
