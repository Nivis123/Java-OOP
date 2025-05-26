package ru.inmemorydb.command;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.core.Row;
import java.util.List;

public class InsertCommand implements Command {
    private String tableName;
    private List<Row> rows;

    public InsertCommand(String tableName, List<Row> rows) {
        this.tableName = tableName;
        this.rows = rows;
    }

    @Override
    public void execute(Database database) {
        database.insertInto(tableName, rows);
    }
}