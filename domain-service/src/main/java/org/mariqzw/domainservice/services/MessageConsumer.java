package org.mariqzw.domainservice.services;

import io.grpc.stub.StreamObserver;
import org.mariqzw.domainservice.models.dtos.MessageDTO;
import org.mariqzw.grpc.ChatMessage;
import org.mariqzw.grpc.ChatMessageByIdRequest;
import org.mariqzw.grpc.ChatMessageRequest;
import org.mariqzw.grpc.ChatMessageResponse;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private final ChatServiceImpl chatService;

    @Autowired
    public MessageConsumer(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @RabbitListener(queuesToDeclare = @Queue("messages.create"))
    public void handleCreateMessage(MessageDTO messageDTO) {
        System.out.println("Create Message: " + messageDTO);
        chatService.createMessage(
                ChatMessageRequest.newBuilder()
                        .setSender(messageDTO.getSender())
                        .setReceiver(messageDTO.getReceiver())
                        .setMessageText(messageDTO.getMessageText())
                        .build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(ChatMessageResponse value) {
                        System.out.println("Create Message Response: " + value.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Failed to create message: " + t.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Create Message process completed");
                    }
                }
        );
    }

    @RabbitListener(queuesToDeclare = @Queue("messages.update"))
    public void handleUpdateMessage(MessageDTO messageDTO) {
        System.out.println("Update Message: " + messageDTO);
        chatService.updateMessage(
                ChatMessage.newBuilder()
                        .setId(messageDTO.getId())
                        .setSender(messageDTO.getSender())
                        .setReceiver(messageDTO.getReceiver())
                        .setMessageText(messageDTO.getMessageText())
                        .build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(ChatMessageResponse value) {
                        System.out.println("Update Message Response: " + value.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Failed to update message: " + t.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Update Message process completed");
                    }
                }
        );
    }

    @RabbitListener(queuesToDeclare = @Queue("messages.delete"))
    public void handleDeleteMessage(Long messageId) {
        System.out.println("Delete Message ID: " + messageId);
        chatService.deleteMessage(
                ChatMessageByIdRequest.newBuilder().setId(messageId).build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(ChatMessageResponse value) {
                        System.out.println("Delete Message Response: " + value.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Failed to delete message: " + t.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Delete Message process completed");
                    }
                }
        );
    }
}
