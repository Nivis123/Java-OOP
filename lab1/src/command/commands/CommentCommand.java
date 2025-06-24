package command.commands;

import calculator.ExecutionContext;

public class CommentCommand implements Command {
    public static final String NAME = "#";

    @Override
    public void execute(ExecutionContext context) {
        String comment = context.getArg(1);
        if (comment != null) {
            System.out.println(comment);
        }
    }
}