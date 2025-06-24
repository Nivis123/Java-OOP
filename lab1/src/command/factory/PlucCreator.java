package command.factory;

import command.commands.Command;
import command.commands.PlusCommand;

public class PlucCreator implements Creator {
    public Command factoryMethod() {
        return new PlusCommand();
    }
}
