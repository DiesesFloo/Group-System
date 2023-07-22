package net.playlegend.spigot.groupsystem.database;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import net.playlegend.spigot.groupsystem.groups.event.GroupChangeEvent;
import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;
import net.playlegend.spigot.groupsystem.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                () -> database.update("CREATE TABLE IF NOT EXISTS group_users (uuid VARCHAR(36) PRIMARY KEY UNIQUE, group_key VARCHAR(36) DEFAULT 'default', until TIMESTAMP NULL)"),
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
            PreparedStatement st = database.getConnection().prepareStatement("INSERT INTO group_users (uuid, group_key, until) VALUES (?,?,?) ON DUPLICATE KEY UPDATE group_key = ?, until = ?");

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
    public CompletableFuture<Optional<String>> getGroupKey(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement groupKeySt = database.getConnection().prepareStatement("SELECT group_key FROM group_users WHERE uuid = ?");

                groupKeySt.setString(1, uuid.toString());

                CompletableFuture<ResultSet> groupKeyFuture = CompletableFuture.supplyAsync(
                        () -> database.query(groupKeySt),
                        pool
                );

                ResultSet groupKeyRs = groupKeyFuture.get();

                if (!groupKeyRs.next()) {
                    return Optional.empty();
                }

                String groupKey = groupKeyRs.getString("group_key");

                return Optional.of(groupKey);

            } catch (SQLException | InterruptedException | ExecutionException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting user group key: " + e.getMessage());
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<String>> getPrefix(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<String> groupKeyOptional = getGroupKey(uuid).get();

                if (groupKeyOptional.isEmpty()) {
                    return Optional.empty();
                }

                return getPrefix(groupKeyOptional.get()).get();

            } catch (InterruptedException | ExecutionException e) {
                Bukkit.getLogger().warning("[Groups] Error while checking user existing: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<Character>> getColor(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<String> groupKeyOptional = getGroupKey(uuid).get();

                if (groupKeyOptional.isEmpty()) {
                    return Optional.empty();
                }

                return getColor(groupKeyOptional.get()).get();

            } catch (InterruptedException | ExecutionException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting user color: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
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
    public void deleteUser(UUID uuid) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("DELETE FROM group_users WHERE uuid = ?");
            st.setString(1, uuid.toString());

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> database.update(st), pool);
            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while deleting user: " + e.getMessage());
        }

    }

    @Override
    public CompletableFuture<Optional<UserGeneric>> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement st = database.getConnection().prepareStatement("SELECT group_key, until FROM group_users WHERE uuid = ?");
                st.setString(1, uuid.toString());

                CompletableFuture<ResultSet> future = CompletableFuture.supplyAsync(
                        () -> database.query(st),
                        pool
                );

                ResultSet rs = future.get();

                if (!rs.next()) {
                    return Optional.empty();
                }

                String groupString = rs.getString("group_key");
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
    public CompletableFuture<Optional<Integer>> getPriority(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<String> groupKeyOptional = getGroupKey(uuid).get();

                if (groupKeyOptional.isEmpty()) {
                    return Optional.empty();
                }

                return getPriority(groupKeyOptional.get()).get();

            } catch (InterruptedException | ExecutionException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting user priority: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<GroupGeneric>> getGroup(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<String> groupKeyOptional = getGroupKey(uuid).get();

                if (groupKeyOptional.isEmpty()) {
                    return Optional.empty();
                }

                return getGroup(groupKeyOptional.get()).get();

            } catch (InterruptedException | ExecutionException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting user priority: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
    }

    @Override
    public void setGroup(UUID uuid, String key, Timestamp until) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("UPDATE group_users SET group_key = ?, until = ? WHERE uuid = ?");
            st.setString(1, key);
            st.setTimestamp(2, until);
            st.setString(3, uuid.toString());

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new GroupChangeEvent(player, key));
            }

        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while setting group: " + e.getMessage());
        }
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
    public void deleteGroup(String key) {
        key = key.toLowerCase();

        try {
            PreparedStatement groupSt = database.getConnection().prepareStatement("DELETE FROM group_groups WHERE group_key = ?");
            groupSt.setString(1, key);

            PreparedStatement userSt = database.getConnection().prepareStatement("UPDATE group_users SET group_name = ?, until = NULL WHERE group_name = ?");
            userSt.setString(1, "default");
            userSt.setString(2, key);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> {
                        database.update(groupSt);
                        database.update(userSt);
                    },
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while deleting group: " + e.getMessage());
        }
    }

    @Override
    public void setPriority(String key, int priority) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("UPDATE group_groups SET priority = ? WHERE group_key = ?");
            st.setInt(1, priority);
            st.setString(2, key);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while setting priority: " + e.getMessage());
        }
    }

    @Override
    public void setDisplayName(String key, String displayName) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("UPDATE group_groups SET display_name = ? WHERE group_key = ?");
            st.setString(1, displayName);
            st.setString(2, key);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();

        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while setting display-name: " + e.getMessage());
        }
    }

    @Override
    public void setPrefix(String key, String prefix) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("UPDATE group_groups SET prefix = ? WHERE group_key = ?");
            st.setString(1, prefix);
            st.setString(2, key);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();

        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while setting prefix: " + e.getMessage());
        }
    }

    @Override
    public void setColor(String key, char color) {
        try {
            PreparedStatement st = database.getConnection().prepareStatement("UPDATE group_groups SET color = ? WHERE group_key = ?");
            st.setString(1, String.valueOf(color));
            st.setString(2, key);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> database.update(st),
                    pool
            );

            future.join();

        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while setting prefix: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Optional<String>> getPrefix(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement groupPrefixSt = database.getConnection().prepareStatement("SELECT prefix FROM group_groups WHERE group_key = ?");
                CompletableFuture<ResultSet> groupPrefixFuture = CompletableFuture.supplyAsync(
                        () -> database.query(groupPrefixSt),
                        pool
                );

                groupPrefixSt.setString(1, key);

                ResultSet groupPrefixRs = groupPrefixFuture.get();

                if (!groupPrefixRs.next()) {
                    return Optional.empty();
                }

                String prefix = groupPrefixRs.getString("prefix");

                return Optional.of(prefix);
            } catch (InterruptedException | ExecutionException | SQLException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting group prefix: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
    }

    @Override
    public CompletableFuture<Optional<Character>> getColor(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement groupColorSt = database.getConnection().prepareStatement("SELECT color FROM group_groups WHERE group_key = ?");
                CompletableFuture<ResultSet> groupColorFuture = CompletableFuture.supplyAsync(
                        () -> database.query(groupColorSt),
                        pool
                );

                ResultSet groupColorRs = groupColorFuture.get();

                if (!groupColorRs.next()) {
                    return Optional.empty();
                }

                char color = groupColorRs.getString("color").charAt(0);

                return Optional.of(color);
            } catch (InterruptedException | ExecutionException | SQLException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting group color: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
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

    @Override
    public CompletableFuture<Optional<Integer>> getPriority(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement groupPrefixSt = database.getConnection().prepareStatement("SELECT priority FROM group_groups WHERE group_key = ?");
                CompletableFuture<ResultSet> groupPrefixFuture = CompletableFuture.supplyAsync(
                        () -> database.query(groupPrefixSt),
                        pool
                );

                groupPrefixSt.setString(1, key);

                ResultSet groupPrefixRs = groupPrefixFuture.get();

                if (!groupPrefixRs.next()) {
                    return Optional.empty();
                }

                int priority = groupPrefixRs.getInt("priority");

                return Optional.of(priority);
            } catch (InterruptedException | ExecutionException | SQLException e) {
                Bukkit.getLogger().warning("[Groups] Error while getting group priority: " + e.getMessage());
                return Optional.empty();
            }
        }, pool);
    }

    private Set<Permission> getPermissionsFromJson(String jsonString) {
        return gson.fromJson(jsonString, Set.class);
    }
}

