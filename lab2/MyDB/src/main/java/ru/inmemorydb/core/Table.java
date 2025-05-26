package ru.inmemorydb.core;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public class Table {
    private String name;
    private List<Column> columns = new ArrayList<>();
    private List<Row> rows = new ArrayList<>();
    private Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
    private Map<String, Set<Object>> uniqueValues = new HashMap<>();

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = new ArrayList<>(columns);
        for (int i = 0; i < columns.size(); i++) {
            columnIndexMap.put(columns.get(i).getName(), i);
            if (columns.get(i).getConstraint() == Constraint.UNIQUE) {
                uniqueValues.put(columns.get(i).getName(), new HashSet<>());
            }
        }
    }

    public void addColumn(Column column) {
        columns.add(column);
        columnIndexMap.put(column.getName(), columns.size() - 1);
        if (column.getConstraint() == Constraint.UNIQUE) {
            uniqueValues.put(column.getName(), new HashSet<>());
        }
        for (Row row : rows) {
            row.setValue(column.getName(), null);
        }
    }

    public void removeColumn(String columnName) {
        if (!columnIndexMap.containsKey(columnName)) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }
        int index = columnIndexMap.get(columnName);
        columns.remove(index);
        uniqueValues.remove(columnName);
        columnIndexMap.clear();
        for (int i = 0; i < columns.size(); i++) {
            columnIndexMap.put(columns.get(i).getName(), i);
        }
        for (Row row : rows) {
            Map<String, Object> values = row.getValues();
            values.remove(columnName);
            row.setValue(columnName, null);
        }
    }

    public void insertRow(Row row) {
        for (Column column : columns) {
            Object value = row.getValue(column.getName());

            if (column.getConstraint() == Constraint.NOT_NULL && value == null) {
                throw new IllegalArgumentException("Column " + column.getName() + " cannot be null");
            }

            if (column.getConstraint() == Constraint.UNIQUE && value != null) {
                if (uniqueValues.get(column.getName()).contains(value)) {
                    throw new IllegalArgumentException("Duplicate value for unique column " + column.getName());
                }
            }
        }

        for (Column column : columns) {
            if (column.getConstraint() == Constraint.UNIQUE) {
                Object value = row.getValue(column.getName());
                if (value != null) {
                    uniqueValues.get(column.getName()).add(value);
                }
            }
        }

        rows.add(row);
    }

    public void deleteRows(Predicate<Row> condition) {
        Iterator<Row> iterator = rows.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (condition.test(row)) {
                for (Column column : columns) {
                    if (column.getConstraint() == Constraint.UNIQUE) {
                        Object value = row.getValue(column.getName());
                        if (value != null) {
                            uniqueValues.get(column.getName()).remove(value);
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    public List<Row> getRows() {
        return new ArrayList<>(rows);
    }

    public List<Column> getColumns() {
        return new ArrayList<>(columns);
    }

    public String getName() {
        return name;
    }
}