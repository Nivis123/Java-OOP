package command.factory;

import command.commands.Command;
import command.commands.PopCommand;

public class PopCreator implements Creator {
    public Command factoryMethod() {
        return new PopCommand();
    }
}
