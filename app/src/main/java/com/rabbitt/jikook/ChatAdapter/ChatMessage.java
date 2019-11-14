package com.rabbitt.jikook.ChatAdapter;

public class ChatMessage {
    private int isMine, type;
    private String message;

    public ChatMessage()
    {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsMine() {
        return isMine;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}