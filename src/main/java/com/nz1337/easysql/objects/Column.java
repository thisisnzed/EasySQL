package com.nz1337.easysql.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Column {

    private final Connection connection;
    private final Table table;
    private final String tableName;
    private final String primaryKey;
    private final StringBuilder allColumns = new StringBuilder();

    public Column(final Table table, final Connection connection, final ArrayList<String> listedColumns) {
        this.connection = connection;
        this.table = table;
        this.primaryKey = table.getPrimaryKey();
        this.tableName = this.table.getTable();
        listedColumns.forEach(column -> {
            this.allColumns.append("`").append(column).append("`");
            if (!listedColumns.get(listedColumns.size() - 1).equals(column)) this.allColumns.append(",");
        });
    }

    public boolean isExists(final String column, final Object value) {
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + column + "`=?");
            preparedStatement.setObject(1, value);
            if (preparedStatement.executeQuery().next()) return true;
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void insertDefault(final Object... values) {
        final String primary = this.getPrimaryKey();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + primary + "`=?");
            preparedStatement.setObject(1, values[0]);
            preparedStatement.executeQuery().next();
            if (!this.isExists(primary, values[0])) {
                final StringBuilder unknownValue = new StringBuilder();
                for (int i = 0; i < countChars(this.allColumns.toString(), ',') + 1; i++) unknownValue.append("?,");
                unknownValue.append(")");
                final PreparedStatement insert = this.connection.prepareStatement("INSERT INTO `" + this.tableName + "` (" + this.allColumns + ") VALUE (" + unknownValue.toString().replace(",)", ")"));
                for (int i = 0; i < values.length; i++) insert.setObject(i + 1, values[i]);
                insert.executeUpdate();
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void editValue(final String searchedColumn, final Object searchedValue, final String editedColumn, final Object newValue) {
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE `" + this.getTableName() + "` SET `" + editedColumn + "`=? WHERE `" + searchedColumn + "`=?");
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, searchedValue);
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Object getValue(final String column, final Object value, final String desiredValue) {
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + column + "`=?");
            preparedStatement.setObject(1, value);
            final ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getObject(desiredValue);
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void delete(final String column, final Object value) {
        if (!this.isExists(column, value)) return;
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM `" + this.tableName + "` WHERE `" + this.tableName + "`.`" + column + "`=?");
            preparedStatement.setObject(1, value);
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public int countRows() {
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT COUNT(*) AS `rows` FROM `" + this.getTableName() + "`");
            final ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getInt("rows");
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public ArrayList<Object> getAll(final String columnLabel) {
        final ArrayList<Object> result = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM `" + this.getTableName() + "`");
            final ResultSet results = preparedStatement.executeQuery();
            while (results.next()) result.add(results.getObject(columnLabel));
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public void makeCustomRequest(final String request) {
        try {
            PreparedStatement prepareStatement = this.connection.prepareStatement(request);
            prepareStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Table getTable() {
        return this.table;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    private int countChars(final String s, final char c) {
        final AtomicInteger i = new AtomicInteger();
        s.chars().filter(chars -> chars == c).forEach(chars -> i.getAndIncrement());
        return i.get();
    }
}
