package main.java.chat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        properties.setProperty("server.port", "5555");
        properties.setProperty("server.host", "localhost");
        properties.setProperty("server.logging", "true");
        properties.setProperty("client.timeout", "300000");
    }

    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public static String getServerHost() {
        return properties.getProperty("server.host", "localhost");
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(properties.getProperty("server.logging", "true"));
    }

    public static int getClientTimeout() {
        return Integer.parseInt(properties.getProperty("client.timeout", "300000"));
    }

    public static boolean useXmlProtocol() {
        return Boolean.parseBoolean(properties.getProperty("protocol.xml", "false"));
    }
}