package command.commands;

import calculator.ExecutionContext;

public class DefineCommand implements Command {
    public static final String NAME = "DEFINE";

    @Override
    public void execute(ExecutionContext context) {
        String key = context.getArg(1);
        String valueStr = context.getArg(2);

        if (key == null || valueStr == null) {
            throw new IllegalArgumentException("DEFINE requires exactly 2 arguments");
        }

        try {
            double value = Double.parseDouble(valueStr);
            context.setVariable(key, value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + valueStr);
        }
    }
}