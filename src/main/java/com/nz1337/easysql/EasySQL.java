package com.nz1337.easysql;

import com.nz1337.easysql.manager.Database;
import com.nz1337.easysql.manager.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EasySQL {

    private final ArrayList<Table> tables = new ArrayList<>();
    private Connection connection;
    private Database database;
    private String host = "127.0.0.1";
    private String user = "root";
    private String password = "password";
    private String databaseName = "database";
    private int port = 3306;
    private boolean encoder = true;

    public void connect() {
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    System.err.println("[EasySQL] An SQL connection is already active!");
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://" + this.host + ":" + this.port;
                String encoding = this.encoder ? "?useUnicode=true&characterEncoding=utf8" : "";
                setConnection(DriverManager.getConnection(url + encoding, this.user, this.password));
                this.database = new Database();
                this.database.createIfNotExists(this.getConnection(), this.databaseName);
                setConnection(DriverManager.getConnection(url + "/" + this.databaseName + encoding, this.user, this.password));
                this.tables.forEach(tableCreator -> tableCreator.createIfNotExists(connection));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*public Column getColumns(Table table) {
        return this.columns.stream().filter(column -> column.getTable().equals(table.getTable())).findFirst().orElse(null);
    }*/

    public void delete() {
        this.database.deleteIfExists(this.databaseName);
    }

    public EasySQL createDefaultTables(Table... table) {
        this.tables.addAll(Arrays.asList(table));
        return this;
    }

    public EasySQL setHost(String host) {
        this.host = host;
        return this;
    }

    public EasySQL setPassword(String password) {
        this.password = password;
        return this;
    }

    public EasySQL setUser(String user) {
        this.user = user;
        return this;
    }

    public EasySQL setDatabase(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public EasySQL setPort(int port) {
        this.port = port;
        return this;
    }

    public EasySQL setEncoder(boolean encoder) {
        this.encoder = encoder;
        return this;
    }

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }
}
