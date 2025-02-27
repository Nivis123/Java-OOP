package command.factory;

import command.commands.Command;
import command.commands.SubCommand;

public class SubCreator implements Creator {
    public Command factoryMethod() {
        return new SubCommand();
    }
}
