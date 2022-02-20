package com.nz1337.easysql;

import com.nz1337.easysql.manager.Database;
import com.nz1337.easysql.manager.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EasySQL {

    private final ArrayList<Table> tables;
    private Connection connection;
    private Database database;
    private String host;
    private String user;
    private String password;
    private String databaseName;
    private int port;
    private boolean encoder;

    public EasySQL() {
        this.tables = new ArrayList<>();
        this.host = "127.0.0.1";
        this.user = "root";
        this.password = "password";
        this.databaseName = "database";
        this.port = 3306;
        this.encoder = true;
    }

    public void connect() {
        try {
            synchronized (this) {
                if (this.getConnection() != null && !this.getConnection().isClosed()) {
                    System.err.println("[EasySQL] An SQL connection is already active!");
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                final String url = "jdbc:mysql://" + this.host + ":" + this.port;
                final String encoding = this.encoder ? "?useUnicode=true&characterEncoding=utf8" : "";
                this.setConnection(DriverManager.getConnection(url + encoding, this.user, this.password));
                this.database = new Database();
                this.database.createIfNotExists(this.getConnection(), this.databaseName);
                this.setConnection(DriverManager.getConnection(url + "/" + this.databaseName + encoding, this.user, this.password));
                this.tables.forEach(tableCreator -> tableCreator.createIfNotExists(this.connection));
            }
        } catch (final SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void close() {
        try {
            this.getConnection().close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    /*public Column getColumns(final Table table) {
        return this.columns.stream().filter(column -> column.getTable().equals(table.getTable())).findFirst().orElse(null);
    }*/

    public void delete() {
        this.database.deleteIfExists(this.databaseName);
    }

    public EasySQL createDefaultTables(final Table... table) {
        this.tables.addAll(Arrays.asList(table));
        return this;
    }

    public EasySQL setHost(final String host) {
        this.host = host;
        return this;
    }

    public EasySQL setPassword(final String password) {
        this.password = password;
        return this;
    }

    public EasySQL setUser(final String user) {
        this.user = user;
        return this;
    }

    public EasySQL setDatabase(final String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public EasySQL setPort(final int port) {
        this.port = port;
        return this;
    }

    public EasySQL setEncoder(final boolean encoder) {
        this.encoder = encoder;
        return this;
    }

    private Connection getConnection() {
        return connection;
    }

    private void setConnection(final Connection connection) {
        this.connection = connection;
    }
}
