package com.hbgtx.hola.handlers;

import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.models.Message;

public class ChatHandler {
    private final DBHelper dbHelper;
    private final AckMessageHandler ackMessageHandler;

    public ChatHandler() {
        this.dbHelper = new DBHelper();
        this.ackMessageHandler = new AckMessageHandler();
    }

    public boolean handleMessage(Message message) {
        UserHandler userHandler = dbHelper.getUserHandler(message.receiverId());
        if (userHandler != null) {
            if (userHandler.sendMessage(message)) {
                ackMessageHandler.waitForAcknowledgement(message);
                return true;
            }
        } else {
            System.out.println("Handler not found for message:" + message);
        }
        return false;
    }
}
