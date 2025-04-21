package ru.inmemorydb.command;

import ru.inmemorydb.core.Database;

public interface Command {
    void execute(Database database) throws Exception;
}