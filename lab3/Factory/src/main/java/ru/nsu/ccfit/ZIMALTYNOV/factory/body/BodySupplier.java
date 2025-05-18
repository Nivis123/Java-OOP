package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.body;


public class BodySupplier implements Runnable {
    private final BodyStorage storage;
    private volatile int delay;

    public BodySupplier(BodyStorage storage, int delay) {
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
                storage.addBody(new Body());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}