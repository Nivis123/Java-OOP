package calculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class.getName());

    public void calculateFile(String[] args) {
        try (Scanner scanner = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(System.in)) {
            calculate(scanner);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "File not found, switching to console input");
            calculate(new Scanner(System.in));
        }
    }

    private void calculate(Scanner scanner) {
        ExecutionContext context = new CalculatorExecutionContext();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            context.setArgs(line);
            String commandName = context.getArg(0);

            try {
                Command command = CommandFactory.createCommand(commandName);
                command.execute(context);
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, e.getMessage());
            } finally {
                context.clearArgs();
            }
        }
    }
}