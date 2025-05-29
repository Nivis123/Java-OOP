package main.java.chat.xml.server;

import main.java.chat.common.ServerMessageWriter;
import main.java.chat.Config;

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

public class XMLChatServer {
    private static final Logger logger = Logger.getLogger(XMLChatServer.class.getName());
    private static final Map<String, String> sessions = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final Map<String, String> users = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static ServerMessageWriter messageWriter;

    public static void main(String[] args, ServerMessageWriter writer) {
        messageWriter = writer;
        int port = Config.getServerPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.INFO, "XML Chat Server started on port {0}", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                XMLClientHandler clientThread = new XMLClientHandler(clientSocket, sessions, users, messageWriter);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Server error: {0}", e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void broadcastMessage(String message, String excludeSession) throws IOException {
        messageWriter.broadcastMessage(message, excludeSession, sessions, users);
    }

    public static void sendXml(String sessionId, String xml) throws IOException {
        messageWriter.sendMessage(sessionId, xml, sessions);
    }

    public static void removeClient(String sessionId, String username) {
        synchronized (sessions) {
            sessions.remove(sessionId);
            users.remove(sessionId);
        }
        try {
            messageWriter.broadcastUserEvent("userlogout", username, sessionId, sessions, users);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error broadcasting logout: {0}", e.getMessage());
        }
    }
}