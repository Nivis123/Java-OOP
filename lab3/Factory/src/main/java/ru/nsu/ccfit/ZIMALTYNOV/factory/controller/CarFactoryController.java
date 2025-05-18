package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.controller;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car.CarStorage;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.threadpool.ThreadPool;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.worker.Worker;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.task.AssemblyTask;

public class CarFactoryController implements Runnable {
    private final CarStorage carStorage;
    private final ThreadPool threadPool;
    private final Worker worker;
    private final int minStockLevel;

    public CarFactoryController(CarStorage carStorage, ThreadPool threadPool,
                                Worker worker, int minStockLevel) {
        this.carStorage = carStorage;
        this.threadPool = threadPool;
        this.worker = worker;
        this.minStockLevel = minStockLevel;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (carStorage) {
                try {
                    while (carStorage.getSize() >= minStockLevel) {
                        carStorage.wait();
                    }

                    int needed = minStockLevel - carStorage.getSize();
                    for (int i = 0; i < needed; i++) {
                        threadPool.addTask(new AssemblyTask(worker));
                    }

                    carStorage.notifyAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}