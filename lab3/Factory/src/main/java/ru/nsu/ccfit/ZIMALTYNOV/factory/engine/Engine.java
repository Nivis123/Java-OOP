package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.IdGenerator;

public class Engine {
    private final int id;

    public Engine() {
        this.id = IdGenerator.generateId();
    }

    public int getId() {
        return id;
    }
}