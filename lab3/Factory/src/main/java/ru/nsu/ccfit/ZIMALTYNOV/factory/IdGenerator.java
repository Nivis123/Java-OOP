package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory;

public class IdGenerator {
    private static int currentId = 0;

    public static synchronized int generateId() {
        return currentId++;
    }
}