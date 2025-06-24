package command.commands;

import calculator.BaseExecutionContext;

public class PrintCommand implements Command {
    private static final String nameCommand = "PRINT";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        if (exeContext.stackIsEmpty()) {
            System.out.println("Error PRINT (Stack already is empty)");
            return;
        }
        Double element = exeContext.popStack();

        System.out.println(element);
        exeContext.pushStack(element);
    }
}
