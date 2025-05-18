package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine;

import java.util.LinkedList;
import java.util.Queue;

public class EngineStorage {
    private final Queue<Engine> queue;
    private final int capacity;
    private int producedCount = 0;

    public EngineStorage(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public synchronized void addEngine(Engine engine) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.add(engine);
        producedCount++;
        notifyAll();
    }

    public synchronized Engine getEngine() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Engine engine = queue.poll();
        notifyAll();
        return engine;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized int getProducedCount() {
        return producedCount;
    }
}