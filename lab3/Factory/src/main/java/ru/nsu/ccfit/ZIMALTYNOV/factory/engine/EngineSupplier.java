package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.engine;


public class EngineSupplier implements Runnable {
    private final EngineStorage storage;
    private volatile int delay;

    public EngineSupplier(EngineStorage storage, int delay) {
        this.storage = storage;
        this.delay = delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(delay);
                storage.addEngine(new Engine());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}