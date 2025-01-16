package org.mariqzw.domainservice.services;

import io.grpc.stub.StreamObserver;
import org.mariqzw.domainservice.models.entity.MessageEntity;
import org.mariqzw.domainservice.repositories.MessageRepository;
import org.mariqzw.grpc.*;
import org.mariqzw.grpc.ChatServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private final MessageRepository messageRepository;

    @Autowired
    public ChatServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(ChatMessageRequest request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            // Save the message to the database
            MessageEntity message = new MessageEntity();
            message.setSender(request.getSender());
            message.setReceiver(request.getReceiver());
            message.setMessageText(request.getMessageText());
            message.setTimestamp(LocalDateTime.now().toString());

            MessageEntity savedMessage = messageRepository.save(message);

            // Build the response
            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Message created successfully.")
                    .setId(savedMessage.getId())
                    .setSender(savedMessage.getSender())
                    .setReceiver(savedMessage.getReceiver())
                    .setMessageText(savedMessage.getMessageText())
                    .setTimestamp(savedMessage.getTimestamp())
                    .build();

            // Send the response
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Handle exceptions
            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to create message: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getMessageById(ChatMessageByIdRequest request, StreamObserver<ChatMessageResponse> responseObserver) {
        Optional<MessageEntity> message = messageRepository.findById(request.getId());
        if (message.isPresent()) {
            MessageEntity entity = message.get();

            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Message found")
                    .setId(entity.getId())
                    .setSender(entity.getSender())
                    .setReceiver(entity.getReceiver())
                    .setMessageText(entity.getMessageText())
                    .setTimestamp(entity.getTimestamp())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Message not found"));
        }
    }

    @Override
    public void getAllMessages(ChatMessagesListRequest request, StreamObserver<ChatMessagesListResponse> responseObserver) {
        List<MessageEntity> messages = messageRepository.findAll();

        ChatMessagesListResponse.Builder responseBuilder = ChatMessagesListResponse.newBuilder();

        for (MessageEntity entity : messages) {
            ChatMessage chatMessage = ChatMessage.newBuilder()
                    .setId(entity.getId())
                    .setSender(entity.getSender())
                    .setReceiver(entity.getReceiver())
                    .setMessageText(entity.getMessageText())
                    .setTimestamp(entity.getTimestamp())
                    .build();

            responseBuilder.addMessages(chatMessage);
        }

        ChatMessagesListResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateMessage(ChatMessage request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Message update request sent successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteMessage(ChatMessageByIdRequest request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Message deletion request sent successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }
}
