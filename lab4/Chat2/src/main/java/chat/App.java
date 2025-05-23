package main.java.chat;

import main.java.chat.javaserial.client.JavaSerialClientGUI;
import main.java.chat.xml.client.XMLClientGUI;
import main.java.chat.javaserial.server.JavaSerialChatServer;
import main.java.chat.xml.server.XMLChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chat Application - Select Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setLayout(new GridLayout(2, 1));

            JButton serverBtn = new JButton("Start Server");
            JButton clientBtn = new JButton("Start Client");

            serverBtn.addActionListener((ActionEvent e) -> {
                frame.dispose();
                startServer();
            });

            clientBtn.addActionListener((ActionEvent e) -> {
                frame.dispose();
                startClient();
            });

            frame.add(serverBtn);
            frame.add(clientBtn);
            frame.setVisible(true);
        });
    }

    private static void startServer() {
        if (Config.useXmlProtocol()) {
            System.out.println("Starting XML Server");
            XMLChatServer.main(new String[]{});
        } else {
            System.out.println("Starting Java Serial Server");
            JavaSerialChatServer.main(new String[]{});
        }
    }

    private static void startClient() {
        if (Config.useXmlProtocol()) {
            System.out.println("Starting XML Client");
            new XMLClientGUI().setVisible(true);
        } else {
            System.out.println("Starting Java Serial Client");
            new JavaSerialClientGUI().setVisible(true);
        }
    }
}