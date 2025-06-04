package main.java.chat;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageReader;
import main.java.chat.common.MessageType;
import main.java.chat.javaserial.client.JavaSerialMessageReader;
import main.java.chat.xml.client.XMLMessageReader;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private String sessionId;
    private Socket socket;
    private MessageReader messageReader;
    private boolean running;
    private Thread receiverThread;

    public void connect(String host, int port, String username, Consumer<ChatMessage> messageHandler) throws IOException {
        this.socket = new Socket(host, port);
        this.running = true;

        try {
            if (Config.useXmlProtocol()) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                this.messageReader = new XMLMessageReader(out, in);
                messageReader.sendMessage(new ChatMessage(username, "", MessageType.LOGIN));
            } else {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                this.messageReader = new JavaSerialMessageReader(out, in);
                messageReader.sendMessage(new ChatMessage(username, "", MessageType.LOGIN));
            }
        } catch (ParserConfigurationException e) {
            throw new IOException("Ошибка инициализации XML-парсера: " + e.getMessage(), e);
        }

        receiverThread = new Thread(() -> {
            try {
                messageReader.readMessages(message -> {
                    if (message.getType() == MessageType.SUCCESS && message.getMessage().startsWith("session:")) {
                        this.sessionId = message.getMessage().substring(8);
                    }
                    messageHandler.accept(message);
                }, running);
            } catch (IOException e) {
                if (running) {
                    System.err.println("Ошибка получения сообщения: " + e.getMessage());
                }
            }
        });
        receiverThread.start();
    }

    public void disconnect() {
        running = false;
        try {
            if (sessionId != null) {
                messageReader.sendMessage(new ChatMessage(sessionId, "", MessageType.LOGOUT));
            }
            messageReader.close();
            socket.close();
            receiverThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка отключения: " + e.getMessage());
        }
    }

    public void sendChatMessage(String message) {
        if (sessionId != null) {
            try {
                messageReader.sendMessage(new ChatMessage(sessionId, message, MessageType.MESSAGE));
            } catch (IOException e) {
                System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            }
        }
    }

    public void requestUserList() {
        if (sessionId != null) {
            try {
                messageReader.sendMessage(new ChatMessage(sessionId, "", MessageType.USER_LIST));
            } catch (IOException e) {
                System.err.println("Ошибка запроса списка пользователей: " + e.getMessage());
            }
        }
    }
}