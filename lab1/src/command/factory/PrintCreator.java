package command.factory;

import command.commands.Command;
import command.commands.PrintCommand;

public class PrintCreator implements Creator {
    public Command factoryMethod() {
        return new PrintCommand();
    }
}
