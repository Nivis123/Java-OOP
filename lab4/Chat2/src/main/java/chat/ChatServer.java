package main.java.chat;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.common.ChatUser;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private static final Map<String, ChatUser> users = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final Map<String, ClientHandler> clients = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private final ServerMessageWriter messageWriter;

    public ChatServer(ServerMessageWriter messageWriter) {
        this.messageWriter = messageWriter;
    }

    public void start() {
        int port = Config.getServerPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.INFO, "Chat Server started on port {0} with {1} protocol",
                    new Object[]{port, Config.useXmlProtocol() ? "XML" : "Java Serial"});
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, users, messageWriter);
                clients.put(clientHandler.getSessionId(), clientHandler);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server error: {0}", e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void removeClient(ClientHandler client, ChatUser user) {
        synchronized (clients) {
            clients.remove(client.getSessionId());
        }
        if (user != null) {
            synchronized (users) {
                users.remove(user.getSessionId());
            }
        }
    }
}