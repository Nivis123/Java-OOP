package ru.inmemorydb.core;

public enum Constraint {
    UNIQUE, NOT_NULL, NONE;

    public static Constraint fromString(String constraint) {
        if (constraint == null || constraint.isEmpty()) return NONE;
        switch (constraint.toLowerCase()) {
            case "unique": return UNIQUE;
            case "not-null": return NOT_NULL;
            default: return NONE;
        }
    }
}