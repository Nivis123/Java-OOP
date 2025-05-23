package command.factory;

import command.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put(CommentCommand.NAME, new CommentCommand());
        commandMap.put(PushCommand.NAME, new PushCommand());
        commandMap.put(PopCommand.NAME, new PopCommand());
        commandMap.put(PrintCommand.NAME, new PrintCommand());
        commandMap.put(DefineCommand.NAME, new DefineCommand());
        commandMap.put(SQRTCommand.NAME, new SQRTCommand());
        commandMap.put(PlusCommand.NAME, new PlusCommand());
        commandMap.put(SubCommand.NAME, new SubCommand());
        commandMap.put(MulCommand.NAME, new MulCommand());
        commandMap.put(DivCommand.NAME, new DivCommand());
    }

    public static Command createCommand(String name) {
        if (!commandMap.containsKey(name)) {
            throw new IllegalArgumentException("Unknown command: " + name);
        }
        return commandMap.get(name);
    }
}