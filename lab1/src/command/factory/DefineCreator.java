package command.factory;

import command.commands.Command;
import command.commands.DefineCommand;

public class DefineCreator implements Creator {
    public Command factoryMethod() {
        return new DefineCommand();
    }
}
