package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.threadpool;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadPool {
    private final Queue<Task> taskQueue = new LinkedList<>();
    private final Thread[] threads;
    private volatile boolean isRunning = true;

    public ThreadPool(int threadCount) {
        threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Worker());
            threads[i].start();
        }
    }

    public synchronized void addTask(Task task) {
        if (isRunning) {
            taskQueue.add(task);
            notify();
        }
    }

    public synchronized Task getTask() throws InterruptedException {
        while (taskQueue.isEmpty() && isRunning) {
            wait();
        }

        if (!isRunning) {
            return null;
        }

        return taskQueue.poll();
    }

    public synchronized int getQueueSize() {
        return taskQueue.size();
    }

    public void shutdown() {
        isRunning = false;
        synchronized (this) {
            notifyAll();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    Task task = getTask();
                    if (task != null) {
                        task.execute();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}