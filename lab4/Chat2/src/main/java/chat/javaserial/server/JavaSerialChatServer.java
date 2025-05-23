package main.java.chat.javaserial.server;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.ChatUser;
import main.java.chat.common.MessageType;
import main.java.chat.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaSerialChatServer {
    private static final Map<String, ChatUser> users = new HashMap<>();
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        int port = Config.getServerPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер чата запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientThread = new ClientHandler(clientSocket, clients, users);
                clients.add(clientThread);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            System.err.println("Ошибка сервера: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void broadcastMessage(ChatMessage message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client, ChatUser user) {
        clients.remove(client);
        if (user != null) {
            users.remove(user.getSessionId());
            broadcastMessage(new ChatMessage(user.getName(), "", MessageType.USER_LOGOUT), null);
            System.out.println(user.getName() + " покинул чат");
        }
    }
}