package command.factory;

import command.commands.Command;
import command.commands.MulCommand;

public class MulCreator implements Creator {
    public Command factoryMethod() {
        return new MulCommand();
    }
}
