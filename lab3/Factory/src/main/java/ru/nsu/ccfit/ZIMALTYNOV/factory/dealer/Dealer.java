package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.dealer;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.Car;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.CarStorage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dealer implements Runnable {
    private final int id;
    private final CarStorage carStorage;
    private volatile int delay;
    private final boolean logSale;
    private PrintWriter logWriter;

    public Dealer(int id, CarStorage carStorage, int delay, boolean logSale) {
        this.id = id;
        this.carStorage = carStorage;
        this.delay = delay;
        this.logSale = logSale;

        if (logSale) {
            try {
                logWriter = new PrintWriter(new FileWriter("sales.log", true));
            } catch (IOException e) {
                System.err.println("Failed to open log file: " + e.getMessage());
            }
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(delay);
                Car car = carStorage.getCar();

                if (logSale && logWriter != null) {
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String logEntry = String.format("%s: Dealer %d: Auto %d (Body: %d, Motor: %d, Accessory: %d)",
                            time, id, car.getId(), car.getBody().getId(), car.getEngine().getId(), car.getAccessory().getId());

                    logWriter.println(logEntry);
                    logWriter.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (logWriter != null) {
            logWriter.close();
        }
    }
}