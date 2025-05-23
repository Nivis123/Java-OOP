package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.car;

import java.util.LinkedList;
import java.util.Queue;

public class CarStorage {
    private final Queue<Car> queue;
    private final int capacity;
    private int producedCount = 0;
    private int soldCount = 0;

    public CarStorage(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public synchronized void addCar(Car car) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.add(car);
        producedCount++;
        notifyAll();
    }

    public synchronized Car getCar() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Car car = queue.poll();
        soldCount++;
        notifyAll();
        return car;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized int getProducedCount() {
        return producedCount;
    }

    public synchronized int getSoldCount() {
        return soldCount;
    }
}