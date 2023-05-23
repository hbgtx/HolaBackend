package com.hbgtx.hola.handlers;

import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.models.Message;

import static com.hbgtx.hola.utils.ConstantUtils.MESSAGE_REQUEST;
import static com.hbgtx.hola.utils.Util.isServerId;

public class RequestMessageHandler {
    private final PendingMessagesHandler pendingMessagesHandler;
    private final DBHelper dbHelper;

    public RequestMessageHandler() {
        this.pendingMessagesHandler = new PendingMessagesHandler();
        this.dbHelper = new DBHelper();
    }

    public boolean handleMessage(Message message) {
        if (message.messageType() != MessageType.REQUEST_MESSAGE) {
            System.out.println("Not a request message");
            return false;
        }
        if (isServerId(message.receiverId())) {
            return handleUserRequest(message);
        } else {
            return sendRequestToUser(message);
        }
    }

    private boolean handleUserRequest(Message message) {
        if (message.messageContent().getContent().equals(MESSAGE_REQUEST)) {
            pendingMessagesHandler.checkPendingMessages(message.senderId(),
                    dbHelper.getUserHandler(message.senderId()));
        }
        return true;
    }

    private boolean sendRequestToUser(Message message) {
        UserHandler userHandler = dbHelper.getUserHandler(message.receiverId());
        if (userHandler == null) {
            System.out.println("No handler found for user:" + message.receiverId().getId());
            return false;
        }
        return userHandler.sendMessage(message);
    }
}
