package command.factory;

import command.commands.*;

public class ClientCreator {
    public static Creator chooseCreator(String command) {
        if (command.equals(CommentCommand.getNameCommand())) { return new CommentCreator(); }
        else if (command.equals(PushCommand.getNameCommand())) { return new PushCreator(); }
        else if (command.equals(PopCommand.getNameCommand())) { return new PopCreator(); }
        else if (command.equals(PrintCommand.getNameCommand())) { return new PrintCreator(); }
        else if (command.equals(DefineCommand.getNameCommand())) { return new DefineCreator(); }
        else if (command.equals(SQRTCommand.getNameCommand())) { return new SQRTCreator(); }
        else if (command.equals(PlusCommand.getNameCommand())) { return new PlucCreator(); }
        else if (command.equals(SubCommand.getNameCommand())) { return new SubCreator(); }
        else if (command.equals(MulCommand.getNameCommand())) { return new MulCreator(); }
        else if (command.equals(DivCommand.getNameCommand())) { return new DivCreator(); }
        return null;
    }
}
