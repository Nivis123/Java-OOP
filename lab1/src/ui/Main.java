package ui;

import calculator.Calculator;

import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        try {
            Calculator test = new Calculator();
            test.calculateFile(args);
        }
        catch (InputMismatchException | IllegalStateException | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
        catch (NoClassDefFoundError e) {
            System.out.println("Error: commands for calculator not fount");
        }
    }
}