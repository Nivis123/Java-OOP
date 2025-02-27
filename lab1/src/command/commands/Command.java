package command.commands;

import calculator.BaseExecutionContext;

public interface Command {
    void toDo(BaseExecutionContext exeContext);
}

