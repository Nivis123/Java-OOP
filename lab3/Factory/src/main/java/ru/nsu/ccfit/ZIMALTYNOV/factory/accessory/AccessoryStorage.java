package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory;

import java.util.LinkedList;
import java.util.Queue;

public class AccessoryStorage {
    private final Queue<Accessory> queue;
    private final int capacity;
    private int producedCount = 0;

    public AccessoryStorage(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public synchronized void addAccessory(Accessory accessory) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.add(accessory);
        producedCount++;
        notifyAll();
    }

    public synchronized Accessory getAccessory() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Accessory accessory = queue.poll();
        notifyAll();
        return accessory;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized int getProducedCount() {
        return producedCount;
    }
}