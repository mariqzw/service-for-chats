package org.mariqzw.domainservice.models.dtos;

public class MessageDTO {

    private Long id;
    private String sender;
    private String receiver;
    private String messageText;
    private String timestamp;

    public MessageDTO() {
    }

    public MessageDTO(Long id, String sender, String receiver, String messageText, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
