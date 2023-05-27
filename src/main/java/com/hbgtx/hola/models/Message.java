package com.hbgtx.hola.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbgtx.hola.enums.MessageType;

import static com.hbgtx.hola.utils.ConstantUtils.*;

public record Message(MessageType messageType, EntityId messageId, EntityId senderId, EntityId receiverId,
                      MessageContent messageContent, long timestamp) {
    private static final Object mutex = new Object();
    private static int customMessageId = 1;

    public Message(MessageType messageType, EntityId messageId, EntityId senderId, EntityId receiverId,
                   MessageContent messageContent, long timestamp) {
        this.messageType = messageType;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
    }

    public Message(MessageType messageType, EntityId messageId, EntityId senderId, EntityId receiverId,
                   MessageContent messageContent) {
        this(messageType, messageId, senderId, receiverId, messageContent, 0);
    }

    public static Message getMessageFromString(String message) {
        try {
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(message);
            long messageTimestamp = jsonObject.has(KEY_TIMESTAMP) ? jsonObject.get(KEY_TIMESTAMP).getAsLong() : -1;
            return new Message(
                    MessageType.get(jsonObject.get(KEY_MESSAGE_TYPE).getAsString()),
                    new EntityId(jsonObject.get(KEY_MESSAGE_ID).getAsString()),
                    new EntityId(jsonObject.get(KEY_SENDER).getAsString()),
                    new EntityId(jsonObject.get(KEY_RECEIVER).getAsString()),
                    new MessageContent(jsonObject.get(KEY_MESSAGE_CONTENT).getAsString()),
                    messageTimestamp);
        } catch (Exception e) {
            System.out.println("Unable to parse message:" + message);
            return null;
        }
    }

    public static Message getAckForUserMessage(Message message) {
        return new Message(MessageType.ACK_MESSAGE, getMessageId(),
                new EntityId(RESERVED_SERVER_ID), message.senderId(),
                new MessageContent(message.messageId.getId()));
    }

    private static EntityId getMessageId() {
        synchronized (mutex) {
            return new EntityId(String.valueOf(customMessageId++));
        }
    }

    public static Message getUserIdReceivedMessage(EntityId userId) {
        JsonObject messageContent = new JsonObject();
        messageContent.addProperty(KEY_MESSAGE_CONTENT_INFO, MESSAGE_USER_ID_RECEIVED);
        messageContent.addProperty(KEY_MESSAGE_EXTRA, userId.getId());

        return new Message(
                MessageType.INFO_MESSAGE, getMessageId(),
                new EntityId(RESERVED_SERVER_ID), userId,
                new MessageContent(messageContent.toString()));
    }

    public String toString() {
        JsonObject message = new JsonObject();
        message.addProperty(KEY_MESSAGE_TYPE, messageType().toString());
        message.addProperty(KEY_MESSAGE_ID, messageId().toString());
        message.addProperty(KEY_SENDER, senderId().toString());
        message.addProperty(KEY_RECEIVER, receiverId().toString());
        message.addProperty(KEY_MESSAGE_CONTENT, messageContent().toString());
        message.addProperty(KEY_TIMESTAMP, timestamp);
        return message.toString();
    }

}
