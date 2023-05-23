package com.hbgtx.hola.handlers;

import com.hbgtx.hola.models.Message;

public class InfoMessageHandler {
    public void handleMessage(Message message) {
        System.out.println("Handle info message" + message + " in future");
    }
}
