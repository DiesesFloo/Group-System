package net.playlegend.spigot.groupsystem.database.util;

public abstract class Database {

    protected final String host, port, user, password, database;

    public Database(String host, String port, String user, String password, String database) {
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
