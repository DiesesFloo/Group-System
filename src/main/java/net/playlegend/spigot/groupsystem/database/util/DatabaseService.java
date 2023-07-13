package net.playlegend.spigot.groupsystem.database.util;

import net.playlegend.spigot.groupsystem.database.groups.GroupGeneric;
import net.playlegend.spigot.groupsystem.database.groups.UserGeneric;

import java.util.UUID;

public abstract class DatabaseService {

    public abstract void createGroupsTable();

    public abstract void createUsersTable();

    public abstract void createUser(UserGeneric user);

    public abstract UserGeneric getUser(UUID uuid);

    public abstract void createGroup(GroupGeneric group);

    public abstract GroupGeneric getGroup(String key);

}
