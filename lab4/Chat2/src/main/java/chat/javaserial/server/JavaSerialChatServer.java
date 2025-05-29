package main.java.chat.javaserial.server;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.common.ChatMessage;
import main.java.chat.common.ChatUser;
import main.java.chat.common.*;
import main.java.chat.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaSerialChatServer {
    private static final Logger logger = Logger.getLogger(JavaSerialChatServer.class.getName());
    private static final Map<String, ChatUser> users = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static ServerMessageWriter messageWriter;

    public static void main(String[] args, ServerMessageWriter writer) {
        messageWriter = writer;
        int port = Config.getServerPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.INFO, "Java Serial Chat Server started on port {0}", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientThread = new ClientHandler(clientSocket, clients, users);
                clients.add(clientThread);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server error: {0}", e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void broadcastMessage(ChatMessage message, ClientHandler excludeClient) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != excludeClient) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler client, ChatUser user) {
        synchronized (clients) {
            clients.remove(client);
        }
        if (user != null) {
            synchronized (users) {
                users.remove(user.getSessionId());
            }
            broadcastMessage(new ChatMessage(user.getName(), "", MessageType.USER_LOGOUT), null);
            logger.log(Level.INFO, "{0} left the chat", user.getName());
        }
    }
}