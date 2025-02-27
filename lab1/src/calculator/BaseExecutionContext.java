package calculator;

import java.util.*;

public class BaseExecutionContext implements ExecutionContext {

    private final Map<String, Double> defineMap = new HashMap<>();
    private final Stack<Double> stack = new Stack<>();
    private final List<String> argList = new ArrayList<>();

    public boolean isDefine(String key) {
        return defineMap.containsKey(key);
    }

    public Double getDefine(String key) {
        return defineMap.get(key);
    }

    public void setDefine(String key, Double val) {
        defineMap.put(key, val);
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
        return argList.get(index);
    }

    public void setArgs(String line) {
        String[] words = line.split(" ");
        argList.addAll(Arrays.asList(words));
    }

    public void clearArgs() {
        argList.clear();
    }
}
