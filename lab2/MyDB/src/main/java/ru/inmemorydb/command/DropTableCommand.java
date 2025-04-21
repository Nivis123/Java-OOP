package ru.inmemorydb.command;

import ru.inmemorydb.core.Database;

public class DropTableCommand implements Command {
    private String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void execute(Database database) {
        database.dropTable(tableName);
    }
}