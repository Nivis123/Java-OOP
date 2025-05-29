package main.java.chat;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.javaserial.server.JavaSerialChatServer;
import main.java.chat.javaserial.client.JavaSerialClientGUI;
import main.java.chat.xml.server.XMLChatServer;
import main.java.chat.xml.client.XMLClientGUI;
import main.java.chat.xml.server.XMLServerMessageWriter;
import main.java.chat.javaserial.server.JavaSerialMessageWriter;

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
                startServer(messageWriter);
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

    private static void startServer(ServerMessageWriter messageWriter) {
        System.out.println("Starting Server with " +
                (Config.useXmlProtocol() ? "XML" : "Java Serial") + " protocol");
        if (Config.useXmlProtocol()) {
            XMLChatServer.main(new String[]{}, messageWriter);
        } else {
            JavaSerialChatServer.main(new String[]{}, messageWriter);
        }
    }

    private static void startClient() {
        System.out.println("Starting Client with " +
                (Config.useXmlProtocol() ? "XML" : "Java Serial") + " protocol");
        if (Config.useXmlProtocol()) {
            new XMLClientGUI().setVisible(true);
        } else {
            new JavaSerialClientGUI().setVisible(true);
        }
    }
}