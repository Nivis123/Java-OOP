package command.commands;

import calculator.ExecutionContext;

public interface Command {
    void execute(ExecutionContext context);
}