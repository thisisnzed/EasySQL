package com.nz1337.easysql.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table {

    private String primaryKey;
    private final String tableName;
    private final StringBuilder columns = new StringBuilder();
    private final ArrayList<String> listedColumns = new ArrayList<>();
    private Connection connection;
    private Column column;

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public void createIfNotExists(Connection connection) {
        this.connection = connection;
        if (this.primaryKey.equals("")) this.primaryKey = listedColumns.get(0);
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + this.tableName + "` (" + this.columns + "PRIMARY KEY (`" + this.primaryKey + "`))").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.column = new Column(this, this.connection, this.listedColumns);
    }

    public Table addColumn(String columnName, String type) {
        type = convert(type);
        this.columns.append("`").append(columnName).append("`").append(" ").append(type).append(", ");
        this.listedColumns.add(columnName);
        return this;
    }

    public Table setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public void delete() {
        try {
            this.connection.prepareStatement("DROP TABLE `" + this.getTable() + "`").execute();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void makeCustomRequest(String request) {
        try {
            PreparedStatement db = this.connection.prepareStatement(request);
            db.execute();
        } catch (SQLException e) {
            e.printStackTrace();
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
