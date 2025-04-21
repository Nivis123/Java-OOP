package ru.inmemorydb.core;

public enum DataType {
    INT, STRING, DATE, BOOLEAN;

    public static DataType fromString(String type) {
        switch (type.toLowerCase()) {
            case "int": return INT;
            case "string": return STRING;
            case "date": return DATE;
            case "boolean": return BOOLEAN;
            default: throw new IllegalArgumentException("Unknown data type: " + type);
        }
    }
}