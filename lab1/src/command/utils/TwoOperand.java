package command.utils;

import calculator.BaseExecutionContext;

import java.util.ArrayList;
import java.util.Arrays;

public interface TwoOperand {
    default ArrayList<Double> getValues(BaseExecutionContext exeContext) {

        if (exeContext.stackIsEmpty()) {
            System.out.println("Stack is Empty");
            return null;
        }
        Double value1 = exeContext.popStack();

        if (exeContext.stackIsEmpty()) {
            exeContext.pushStack(value1);
            System.out.println("Cant use operand, you have one decimal");
            return null;
        }
        if (exeContext.isDefine(String.valueOf(value1))) {
            value1 = exeContext.getDefine(String.valueOf(value1));
        }
        Double value2 = exeContext.popStack();

        if (exeContext.isDefine(String.valueOf(value2))) {
            value2 = exeContext.getDefine(String.valueOf(value2));
        }
        return new ArrayList<Double>(Arrays.asList(value1, value2));
    }
}
