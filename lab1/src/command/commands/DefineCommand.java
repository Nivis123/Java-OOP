package command.commands;

import calculator.BaseExecutionContext;

public class DefineCommand implements Command {
    private static final String nameCommand = "DEFINE";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        String value = null;
        try {
            String key = exeContext.getArg(1);
            value = exeContext.getArg(2);
            exeContext.setDefine(key, Double.parseDouble(value));
        }
        catch (NumberFormatException e) {
            System.out.println("Error set constanta: " + value + " is not digit");
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Error set constanta: you must use this pattern: DEFINE constanta digit");
        }
    }
}