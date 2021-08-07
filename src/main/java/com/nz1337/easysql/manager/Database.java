package com.nz1337.easysql.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private Connection connection;

    public void createIfNotExists(Connection connection, String database) {
        this.connection = connection;
        try {
            PreparedStatement db = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
            db.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteIfExists(String database) {
        try {
            PreparedStatement db = this.connection.prepareStatement("DROP DATABASE IF EXISTS " + database);
            db.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeCustomRequest(String request) {
        try {
            PreparedStatement db = this.connection.prepareStatement(request);
            db.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}