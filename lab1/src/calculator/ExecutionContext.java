package calculator;

interface ExecutionContext {

    boolean isDefine(String key);

    Double getDefine(String key);

    void setDefine(String key, Double val);

    void pushStack(Double val);

    Double popStack();

    boolean stackIsEmpty();

    String getArg(Integer index);

    void setArgs(String line);

    void clearArgs();
}
