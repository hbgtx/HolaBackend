package com.hbgtx.hola.manager;

import com.hbgtx.hola.callbacks.UserIdCallback;
import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.handlers.MessageHandler;
import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;
import com.hbgtx.hola.models.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager extends Thread {
    private static ConnectionManager connectionManager;
    private final int port;
    private final DBHelper dbHelper;
    private final MessageHandler messageHandler;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final Object mutex = new Object();
    private ServerSocket serverSocket;
    private boolean isListening = false;

    private ConnectionManager(int port) {
        this.port = port;
        this.dbHelper = new DBHelper();
        messageHandler = new MessageHandler();
    }

    public static ConnectionManager getInstance(int port) {
        return Objects.requireNonNullElseGet(connectionManager,
                () -> connectionManager = new ConnectionManager(port));
    }


    public void run() {
        if (isListening) {
            System.out.println("Connection Manager already started!");
        }
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server Started!");
            isListening = true;
            while (keepRunning.get()) {
                // keep listening for socket connections
                Socket socket = serverSocket.accept();
                UserHandler userHandler = new UserHandler(socket, new UserIdCallback() {
                    @Override
                    public boolean onUserIdReceived(EntityId userId, UserHandler userHandler) {
                        return handleUserIdReceived(userId, userHandler);
                    }

                    @Override
                    public void onUserDisconnected(EntityId userId) {
                        unregisterUserHandler(userId);
                    }
                });
                userHandler.start();
            }

        } catch (IOException e) {
            System.out.println("Exception in Server");
            e.printStackTrace();
        }
    }

    private boolean handleUserIdReceived(EntityId userId, UserHandler handler) {
        synchronized (mutex) {
            if (canAddUserHandler(userId)) {
                dbHelper.addUserHandler(userId.getId(), handler);
                messageHandler.handleMessage(Message.getUserIdReceivedMessage(userId));
                return true;
            }
            return false;
        }

    }

    private boolean canAddUserHandler(EntityId userId) {
        UserHandler userHandler = dbHelper.getUserHandler(userId);
        if (userHandler == null) {
            return true;
        }
        return !userHandler.isActive();
    }

    private void unregisterUserHandler(EntityId userId) {
        dbHelper.deleteUserHandler(userId);

    }

    public void stopListening() {
        keepRunning.set(false);
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to close server!");
        }
        List<UserHandler> userHandlers = dbHelper.getAndDeleteAllUserHandlers();
        for (UserHandler userHandler : userHandlers) {
            userHandler.stopListening();
        }
        isListening = false;
    }
}
