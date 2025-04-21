package ru.inmemorydb.command;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.core.Column;
import java.util.List;

public class CreateTableCommand implements Command {
    private String tableName;
    private List<Column> columns;

    public CreateTableCommand(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    @Override
    public void execute(Database database) {
        database.createTable(tableName, columns);
    }
}