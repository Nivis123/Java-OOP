package ru.inmemorydb.core;

import java.util.HashMap;
import java.util.Map;

public class Row {
    private Map<String, Object> values = new HashMap<>();

    public void setValue(String columnName, Object value) {
        values.put(columnName, value);
    }

    public Object getValue(String columnName) {
        return values.get(columnName);
    }

    public Map<String, Object> getValues() {
        return new HashMap<>(values);
    }
}