package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CarFactoryConfig {
    private final int storageBodySize;
    private final int storageMotorSize;
    private final int storageAccessorySize;
    private final int storageAutoSize;
    private final int accessorySuppliers;
    private final int workers;
    private final int dealers;
    private final boolean logSale;

    public CarFactoryConfig(String configPath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
        }

        storageBodySize = Integer.parseInt(props.getProperty("StorageBodySize"));
        storageMotorSize = Integer.parseInt(props.getProperty("StorageMotorSize"));
        storageAccessorySize = Integer.parseInt(props.getProperty("StorageAccessorySize"));
        storageAutoSize = Integer.parseInt(props.getProperty("StorageAutoSize"));
        accessorySuppliers = Integer.parseInt(props.getProperty("AccessorySuppliers"));
        workers = Integer.parseInt(props.getProperty("Workers"));
        dealers = Integer.parseInt(props.getProperty("Dealers"));
        logSale = Boolean.parseBoolean(props.getProperty("LogSale"));
    }

    public int getStorageBodySize() { return storageBodySize; }
    public int getStorageMotorSize() { return storageMotorSize; }
    public int getStorageAccessorySize() { return storageAccessorySize; }
    public int getStorageAutoSize() { return storageAutoSize; }
    public int getAccessorySuppliers() { return accessorySuppliers; }
    public int getWorkers() { return workers; }
    public int getDealers() { return dealers; }
    public boolean isLogSale() { return logSale; }
}