package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.IdGenerator;

public class Body {
    private final int id;

    public Body() {
        this.id = IdGenerator.generateId();
    }

    public int getId() {
        return id;
    }
}