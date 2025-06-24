package command.commands;

import calculator.BaseExecutionContext;

public class PopCommand implements Command {
    private static final String nameCommand = "POP";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        if (!exeContext.stackIsEmpty()) {
            exeContext.popStack();
        }
        else {
            System.out.println("Error POP(Stack already is empty)");
        }
    }
}
