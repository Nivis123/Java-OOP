package calculator;

import java.util.*;

public class CalculatorExecutionContext implements ExecutionContext {
    private final Map<String, Double> variables = new HashMap<>();
    private final Stack<Double> stack = new Stack<>();
    private final List<String> argList = new ArrayList<>();

    public boolean containsVariable(String key) {
        return variables.containsKey(key);
    }

    public Double getVariable(String key) {
        return variables.get(key);
    }

    public void setVariable(String key, Double val) {
        variables.put(key, val);
    }

    public void pushStack(Double val) {
        stack.push(val);
    }

    public Double popStack() {
        return stack.pop();
    }

    public boolean stackIsEmpty() {
        return stack.empty();
    }

    public String getArg(Integer index) {
        if (index < 0 || index >= argList.size()) {
            return null;
        }
        return argList.get(index);
    }

    public void setArgs(String line) {
        argList.clear();
        String[] words = line.split("\\s+");
        argList.addAll(Arrays.asList(words));
    }

    public void clearArgs() {
        argList.clear();
    }
}