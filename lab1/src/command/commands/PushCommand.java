package command.commands;

import calculator.BaseExecutionContext;

public class PushCommand implements Command {
    private static final String nameCommand = "PUSH";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        try {
            String arg = exeContext.getArg(1);
            if (exeContext.isDefine(arg)) {
                Double value = exeContext.getDefine(arg);
                exeContext.pushStack(value);
            }
            else {
                exeContext.pushStack(Double.parseDouble(arg));
            }
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Error push element(push is empty)");
        }
        catch (NumberFormatException e) {
            System.out.println("Error push element: " + exeContext.getArg(1) + " is not digit or constanta");
        }
    }
}
