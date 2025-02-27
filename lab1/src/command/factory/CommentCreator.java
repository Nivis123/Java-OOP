package command.factory;

import command.commands.Command;
import command.commands.CommentCommand;

public class CommentCreator implements Creator {
    public Command factoryMethod() {
        return new CommentCommand();
    }
}
