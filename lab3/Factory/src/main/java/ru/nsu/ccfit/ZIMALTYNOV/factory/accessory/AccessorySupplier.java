package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.accessory;


public class AccessorySupplier implements Runnable {
    private final AccessoryStorage storage;
    private volatile int delay;

    public AccessorySupplier(AccessoryStorage storage, int delay) {
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
                storage.addAccessory(new Accessory());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}