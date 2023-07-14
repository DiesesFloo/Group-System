package net.playlegend.spigot.groupsystem.database;

import lombok.Getter;
import net.playlegend.spigot.groupsystem.database.util.Database;
import net.playlegend.spigot.groupsystem.database.util.DatabaseService;
import org.bukkit.Bukkit;

import java.sql.*;

public class MySQLDatabase extends Database {

    private DatabaseService service;

    @Getter
    private Connection connection;

    public MySQLDatabase(String host, int port, String user, String password, String database) {
        super(host, port, user, password, database);

        this.service = new MySQLService(this);
    }


    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/"+ database + "?useSSL=false",
                    user,
                    password
            );

            boolean dbExists = connection != null;

            Bukkit.getLogger().info(connection.toString());

            Bukkit.getLogger().info("[Groups] Connected to database");
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while connecting to MySQL database:" + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                Bukkit.getLogger().info("[Groups] Disconnected from MySQL database");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[Groups] Error while disconnecting from MySQL database: " + e.getMessage());
        }
    }

    public void update(String query) {
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            connect();
            Bukkit.getLogger().info("[Groups] Error while executing MySQL update: " + e.getMessage());
        }
    }

    public void update(PreparedStatement st) {
        try {
            st.execute();
            st.close();
        } catch (SQLException e) {
            connect();
            Bukkit.getLogger().info("[Groups] Error while executing MySQL update: " + e.getMessage());
        }
    }

    public ResultSet query(PreparedStatement st){
        ResultSet rs = null;

        try {
            rs = st.executeQuery();
        } catch (SQLException e) {
            connect();
            Bukkit.getLogger().info("[Groups] Error wile executing MySQL query: " + e.getMessage());
        }

        return rs;
    }

    @Override
    public DatabaseService getService() {
        return this.service;
    }
}
