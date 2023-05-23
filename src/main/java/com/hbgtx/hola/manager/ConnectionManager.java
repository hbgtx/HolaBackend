package com.hbgtx.hola.manager;

import com.hbgtx.hola.callbacks.UserIdCallback;
import com.hbgtx.hola.database.DBHelper;
import com.hbgtx.hola.handlers.PendingMessagesHandler;
import com.hbgtx.hola.handlers.UserHandler;
import com.hbgtx.hola.models.EntityId;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class ConnectionManager extends Thread{
    private static ConnectionManager connectionManager;
    private final int port;
    private final DBHelper dbHelper;
    private final PendingMessagesHandler pendingMessagesHandler;
    private ServerSocket serverSocket;
    private boolean isListening = false;
    private boolean keepRunning = true;

    private ConnectionManager(int port) {
        this.port = port;
        this.dbHelper = new DBHelper();
        this.pendingMessagesHandler = new PendingMessagesHandler();
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
            while (keepRunning) {
                // keep listening for socket connections
                Socket socket = serverSocket.accept();
                UserHandler userHandler = new UserHandler(socket, new UserIdCallback() {
                    @Override
                    public void onUserIdReceived(EntityId userId, UserHandler userHandler) {
                        handleUserIdReceived(userId, userHandler);
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

    private void handleUserIdReceived(EntityId userId, UserHandler handler) {
        dbHelper.addUserHandler(userId.getId(), handler);
        pendingMessagesHandler.checkPendingMessages(userId, handler);
    }

    private void unregisterUserHandler(EntityId userId) {
        dbHelper.deleteUserHandler(userId);

    }

    public void stopListening() {
        keepRunning = false;
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
