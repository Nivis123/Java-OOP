package calculator;

import command.factory.ClientCreator;
import command.commands.Command;
import command.factory.Creator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Calculator {

    public void calculateFile(String[] args) {
        try (Scanner scanner = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(System.in)) {
            calculate(scanner, args);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not exists. Use console\n");
            try (Scanner scanner = new Scanner(System.in)) {
                calculate(scanner, args);
            }
            catch (InputMismatchException | IllegalStateException e2) {
                System.out.println("Error with input stream");
                throw e2;
            }
        }
    }

    private void calculate(Scanner scanner, String[] args) {

        BaseExecutionContext exeContext;
        try {
            exeContext = new BaseExecutionContext();
        }
        catch (OutOfMemoryError e) {
            System.out.println("Error: lack memory");
            throw e;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            exeContext.setArgs(line);

            try {
                Creator creator = ClientCreator.chooseCreator(exeContext.getArg(0));
                Command command = creator.factoryMethod();
                command.toDo(exeContext);
            }
            catch (NullPointerException e) {
                System.out.println("Error: unknown command - " + exeContext.getArg(0));
            }
            exeContext.clearArgs();
        }
    }
}