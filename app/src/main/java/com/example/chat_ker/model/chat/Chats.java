package com.example.chat_ker.model.chat;

public class Chats {
    private String dateTime;
    private String textMessage;
    private String type;
    private String sender;
    private String receiver;
    private boolean isseen;

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public Chats() {
    }

    public Chats(String dateTime, String textMessage, String type, String sender, String receiver,boolean isseen) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.isseen=isseen;

    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
