package net.playlegend.spigot.groupsystem.database.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Database {

    final String host, user, password, database;
    final int port;

    public Database(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract DatabaseService getService();

}
