package com.nz1337.easysql.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private Connection connection;

    public void createIfNotExists(final Connection connection, final String database) {
        this.connection = connection;
        try {
            final PreparedStatement prepareStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS `" + database + "`");
            prepareStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteIfExists(final String database) {
        try {
            final PreparedStatement prepareStatement = this.connection.prepareStatement("DROP DATABASE IF EXISTS `" + database + "`");
            prepareStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void makeCustomRequest(final String request) {
        try {
            final PreparedStatement prepareStatement = this.connection.prepareStatement(request);
            prepareStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }
}