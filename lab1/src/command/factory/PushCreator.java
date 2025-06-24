package command.factory;

import command.commands.Command;
import command.commands.PushCommand;

public class PushCreator implements Creator {
    public Command factoryMethod() {
        return new PushCommand();
    }
}
