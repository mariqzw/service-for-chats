package org.mariqzw.domainservice.models.dtos;

import java.time.LocalDateTime;

public class MessageDTO {

    private String sender;
    private String receiver;
    private String messageText;
    private LocalDateTime timestamp;

    public MessageDTO() {
    }

    public MessageDTO(String sender, String receiver, String messageText, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.timestamp = timestamp;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
