package net.playlegend.spigot.groupsystem.database.util;

import net.playlegend.spigot.groupsystem.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.groups.UserGeneric;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DatabaseService {

    public abstract void createGroupsTable();

    public abstract void createUsersTable();

    public abstract void createDefaultGroup();

    public abstract void createUser(UserGeneric user);

    public abstract CompletableFuture<Boolean> userExists(UUID uuid);

    public abstract CompletableFuture<Optional<UserGeneric>> getUser(UUID uuid);

    public abstract void createGroup(GroupGeneric group);

    public abstract void deleteGroup(String key);

    public abstract CompletableFuture<Boolean> groupExists(String key);

    public abstract CompletableFuture<Optional<GroupGeneric>> getGroup(String key);

}
