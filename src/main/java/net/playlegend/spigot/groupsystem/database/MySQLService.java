package net.playlegend.spigot.groupsystem.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.database.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.database.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.permission.Permission;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
                () -> database.update("CREATE TABLE IF NOT EXISTS group_groups (group_key VARCHAR(36) PRIMARY KEY UNIQUE, priority INT, display_name VARCHAR(50), prefix VARCHAR(50), permissions TEXT)"),
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
                Bukkit.getLogger().warning("[Groups] Error while preparing user existing check statement");
            }

            return false;
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<UserGeneric>> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT groups FROM group_users WHERE uuid = ?");
                st.setString(1, uuid.toString());

                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                        () -> database.query(st),
                        pool
                );

                ResultSet rs = future.get();

                if (!rs.next()) {
                    return Optional.empty();
                }

                String groupsString = rs.getString("groups");

                rs.close();

                Set<String> groupKeys = gson.fromJson(groupsString, Set.class);

                Set<GroupGeneric> groups = ConcurrentHashMap.newKeySet();
                groupKeys.forEach(groupKey -> getGroup(groupKey).thenAccept(optionalGroup -> optionalGroup.ifPresent(groups::add)));


                return Optional.of(new UserGeneric(uuid, groups));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while preparing user getting statement");
            }

            return Optional.empty();

        }, pool);
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
                Bukkit.getLogger().warning("[Groups] Error while preparing group existing check statement");
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
                Set<Permission> permissions = getPermissionsFromJson(rs.getString("permissions"));

                rs.close();

                return Optional.of(new GroupGeneric(key, priority, displayName, prefix, permissions));

            } catch (Exception e) {
                Bukkit.getLogger().warning("[Groups] Error while preparing group getting statement");
            }

            return Optional.empty();

        }, pool);
    }

    private Set<Permission> getPermissionsFromJson(String jsonString) {
        return gson.fromJson(jsonString, new TypeToken<Set<Permission>>() {
        }.getType());
    }
}

