package tests;

import calculator.Calculator;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class CalculatorTest {

    @Test
    public void unknownCommand() throws IOException {
        File file = new File("test_file.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Bad");
        }
        catch (Exception e) {
            file.delete();
            throw e;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        String[] args = new String[]{"test_file.txt"};
        Calculator calculator = new Calculator();
        calculator.calculateFile(args);
        String output = outputStream.toString().trim();
        assertEquals("Error: unknown command - Bad", output);

        System.setOut(originalOut);
        file.delete();
    }
}