package com.hbgtx.hola.handlers;

import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.models.Message;

public class TypingMessageHandler {
    private final DBHelper dbHelper;

    public TypingMessageHandler() {
        this.dbHelper = new DBHelper();
    }

    public void handleMessage(Message message) {
        System.out.println("Handle typing message:" + message);
        if (message.messageType() == MessageType.TYPING_MESSAGE) {
            UserHandler userHandler = dbHelper.getUserHandler(message.receiverId());
            if (userHandler != null) {
                if (userHandler.isActive()) {
                    userHandler.sendMessage(message);
                }
            }
        }
    }
}
