package main.java.chat.javaserial.server;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.ChatUser;
import main.java.chat.common.MessageType;
import main.java.chat.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final List<ClientHandler> clients;
    private final Map<String, ChatUser> users;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatUser currentUser;
    private boolean running;

    public ClientHandler(Socket socket, List<ClientHandler> clients, Map<String, ChatUser> users) {
        this.clientSocket = socket;
        this.clients = clients;
        this.users = users;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            clientSocket.setSoTimeout(Config.getClientTimeout());

            while (running) {
                Object input = in.readObject();
                if (input instanceof ChatMessage) {
                    ChatMessage message = (ChatMessage) input;
                    processMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, "Client error: {0}", e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void processMessage(ChatMessage message) throws IOException {
        switch (message.getType()) {
            case LOGIN:
                handleLogin(message);
                break;
            case LOGOUT:
                handleLogout();
                break;
            case MESSAGE:
                handleChatMessage(message);
                break;
            case USER_LIST:
                sendUserList();
                break;
        }
    }

    private void handleLogin(ChatMessage message) throws IOException {
        String username = message.getSender();
        if (username == null || username.trim().isEmpty()) {
            sendMessage(new ChatMessage("Сервер", "Имя пользователя не может быть пустым", MessageType.ERROR));
            return;
        }

        String sessionId = UUID.randomUUID().toString();
        currentUser = new ChatUser(username, sessionId);

        synchronized (users) {
            users.put(sessionId, currentUser);
        }

        sendMessage(new ChatMessage("Сервер", sessionId, MessageType.SUCCESS));
        JavaSerialChatServer.broadcastMessage(new ChatMessage(username, "", MessageType.USER_LOGIN), this);
        logger.log(Level.INFO, "{0} joined the chat", username);
    }

    private void handleLogout() {
        running = false;
    }

    private void handleChatMessage(ChatMessage message) {
        JavaSerialChatServer.broadcastMessage(
                new ChatMessage(currentUser.getName(), message.getMessage(), MessageType.MESSAGE),
                this);
    }

    private void sendUserList() throws IOException {
        StringBuilder userList = new StringBuilder("Участники чата:\n");
        synchronized (users) {
            for (ChatUser user : users.values()) {
                userList.append("- ").append(user.getName()).append("\n");
            }
        }
        sendMessage(new ChatMessage("Сервер", userList.toString(), MessageType.USER_LIST));
    }

    public void sendMessage(ChatMessage message) {
        try {
            synchronized (out) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error sending message: {0}", e.getMessage());
        }
    }

    private void cleanup() {
        JavaSerialChatServer.removeClient(this, currentUser);
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing socket: {0}", e.getMessage());
        }
    }
}