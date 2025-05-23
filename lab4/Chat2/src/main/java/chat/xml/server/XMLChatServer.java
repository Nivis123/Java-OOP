package main.java.chat.xml.server;

import main.java.chat.Config;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XMLChatServer {
    private static final Map<String, String> sessions = new HashMap<>();
    private static final Map<String, String> users = new HashMap<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        int port = Config.getServerPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("XML Chat Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                XMLClientHandler clientThread = new XMLClientHandler(clientSocket, sessions, users);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void broadcastMessage(String message, String excludeSession) throws IOException {
        for (Map.Entry<String, String> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeSession)) {
                String user = users.get(entry.getKey());
                String xmlMessage = "<event name=\"message\">" +
                        "<message>" + escapeXml(message) + "</message>" +
                        "<name>" + escapeXml(user) + "</name>" +
                        "</event>";
                sendXml(entry.getValue(), xmlMessage);
            }
        }
    }

    public static void sendXml(String sessionId, String xml) throws IOException {
        Socket socket = new Socket("localhost", Integer.parseInt(sessionId.split(":")[1]));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        byte[] bytes = xml.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.write(bytes);
        out.flush();
        socket.close();
    }

    private static String escapeXml(String input) {
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}