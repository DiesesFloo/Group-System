package net.playlegend.spigot.groupsystem.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.permission.Permission;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MySQLService extends DatabaseService {

    final MySQLDatabase database;
    final ExecutorService pool = Executors.newCachedThreadPool();
    final Gson gson;

    public MySQLService(MySQLDatabase database) {
        this.database = database;

        this.gson = new Gson();
    }

    @Override
    public void createGroupsTable() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> database.update("CREATE TABLE IF NOT EXISTS group_groups (group_key VARCHAR(36) PRIMARY KEY UNIQUE, priority INT, display_name VARCHAR(50), prefix VARCHAR(50), color VARCHAR(1), permissions TEXT)"),
                pool
        );

        future.join();
    }

    @Override
    public void createUsersTable() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> database.update("CREATE TABLE IF NOT EXISTS group_users (uuid VARCHAR(36) PRIMARY KEY UNIQUE, group_name VARCHAR(36) DEFAULT 'default', until TIMESTAMP NULL)"),
                pool
        );

        future.join();
    }

    @Override
    public void createDefaultGroup() {
        try {
            if (groupExists("default").get()) {
                return;
            }

            GroupGeneric defaultGroup = new GroupGeneric(
                    "default",
                    100,
                    "Default",
                    "&7",
                    Collections.emptySet(),
                    '7'
            );

            createGroup(defaultGroup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(UserGeneric user) {
        String group = user.getGroup().getKey();
        Timestamp timestamp = user.getGroupUntilTimeStamp();

        try {
            PreparedStatement st = database.getConnection().prepareStatement("INSERT INTO group_users (uuid, group_name, until) VALUES (?,?,?) ON DUPLICATE KEY UPDATE group_name = ?, until = ?");

            st.setString(1, user.getUuid().toString());
            st.setString(2, group);
            st.setTimestamp(3, timestamp);
            st.setString(4, group);
            st.setTimestamp(5, timestamp);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while creating user: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Boolean> userExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT uuid FROM group_users WHERE uuid = ?");
                st.setString(1, uuid.toString());

                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                        () -> database.query(st),
                        pool
                );

                ResultSet rs = future.get();

                return rs.next();

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while checking user existing: " + e.getMessage());
            }

            return false;
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<UserGeneric>> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT group_name, until FROM group_users WHERE uuid = ?");
                st.setString(1, uuid.toString());

                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                        () -> database.query(st),
                        pool
                );

                ResultSet rs = future.get();

                if (!rs.next()) {
                    return Optional.empty();
                }

                String groupString = rs.getString("group_name");
                Timestamp until = rs.getTimestamp("until");

                rs.close();

                GroupGeneric group = getGroup("default").get().get();

                if (!groupString.equals("default")) {
                    Optional<GroupGeneric> groupOptional = getGroup(groupString).get();

                    if (groupOptional.isPresent()) {
                        group = groupOptional.get();
                    }
                }

                return Optional.of(new UserGeneric(uuid, group, until));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while getting user: " + e.getMessage());
            }

            return Optional.empty();

        }, pool);
    }

    @Override
    public void createGroup(GroupGeneric group) {
        try {
            PreparedStatement st = database.getConnection()
                    .prepareStatement("INSERT INTO group_groups (group_key, priority, display_name, prefix, color, permissions) " +
                            "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY " +
                            "UPDATE priority=?, display_name=?, prefix=?, color=?, permissions=?");

            String permissions;

            if (group.getPermissions().isEmpty()) {
                permissions = "[]";
            } else {
                permissions = gson.toJson(group.getPermissions());
            }

            st.setString(1, group.getKey());
            st.setInt(2, group.getPriority());
            st.setString(3, group.getDisplayName());
            st.setString(4, group.getPrefix());
            st.setString(5, String.valueOf(group.getColor()));
            st.setString(6, permissions);
            st.setInt(7, group.getPriority());
            st.setString(8, group.getDisplayName());
            st.setString(9, group.getPrefix());
            st.setString(10, String.valueOf(group.getColor()));
            st.setString(11, permissions);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while creating group: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Boolean> groupExists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT group_key FROM group_groups WHERE group_key=?");
                st.setString(1, key);

                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                        () -> database.query(st),
                        pool
                );

                ResultSet rs = future.get();

                return rs.next();

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while checking user existing: " + e.getMessage());
            }

            return false;
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<GroupGeneric>> getGroup(String key) {
        return CompletableFuture.supplyAsync(() -> {
            key.toLowerCase();
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT * FROM group_groups WHERE group_key = ?");
                st.setString(1, key);

                ResultSet rs = database.query(st);

                if (!rs.next()) {
                    return Optional.empty();
                }

                int priority = rs.getInt("priority");
                String displayName = rs.getString("display_name");
                String prefix = rs.getString("prefix");
                String colorString = rs.getString("color");
                Set<Permission> permissions = getPermissionsFromJson(rs.getString("permissions"));

                rs.close();

                return Optional.of(new GroupGeneric(key, priority, displayName, prefix, permissions, colorString.charAt(0)));

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while getting group: " + e.getMessage());
            }

            return Optional.empty();

        }, pool);
    }

    private Set<Permission> getPermissionsFromJson(String jsonString) {
        return gson.fromJson(jsonString, Set.class);
    }
}

