package net.playlegend.spigot.groupsystem.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.database.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.database.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.permission.Permission;
import net.playlegend.spigot.groupsystem.permission.PermissionType;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
                () -> database.update("CREATE TABLE IF NOT EXISTS group_groups (key VARCHAR(36), priority INT(1000) display_name VARCHAR(50), prefix VARCHAR(50), permissions TEXT)"),
                pool
        );

        future.join();
    }

    @Override
    public void createUsersTable() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> database.update("CREATE TABLE IF NOT EXISTS group_users (uuid VARCHAR(36) PRIMARY KEY UNIQUE, groups TEXT)"),
                pool
        );

        future.join();
    }

    @Override
    public void createUser(UserGeneric user) {
        List<String> groupKeys = user.getGroups().parallelStream()
                .map(GroupGeneric::getKey)
                .toList();

        String groups = gson.toJson(groupKeys);

        try {
            PreparedStatement st = database.getConnection().prepareStatement("INSERT INTO group_users (uuid, groups) VALUES (?,?) ON DUPLICATE KEY UPDATE groups = ?");
            st.setString(1, user.getUuid().toString());
            st.setString(2, groups);
            st.setString(3, groups);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while preparing user creation statement");
        }
    }

    @Override
    public UserGeneric getUser(UUID uuid) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("SELECT groups FROM group_users WHERE uuid = ?");
            st.setString(1, uuid.toString());

            CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                    () -> database.query(st),
                    pool
            );

            ResultSet rs = future.get();

            while (rs.next()) {
                String groupsString = rs.getString("groups");
                Set<String> groupKeys = gson.fromJson(groupsString, new TypeToken<Set<String>>() {}.getType());
                Set<GroupGeneric> groups = groupKeys.parallelStream()
                        .map(this::getGroup)
                        .collect(Collectors.toSet());

                return new UserGeneric(uuid, groups);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[Groups] Error while preparing user getting statement");
        }

        return null;
    }

    @Override
    public void createGroup(GroupGeneric group) {
        try {
            PreparedStatement st = database.getConnection()
                    .prepareStatement("INSERT INTO group_groups (key, priority, display_name, prefix, permissions) " +
                            "VALUES(?,?,?,?,?) ON DUPLICATE KEY " +
                            "UPDATE priority=?, display_name=?, prefix=?, permissions=?");

            String permissions = gson.toJson(group.getPermissions());

            st.setString(1, group.getKey());
            st.setInt(2, group.getPriority());
            st.setString(3, group.getDisplayName());
            st.setString(4, group.getPrefix());
            st.setString(5, permissions);
            st.setInt(6, group.getPriority());
            st.setString(7, group.getDisplayName());
            st.setString(8, group.getPrefix());
            st.setString(9, permissions);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while preparing group creation statement");
        }
    }

    @Override
    public GroupGeneric getGroup(String key) {
        key = key.toLowerCase();

        try {
            PreparedStatement st = database.getConnection().prepareStatement("SELECT * FROM group_groups WHERE key = ?");
            st.setString(1, key);

            ResultSet rs = database.query(st);
            while (rs.next()) {
                int priority = rs.getInt("priority");
                String displayName = rs.getString("display_name");
                String prefix = rs.getString("prefix");
                Set<Permission> permissions = getPermissionsFromJson(rs.getString("permissions"));

                return new GroupGeneric(key, priority, displayName, prefix, permissions);


            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while preparing group getting statement");
        }

        return null;
    }

    private Set<Permission> getPermissionsFromJson(String jsonString)  {
        return gson.fromJson(jsonString, new TypeToken<Set<Permission>>() {}.getType());
    }
}

