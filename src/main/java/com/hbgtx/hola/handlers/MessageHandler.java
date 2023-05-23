package com.hbgtx.hola.handlers;

import com.hbgtx.hola.models.Message;

public class MessageHandler {
    private final ChatHandler chatHandler;
    private final PendingMessagesHandler pendingMessagesHandler;
    private final AckMessageHandler ackMessageHandler;
    private final RequestMessageHandler requestMessageHandler;
    private final TypingMessageHandler typingMessageHandler;
    private final InfoMessageHandler infoMessageHandler;

    public MessageHandler() {
        this.chatHandler = new ChatHandler();
        this.pendingMessagesHandler = new PendingMessagesHandler();
        this.ackMessageHandler = new AckMessageHandler();
        this.requestMessageHandler = new RequestMessageHandler();
        this.typingMessageHandler = new TypingMessageHandler();
        infoMessageHandler = new InfoMessageHandler();
    }

    public void handleMessage(Message message) {
        if (message == null) {
            return;
        }
        switch (message.messageType()) {
            case CHAT_MESSAGE -> handleChatMessage(message);
            case ACK_MESSAGE -> handleAckMessage(message);
            case TYPING_MESSAGE -> handleTypingMessage(message);
            case INFO_MESSAGE -> handleInfoMessage(message);
            case REQUEST_MESSAGE -> handleRequestMessage(message);
        }
    }

    private void handleChatMessage(Message message) {
        boolean messageHandled = chatHandler.handleMessage(message);
        if (!messageHandled) {
            pendingMessagesHandler.addToPendingMessages(message);
        }
    }

    private void handleAckMessage(Message message) {
        boolean messageHandled = ackMessageHandler.handleMessage(message);
        if (!messageHandled) {
            pendingMessagesHandler.addToPendingMessages(message);
        }
    }

    private void handleTypingMessage(Message message) {
        typingMessageHandler.handleMessage(message);
    }

    private void handleInfoMessage(Message message) {
        if (!infoMessageHandler.handleMessage(message)) {
            pendingMessagesHandler.addToPendingMessages(message);
        }
    }

    private void handleRequestMessage(Message message) {
        if (!requestMessageHandler.handleMessage(message)) {
            pendingMessagesHandler.addToPendingMessages(message);
        }
    }
}
