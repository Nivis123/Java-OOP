package command.factory;

import command.commands.Command;
import command.commands.SQRTCommand;

public class SQRTCreator implements Creator {
    public Command factoryMethod() {
        return new SQRTCommand();
    }
}
