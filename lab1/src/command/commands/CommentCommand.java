package command.commands;

import calculator.BaseExecutionContext;

public class CommentCommand implements Command {
    private static final String nameCommand = "#";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        try {
            System.out.println(exeContext.getArg(1));
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println();
        }
    }
}
