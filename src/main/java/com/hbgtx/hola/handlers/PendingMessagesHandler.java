package com.hbgtx.hola.handlers;

import com.hbgtx.hola.models.EntityId;
import com.hbgtx.hola.models.Message;

import java.util.*;

public class PendingMessagesHandler {
    private static final Object mutex = new Object();
    private static final HashMap<String, Queue<Message>> pendingMessages = new HashMap<>();

    public PendingMessagesHandler() {
    }

    public void addToPendingMessages(Message message) {
        synchronized (mutex) {
            if (pendingMessages.containsKey(message.receiverId().getId())) {
                pendingMessages.get(message.receiverId().getId()).add(message);
                System.out.println("message added to pending:" + message);
                return;
            }

            System.out.println("message added to pending message:" + message);
            pendingMessages.put(message.receiverId().getId(), new LinkedList<>(List.of(message)));
        }
    }

    public void checkPendingMessages(EntityId userId, UserHandler userHandler) {
        if (userHandler == null) {
            System.out.println("No handler found for user:" + userId.getId());
            return;
        }
        synchronized (mutex) {
            Queue<Message> messages = pendingMessages.get(userId.getId());
            if (messages == null) {
                System.out.println("No pending messages for user:" + userId);
                return;
            }
            System.out.println("Sending pending messages for user:" + userId);
            List<Message> failedMessages = new ArrayList<>();
            Message message;
            while ((message = messages.poll()) != null) {
                System.out.println("pending message:" + message);
                if (!userHandler.sendMessage(message)) {
                    failedMessages.add(message);
                }
            }
            pendingMessages.put(userId.getId(), new LinkedList<>(failedMessages));
        }
    }
}
