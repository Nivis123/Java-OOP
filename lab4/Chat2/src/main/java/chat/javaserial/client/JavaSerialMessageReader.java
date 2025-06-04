package main.java.chat.javaserial.client;

import main.java.chat.common.ChatMessage;
import main.java.chat.common.MessageReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Consumer;

public class JavaSerialMessageReader implements MessageReader {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public JavaSerialMessageReader(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void readMessages(Consumer<ChatMessage> messageHandler, boolean running) throws IOException {
        try {
            while (running) {
                Object input = in.readObject();
                if (input instanceof ChatMessage) {
                    messageHandler.accept((ChatMessage) input);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка чтения сообщения: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }
}