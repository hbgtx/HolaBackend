package com.hbgtx.hola.handlers;

import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.models.EntityId;
import com.hbgtx.hola.models.Message;

import java.util.HashMap;

import static com.hbgtx.hola.utils.Util.isServerId;

public class AckMessageHandler {
    private static final HashMap<String, Message> idToMessageMap = new HashMap<>();
    private final DBHelper dbHelper;

    public AckMessageHandler() {
        this.dbHelper = new DBHelper();
    }


    public boolean handleMessage(Message message) {
        if (message.messageType().equals(MessageType.ACK_MESSAGE)) {
            EntityId receivedId = new EntityId(message.receiverId().getId());
            if (isServerId(receivedId)) {
                return handleAckReceived(message);
            } else {
                return sendAckMessage(message);
            }
        } else {
            System.out.println("Not an Ack message:" + message);
        }
        return false;
    }

    private boolean handleAckReceived(Message message) {
        if (idToMessageMap.containsKey(message.messageId().getId())) {
            idToMessageMap.remove(message.messageId().getId());
            System.out.println("Acknowledged MessageId:" + message.messageId());
        }
        return true;
    }

    private boolean sendAckMessage(Message message) {
        UserHandler userHandler = dbHelper.getUserHandler(message.receiverId());
        if (userHandler != null) {
            return userHandler.sendMessage(message);
        }
        return false;
    }

    public void waitForAcknowledgement(Message message) {
        if (message.messageType() == MessageType.ACK_MESSAGE) {
            return;
        }
        idToMessageMap.put(message.messageId().getId(), message);
    }
}
