package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.task;

import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.worker.Worker;
import main.java.ru.nsu.ccfit.ZIMALTYNOV.factory.threadpool.Task;

public class AssemblyTask implements Task {
    private final Worker worker;

    public AssemblyTask(Worker worker) {
        this.worker = worker;
    }

    @Override
    public void execute() {
        worker.run();
    }
}