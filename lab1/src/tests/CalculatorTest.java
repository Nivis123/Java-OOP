package tests;

import calculator.Calculator;
import org.junit.Test;
import java.io.*;

import static org.junit.Assert.*;

public class CalculatorTest {
    @Test
    public void testUnknownCommand() throws IOException {
        testOutput("UNKNOWN", "Unknown command: UNKNOWN");
    }

    @Test
    public void testDivisionByZero() throws IOException {
        testOutput("PUSH 10\nPUSH 0\n/", "Division by zero");
    }

    @Test
    public void testDefineCommand() throws IOException {
        testOutput("DEFINE x 10\nPRINT x", "10.0");
    }

    private void testOutput(String input, String expectedOutput) throws IOException {
        File tempFile = File.createTempFile("calc_test_", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(input);
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            new Calculator().calculateFile(new String[]{tempFile.getAbsolutePath()});
            assertTrue(outContent.toString().contains(expectedOutput));
        } finally {
            System.setOut(originalOut);
            tempFile.delete();
        }
    }
}