package command.commands;

import calculator.ExecutionContext;
import command.utils.TwoOperandUtils;

public class MulCommand implements Command {
    public static final String NAME = "*";

    @Override
    public void execute(ExecutionContext context) {
        double[] operands = TwoOperandUtils.getOperands(context);
        context.pushStack(operands[0] * operands[1]);
    }
}