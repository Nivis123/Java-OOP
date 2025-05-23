package main.java.ru.nsu.ccfit.ZIMALTYNOV.factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
//"src/main/resources/config.properties"

public class CarFactoryGUI extends JFrame {
    private final CarFactory factory;
    private final JLabel bodyStorageLabel = new JLabel();
    private final JLabel engineStorageLabel = new JLabel();
    private final JLabel accessoryStorageLabel = new JLabel();
    private final JLabel carStorageLabel = new JLabel();
    private final JLabel producedCarsLabel = new JLabel();
    private final JLabel soldCarsLabel = new JLabel();
    private final JLabel tasksInQueueLabel = new JLabel();

    private final JSlider bodySupplierSlider = new JSlider(500, 3000, 1000);
    private final JSlider engineSupplierSlider = new JSlider(500, 3000, 1000);
    private final JSlider accessorySupplierSlider = new JSlider(500, 3000, 1000);
    private final JSlider dealerSlider = new JSlider(500, 3000, 3000);

    public CarFactoryGUI(String configPath) throws IOException {
        this.factory = new CarFactory(configPath);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Car Factory Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Body Storage:"));
        add(bodyStorageLabel);

        add(new JLabel("Engine Storage:"));
        add(engineStorageLabel);

        add(new JLabel("Accessory Storage:"));
        add(accessoryStorageLabel);

        add(new JLabel("Car Storage:"));
        add(carStorageLabel);

        add(new JLabel("Produced Cars:"));
        add(producedCarsLabel);

        add(new JLabel("Sold Cars:"));
        add(soldCarsLabel);

        add(new JLabel("Tasks in Queue:"));
        add(tasksInQueueLabel);

        add(new JLabel("Body Supplier Delay (ms):"));
        add(bodySupplierSlider);

        add(new JLabel("Engine Supplier Delay (ms):"));
        add(engineSupplierSlider);

        add(new JLabel("Accessory Supplier Delay (ms):"));
        add(accessorySupplierSlider);

        add(new JLabel("Dealer Delay (ms):"));
        add(dealerSlider);

        setupSlider(bodySupplierSlider, "BodySupplierDelay");
        setupSlider(engineSupplierSlider, "EngineSupplierDelay");
        setupSlider(accessorySupplierSlider, "AccessorySupplierDelay");
        setupSlider(dealerSlider, "DealerDelay");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                factory.stop();
                dispose();
            }
        });

        Timer updateTimer = new Timer(100, e -> updateStats());
        updateTimer.start();
    }

    private void setupSlider(JSlider slider, String propertyName) {
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(500);

        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                int value = slider.getValue();
                factory.updateDelay(propertyName, value);
            }
        });
    }

    private void updateStats() {
        bodyStorageLabel.setText(factory.getBodyStorage().getSize() + " / " + factory.getBodyStorage().getProducedCount());
        engineStorageLabel.setText(factory.getEngineStorage().getSize() + " / " + factory.getEngineStorage().getProducedCount());
        accessoryStorageLabel.setText(factory.getAccessoryStorage().getSize() + " / " + factory.getAccessoryStorage().getProducedCount());
        carStorageLabel.setText(factory.getCarStorage().getSize() + " / " + factory.getCarStorage().getProducedCount());
        soldCarsLabel.setText(String.valueOf(factory.getCarStorage().getSoldCount()));
        tasksInQueueLabel.setText(String.valueOf(factory.getThreadPool().getQueueSize()));
    }

    public void start() {
        factory.start();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            CarFactoryGUI gui = new CarFactoryGUI("src/main/resources/config.properties");
            gui.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to load configuration: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}