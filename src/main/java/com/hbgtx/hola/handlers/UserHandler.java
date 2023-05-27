package com.hbgtx.hola.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbgtx.hola.callbacks.UserIdCallback;
import com.hbgtx.hola.models.EntityId;
import com.hbgtx.hola.models.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hbgtx.hola.utils.ConstantUtils.*;
import static com.hbgtx.hola.utils.Util.isServerId;

public class UserHandler extends Thread {
    private final Socket socket;
    private final MessageHandler messageHandler;
    private final UserIdCallback userIdCallback;
    private EntityId userId;
    private PrintWriter out;
    private BufferedReader in;
    private boolean userIdReceived = false;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);


    public UserHandler(Socket socket, UserIdCallback userIdCallback) {
        this.socket = socket;
        this.messageHandler = new MessageHandler();
        this.userIdCallback = userIdCallback;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Get userId before sending/receiving message
            listenForUserId();
            listenForMessages();
            stopListening();
        } catch (IOException e) {
            System.out.println("IO Exception for user:" + userId);
            e.printStackTrace();
        }
    }

    public void stopListening() {
        this.keepRunning.set(false);
        this.userIdReceived = false;
        userIdCallback.onUserDisconnected(userId);
        System.out.println("User:" + userId + " stopped listening");
    }

    private void setUserId(EntityId userId) {
        if (this.userId == null) {
            this.userId = userId;
            this.userIdReceived = true;
        }
    }

    public boolean sendMessage(Message message) {
        System.out.println("Sending Message: " + message);
        if (isActive()) {
            out.println(message);
            return true;
        }
        return false;
    }

    public boolean isActive() {
        if (socket.isConnected() && userIdReceived) {
            try {
                socket.sendUrgentData(PING_DATA);
                return true;
            } catch (IOException e) {
                System.out.println("User is down:" + userId);
            }
        }
        return false;
    }

    private void listenForUserId() throws IOException {
        String inputLine;
        int readResult;
        while (keepRunning.get() && ((readResult = in.read()) != -1)) {
            inputLine = (char) readResult + in.readLine();
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(inputLine);
            if (jsonObject.has(KEY_USER_ID)) {
                String userIdValue = jsonObject.get(KEY_USER_ID).getAsString();
                EntityId userId = new EntityId(userIdValue);
                if (isServerId(userId)) {
                    System.out.println("Invalid User Id:" + userIdValue);
                    continue;
                }
                setUserId(userId);
                System.out.println("User id:" + userIdValue);
                if (userIdCallback.onUserIdReceived(userId, this)) {
                 return;
                }
            } else {
                out.println(USER_ID_REQUEST);
            }
        }
    }

    private void listenForMessages() throws IOException {
        System.out.println("listening messages for user:" + userId);
        String inputLine;
        int readResult;
        while (keepRunning.get() && ((readResult = in.read()) != -1)) {
            inputLine = (char) readResult + in.readLine();
            System.out.println("Message received from user:" + userId + " Message:" + inputLine);
            Message message = Message.getMessageFromString(inputLine);
            if (message != null) {
                sendAck(message);
                messageHandler.handleMessage(message);
            }
        }
    }

    private void sendAck(Message message) {
        System.out.println("Send ack for message:" + message);
        messageHandler.handleMessage(Message.getAckForUserMessage(message));
    }
}
