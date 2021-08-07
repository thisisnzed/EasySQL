package com.nz1337.easysql.manager;

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

    public Column(Table table, Connection connection, ArrayList<String> listedColumns) {
        this.connection = connection;
        this.table = table;
        this.primaryKey = table.getPrimaryKey();
        this.tableName = this.table.getTable();
        listedColumns.forEach(column -> {
            allColumns.append("`").append(column).append("`");
            if (!listedColumns.get(listedColumns.size() - 1).equals(column)) allColumns.append(",");
        });
    }

    public boolean isExists(String column, Object value) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + column + "`=?");
            preparedStatement.setObject(1, value);
            if (preparedStatement.executeQuery().next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertDefault(Object... values) {
        String primary = this.getPrimaryKey();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + primary + "`=?");
            preparedStatement.setObject(1, values[0]);
            preparedStatement.executeQuery().next();
            if (!isExists(primary, values[0])) {
                StringBuilder unknownValue = new StringBuilder();
                for (int i = 0; i < countChars(this.allColumns.toString(), ',') + 1; i++) unknownValue.append("?,");
                unknownValue.append(")");
                PreparedStatement insert = connection.prepareStatement("INSERT INTO `" + this.tableName + "` (" + this.allColumns + ") VALUE (" + unknownValue.toString().replace(",)", ")"));
                for (int i = 0; i < values.length; i++) insert.setObject(i + 1, values[i]);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editValue(String searchedColumn, Object searchedValue, String editedColumn, Object newValue) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + this.getTableName() + "` SET `" + editedColumn + "`=? WHERE `" + searchedColumn + "`=?");
            preparedStatement.setObject(1, newValue);
            preparedStatement.setObject(2, searchedValue);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getValue(String column, Object value, String desiredValue) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + this.tableName + "` WHERE `" + column + "`=?");
            preparedStatement.setObject(1, value);
            ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getObject(desiredValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(String column, Object value) {
        if (!isExists(column, value)) return;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM `" + this.tableName + "` WHERE `" + this.tableName + "`.`" + column + "`=?");
            preparedStatement.setObject(1, value);
            preparedStatement.executeUpdate();
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

    public Table getTable() {
        return this.table;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    private int countChars(String s, char c) {
        AtomicInteger i = new AtomicInteger();
        s.chars().forEach(chars -> {
            if (chars == c) i.getAndIncrement();
        });
        return i.get();
    }
}
