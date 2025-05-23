package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body;

import java.util.LinkedList;
import java.util.Queue;

public class BodyStorage {
    private final Queue<Body> queue;
    private final int capacity;
    private int producedCount = 0;

    public BodyStorage(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    public synchronized void addBody(Body body) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.add(body);
        producedCount++;
        notifyAll();
    }

    public synchronized Body getBody() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Body body = queue.poll();
        notifyAll();
        return body;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized int getProducedCount() {
        return producedCount;
    }
}