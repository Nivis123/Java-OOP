package main.java.chat;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.javaserial.server.JavaSerialMessageWriter;
import main.java.chat.xml.server.XMLServerMessageWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App {
    public static void main(String[] args) {
        ServerMessageWriter messageWriter = Config.useXmlProtocol()
                ? new XMLServerMessageWriter()
                : new JavaSerialMessageWriter();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setLayout(new GridLayout(2, 1));

            JButton serverBtn = new JButton("Start Server");
            JButton clientBtn = new JButton("Start Client");

            serverBtn.addActionListener((ActionEvent e) -> {
                frame.dispose();
                new Thread(() -> new ChatServer(messageWriter).start()).start();
            });

            clientBtn.addActionListener((ActionEvent e) -> {
                frame.dispose();
                new ClientGUI().setVisible(true);
            });

            frame.add(serverBtn);
            frame.add(clientBtn);
            frame.setVisible(true);
        });
    }
}