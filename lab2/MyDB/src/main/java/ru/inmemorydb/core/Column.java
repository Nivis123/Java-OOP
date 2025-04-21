package ru.inmemorydb.core;

public class Column {
    private String name;
    private DataType type;
    private Constraint constraint;

    public Column(String name, DataType type, Constraint constraint) {
        this.name = name;
        this.type = type;
        this.constraint = constraint;
    }

    public String getName() { return name; }
    public DataType getType() { return type; }
    public Constraint getConstraint() { return constraint; }

    public void setName(String name) { this.name = name; }
    public void setType(DataType type) { this.type = type; }
    public void setConstraint(Constraint constraint) { this.constraint = constraint; }
}