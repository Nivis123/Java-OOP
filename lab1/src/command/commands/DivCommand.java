package command.commands;

import calculator.BaseExecutionContext;
import command.utils.TwoOperand;

import java.util.ArrayList;

public class DivCommand implements Command, TwoOperand {
    private static final String nameCommand = "/";

    public static String getNameCommand() {
        return nameCommand;
    }

    @Override
    public void toDo(BaseExecutionContext exeContext) {
        ArrayList<Double> values = getValues(exeContext);
        if (values == null) {
            System.out.println("Error: operation " + nameCommand + " not completed");
            return;
        }
        if (values.get(1) == 0.0) {
            System.out.println("Error: try div by zero");
            exeContext.pushStack(values.get(1));
            exeContext.pushStack(values.get(0));
        }
        else {
            exeContext.pushStack(values.get(0) / values.get(1));
        }
    }
}
