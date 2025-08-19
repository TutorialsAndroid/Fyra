package com.app.fyra.model;

public class Message {
    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;
    private String status;

    public Message() {} // needed for Firestore

    public Message(String senderId, String receiverId, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Message(String senderId, String receiverId, String text, long timestamp, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }

    public String getStatus() {
        return status;
    }
}

