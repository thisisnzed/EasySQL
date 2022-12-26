package com.nz1337.easysql.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table {

    private String primaryKey;
    private final String tableName;
    private final StringBuilder columns;
    private final ArrayList<String> listedColumns;
    private Connection connection;
    private Column column;

    public Table(final String tableName) {
        this.tableName = tableName;
        this.columns = new StringBuilder();
        this.listedColumns = new ArrayList<>();
    }

    public void createIfNotExists(final Connection connection) {
        this.connection = connection;
        if (this.primaryKey.equals("")) this.primaryKey = this.listedColumns.get(0);
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (" + this.columns + "PRIMARY KEY (`" + this.primaryKey + "`))").execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        this.column = new Column(this, this.connection, this.listedColumns);
    }

    public Table addColumn(final String columnName, String type) {
        type = this.convert(type);
        this.columns.append("`").append(columnName).append("`").append(" ").append(type).append(", ");
        this.listedColumns.add(columnName);
        return this;
    }

    public Table setPrimaryKey(final String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public void delete() {
        try {
            this.connection.prepareStatement("DROP TABLE `" + this.getTable() + "`").execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getTable() {
        return this.tableName;
    }

    public Column getColumns() {
        return this.column;
    }

    public void makeCustomRequest(final String request) {
        try {
            final PreparedStatement prepareStatement = this.connection.prepareStatement(request);
            prepareStatement.execute();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    private String convert(String type) {
        switch (type.toLowerCase()) {
            case "integer":
            case "int":
                type = "INTEGER";
                break;
            case "string":
            case "str":
                type = "VARCHAR(255)";
                break;
            case "uuid":
            case "id":
                type = "VARCHAR(36)";
                break;
            case "boolean":
            case "bool":
                type = "TINYINT(1)";
                break;
            case "text":
            case "texte":
                type = "TEXT";
                break;
            case "double":
                type = "DOUBLE";
                break;
        }
        return type;
    }
}
