package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.worker;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.Car;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body.Body;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine.Engine;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory.Accessory;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.CarStorage;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body.BodyStorage;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine.EngineStorage;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory.AccessoryStorage;

public class Worker implements Runnable {
    private final BodyStorage bodyStorage;
    private final EngineStorage engineStorage;
    private final AccessoryStorage accessoryStorage;
    private final CarStorage carStorage;

    public Worker(BodyStorage bodyStorage, EngineStorage engineStorage,
                  AccessoryStorage accessoryStorage, CarStorage carStorage) {
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.accessoryStorage = accessoryStorage;
        this.carStorage = carStorage;
    }

    @Override
    public void run() {
        try {
            Body body = bodyStorage.getBody();
            Engine engine = engineStorage.getEngine();
            Accessory accessory = accessoryStorage.getAccessory();

            Thread.sleep(100);

            Car car = new Car(body, engine, accessory);
            carStorage.addCar(car);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}