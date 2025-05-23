package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.IdGenerator;

public class Accessory {
    private final int id;

    public Accessory() {
        this.id = IdGenerator.generateId();
    }

    public int getId() {
        return id;
    }
}