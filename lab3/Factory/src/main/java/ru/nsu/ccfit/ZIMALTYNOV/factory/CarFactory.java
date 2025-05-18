package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.dealer.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.controller.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.worker.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.task.*;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.threadpool.ThreadPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CarFactory {
    private final CarFactoryConfig config;
    private final BodyStorage bodyStorage;
    private final EngineStorage engineStorage;
    private final AccessoryStorage accessoryStorage;
    private final CarStorage carStorage;
    private final ThreadPool threadPool;
    private final Map<String, Integer> delays = new HashMap<>();
    private BodySupplier bodySupplier;
    private EngineSupplier engineSupplier;
    private AccessorySupplier[] accessorySuppliers;
    private Dealer[] dealers;

    public CarFactory(String configPath) throws IOException {
        this.config = new CarFactoryConfig(configPath);
        this.bodyStorage = new BodyStorage(config.getStorageBodySize());
        this.engineStorage = new EngineStorage(config.getStorageMotorSize());
        this.accessoryStorage = new AccessoryStorage(config.getStorageAccessorySize());
        this.carStorage = new CarStorage(config.getStorageAutoSize());
        this.threadPool = new ThreadPool(config.getWorkers());

        delays.put("BodySupplierDelay", 1000);
        delays.put("EngineSupplierDelay", 1000);
        delays.put("AccessorySupplierDelay", 1000);
        delays.put("DealerDelay", 3000);
    }

    public void updateDelay(String propertyName, int value) {
        delays.put(propertyName, value);

        if (propertyName.equals("BodySupplierDelay") && bodySupplier != null) {
            bodySupplier.setDelay(value);
        } else if (propertyName.equals("EngineSupplierDelay") && engineSupplier != null) {
            engineSupplier.setDelay(value);
        } else if (propertyName.equals("AccessorySupplierDelay") && accessorySuppliers != null) {
            for (AccessorySupplier supplier : accessorySuppliers) {
                supplier.setDelay(value);
            }
        } else if (propertyName.equals("DealerDelay") && dealers != null) {
            for (Dealer dealer : dealers) {
                dealer.setDelay(value);
            }
        }
    }

    public void start() {
        bodySupplier = new BodySupplier(bodyStorage, delays.get("BodySupplierDelay"));
        engineSupplier = new EngineSupplier(engineStorage, delays.get("EngineSupplierDelay"));
        accessorySuppliers = new AccessorySupplier[config.getAccessorySuppliers()];

        for (int i = 0; i < config.getAccessorySuppliers(); i++) {
            accessorySuppliers[i] = new AccessorySupplier(accessoryStorage, delays.get("AccessorySupplierDelay"));
            new Thread(accessorySuppliers[i]).start();
        }

        Worker worker = new Worker(bodyStorage, engineStorage, accessoryStorage, carStorage);
        CarFactoryController controller = new CarFactoryController(carStorage, threadPool, worker, config.getStorageAutoSize() / 2);
        new Thread(controller).start();

        dealers = new Dealer[config.getDealers()];
        for (int i = 0; i < config.getDealers(); i++) {
            dealers[i] = new Dealer(i, carStorage, delays.get("DealerDelay"), config.isLogSale());
            new Thread(dealers[i]).start();
        }

        new Thread(bodySupplier).start();
        new Thread(engineSupplier).start();
    }

    public void stop() {
        if (bodySupplier != null) {
            Thread.currentThread().interrupt();
        }
        if (engineSupplier != null) {
            Thread.currentThread().interrupt();
        }
        for (AccessorySupplier supplier : accessorySuppliers) {
            Thread.currentThread().interrupt();
        }
        for (Dealer dealer : dealers) {
            Thread.currentThread().interrupt();
        }
        threadPool.shutdown();
    }

    public BodyStorage getBodyStorage() { return bodyStorage; }
    public EngineStorage getEngineStorage() { return engineStorage; }
    public AccessoryStorage getAccessoryStorage() { return accessoryStorage; }
    public CarStorage getCarStorage() { return carStorage; }
    public ThreadPool getThreadPool() { return threadPool; }
}