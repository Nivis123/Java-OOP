package ru.inmemorydb.core;

import java.util.*;
import java.util.function.Predicate;

public class Database {
    private String name;
    private Map<String, Table> tables = new HashMap<>();

    public Database() {
        this("default");
    }

    public Database(String name) {
        this.name = name;
    }

    public void createTable(String name, List<Column> columns) {
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException("Table already exists: " + name);
        }
        tables.put(name, new Table(name, columns));
    }

    public void dropTable(String name) {
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("Table not found: " + name);
        }
        tables.remove(name);
    }

    public void insertInto(String tableName, List<Row> rows) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        for (Row row : rows) {
            table.insertRow(row);
        }
    }

    public void deleteFrom(String tableName, Predicate<Row> condition) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        table.deleteRows(condition);
    }

    public void addColumn(String tableName, Column column) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        table.addColumn(column);
    }

    public void removeColumn(String tableName, String columnName) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        table.removeColumn(columnName);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public Collection<Table> getTables() {
        return tables.values();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}