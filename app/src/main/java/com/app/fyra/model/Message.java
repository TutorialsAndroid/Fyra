package com.app.fyra.model;

public class Message {
    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;

    public Message() {} // needed for Firestore

    public Message(String senderId, String receiverId, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
}

