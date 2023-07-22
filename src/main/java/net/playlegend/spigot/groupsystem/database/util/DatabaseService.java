package net.playlegend.spigot.groupsystem.database.util;

import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DatabaseService {

    public abstract void createGroupsTable();

    public abstract void createUsersTable();

    public abstract void createDefaultGroup();

    public abstract void createUser(UserGeneric user);

    public abstract CompletableFuture<Optional<String>> getGroupKey(UUID uuid);

    public abstract CompletableFuture<Optional<String>> getPrefix(UUID uuid);

    public abstract CompletableFuture<Optional<Character>> getColor(UUID uuid);

    public abstract CompletableFuture<Boolean> userExists(UUID uuid);

    public abstract void deleteUser(UUID uuid);

    public abstract CompletableFuture<Optional<UserGeneric>> getUser(UUID uuid);

    public abstract CompletableFuture<Optional<Integer>> getPriority(UUID uuid);

    public abstract CompletableFuture<Optional<GroupGeneric>> getGroup(UUID uuid);

    public abstract void setGroup(UUID uuid, String key, Timestamp until);

    public abstract void createGroup(GroupGeneric group);

    public abstract void deleteGroup(String key);

    public abstract void setPriority(String key, int priority);

    public abstract void setDisplayName(String key, String displayName);

    public abstract void setPrefix(String key, String prefix);

    public abstract void setColor(String key, char color);

    public abstract CompletableFuture<Optional<String>> getPrefix(String key);

    public abstract CompletableFuture<Optional<Character>> getColor(String key);

    public abstract CompletableFuture<Boolean> groupExists(String key);

    public abstract CompletableFuture<Optional<GroupGeneric>> getGroup(String key);

    public abstract CompletableFuture<Optional<Integer>> getPriority(String key);

}
