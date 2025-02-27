package command.commands;

import calculator.BaseExecutionContext;

public class SQRTCommand implements Command {
    private static final String nameCommand = "SQRT";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        if (!exeContext.stackIsEmpty()) {
            Double val = exeContext.popStack();
            exeContext.pushStack(Math.sqrt(val));
        }
        else {
            System.out.println("Error SQRT: stack is empty");
        }
    }
}
