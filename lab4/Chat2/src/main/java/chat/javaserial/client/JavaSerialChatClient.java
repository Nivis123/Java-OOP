package main.java.chat.javaserial.client;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.ChatUser;
import main.java.chat.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class JavaSerialChatClient {
    private String sessionId;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<ChatMessage> messageHandler;
    private boolean running;

    public void connect(String host, int port, String username, Consumer<ChatMessage> messageHandler) throws IOException {
        this.messageHandler = messageHandler;
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.running = true;
        sendMessage(new ChatMessage(username, "", MessageType.LOGIN));


        new Thread(this::receiveMessages).start();
    }

    public void disconnect() {
        running = false;
        try {
            if (sessionId != null) {
                sendMessage(new ChatMessage("", "", MessageType.LOGOUT));
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении: " + e.getMessage());
        }
    }

    public void sendChatMessage(String message) {
        if (sessionId != null) {
            sendMessage(new ChatMessage(sessionId, message, MessageType.MESSAGE));
        }
    }

    public void requestUserList() {
        if (sessionId != null) {
            sendMessage(new ChatMessage(sessionId, "", MessageType.USER_LIST));
        }
    }

    private void sendMessage(ChatMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    private void receiveMessages() {
        try {
            while (running) {
                Object input = in.readObject();
                if (input instanceof ChatMessage) {
                    ChatMessage message = (ChatMessage) input;
                    processMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running) {
                System.err.println("Ошибка получения сообщения: " + e.getMessage());
            }
        }
    }

    private void processMessage(ChatMessage message) {
        switch (message.getType()) {
            case SUCCESS:
                this.sessionId = message.getMessage();
                break;
            case ERROR:
                System.err.println("Ошибка: " + message.getMessage());
                break;
        }
        messageHandler.accept(message);
    }
}