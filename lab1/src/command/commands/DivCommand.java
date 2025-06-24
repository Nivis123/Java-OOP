package command.commands;

import calculator.ExecutionContext;
import command.utils.TwoOperandUtils;

public class DivCommand implements Command {
    public static final String NAME = "/";

    @Override
    public void execute(ExecutionContext context) {
        double[] operands = TwoOperandUtils.getOperands(context);
        if (operands[1] == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
        context.pushStack(operands[0] / operands[1]);
    }
}