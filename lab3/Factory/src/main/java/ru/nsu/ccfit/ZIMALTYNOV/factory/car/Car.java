package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.IdGenerator;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body.Body;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine.Engine;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory.Accessory;

public class Car {
    private final int id;
    private final Body body;
    private final Engine engine;
    private final Accessory accessory;

    public Car(Body body, Engine engine, Accessory accessory) {
        this.id = IdGenerator.generateId();
        this.body = body;
        this.engine = engine;
        this.accessory = accessory;
    }

    public int getId() { return id; }
    public Body getBody() { return body; }
    public Engine getEngine() { return engine; }
    public Accessory getAccessory() { return accessory; }
}