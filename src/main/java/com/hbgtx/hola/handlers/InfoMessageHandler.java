package com.hbgtx.hola.handlers;

import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.models.Message;

import static com.hbgtx.hola.utils.Util.isServerId;

public class InfoMessageHandler {
    private final DBHelper dbHelper;

    public InfoMessageHandler() {
        dbHelper = new DBHelper();
    }

    public boolean handleMessage(Message message) {
        System.out.println("Handle info message:" + message );
        if (message.messageType() == MessageType.INFO_MESSAGE) {
            if (isServerId(message.receiverId())) {
                System.out.println("Handle info from User:" + message);
                return true;
            } else {
                UserHandler userHandler = dbHelper.getUserHandler(message.receiverId());
                if (userHandler != null) {
                    return userHandler.sendMessage(message);
                }
            }
        }
        return false;
    }
}
