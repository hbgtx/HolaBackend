package com.hbgtx.hola.models;

public class MessageContent {
    private final String content;

    public MessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}
