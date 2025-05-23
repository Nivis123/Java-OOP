package main.java.chat.common;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private String sender;
    private String message;
    private Date timestamp;
    private MessageType type;

    public ChatMessage(String sender, String message, MessageType type) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.timestamp = new Date();
    }


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (type == MessageType.USER_LOGIN) {
            return ">>> " + sender + " присоединился к чату";
        } else if (type == MessageType.USER_LOGOUT) {
            return "<<< " + sender + " покинул чат";
        } else if (type == MessageType.MESSAGE) {
            return sender + ": " + message;
        }
        return message;
    }
}