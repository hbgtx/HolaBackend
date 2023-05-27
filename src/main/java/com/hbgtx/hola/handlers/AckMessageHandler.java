package com.hbgtx.hola.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.models.EntityId;
import com.hbgtx.hola.models.Message;

import java.util.HashMap;

import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_EXTRA;
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
        try {
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(message.messageContent().getContent());
            if (jsonObject.has(KEY_MESSAGE_EXTRA)) {
                String ackedMessageId = jsonObject.get(KEY_MESSAGE_EXTRA).getAsString();
                if (idToMessageMap.containsKey(ackedMessageId)) {
                    idToMessageMap.remove(ackedMessageId);
                    System.out.println("Acknowledged MessageId:" + ackedMessageId);
                }
            }
        } catch (JsonParseException e) {
            System.out.println("Exception while parsing ack message:" + message);
            e.printStackTrace();
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
