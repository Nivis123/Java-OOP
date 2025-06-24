package calculator;

import java.util.*;

public interface ExecutionContext {
    boolean containsVariable(String key);
    Double getVariable(String key);
    void setVariable(String key, Double val);
    void pushStack(Double val);
    Double popStack();
    boolean stackIsEmpty();
    String getArg(Integer index);
    void setArgs(String line);
    void clearArgs();
}