package ru.inmemorydb.command;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.core.Row;

import java.util.function.Predicate;

public class DeleteCommand implements Command {
    private String tableName;
    private Predicate<Row> condition;

    public DeleteCommand(String tableName, Predicate<Row> condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    @Override
    public void execute(Database database) {
        database.deleteFrom(tableName, condition);
    }
}