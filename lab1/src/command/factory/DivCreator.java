package command.factory;

import command.commands.Command;
import command.commands.DivCommand;

public class DivCreator implements Creator {
    public Command factoryMethod() {
        return new DivCommand();
    }
}
