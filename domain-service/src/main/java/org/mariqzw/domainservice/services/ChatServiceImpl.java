package org.mariqzw.domainservice.services;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.mariqzw.domainservice.exception.DatabaseUnavailableException;
import org.mariqzw.domainservice.exception.MessageNotFoundException;
import org.mariqzw.domainservice.models.dtos.MessageDTO;
import org.mariqzw.domainservice.models.entity.MessageEntity;
import org.mariqzw.domainservice.repositories.MessageRepository;
import org.mariqzw.grpc.*;
import org.mariqzw.grpc.ChatServiceGrpc;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ChatServiceImpl(MessageRepository messageRepository, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createMessage(ChatMessageRequest request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSender(request.getSender());
            messageDTO.setReceiver(request.getReceiver());
            messageDTO.setMessageText(request.getMessageText());
            messageDTO.setTimestamp(LocalDateTime.now().toString());

            MessageEntity messageEntity = modelMapper.map(messageDTO, MessageEntity.class);

            MessageEntity savedEntity = messageRepository.save(messageEntity);

            MessageDTO savedDTO = modelMapper.map(savedEntity, MessageDTO.class);

            ChatMessageResponse response = ChatMessageResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Message created successfully.")
                    .setId(savedDTO.getId())
                    .setSender(savedDTO.getSender())
                    .setReceiver(savedDTO.getReceiver())
                    .setMessageText(savedDTO.getMessageText())
                    .setTimestamp(savedDTO.getTimestamp())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (DataAccessException e) {
            throw new DatabaseUnavailableException("Database is currently unavailable", e);
        } catch (Exception e) {
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
        try {
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
                throw new MessageNotFoundException("Message not found");
            }
        } catch (MessageNotFoundException e) {
            responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage())));
        } catch (DataAccessException e) {
            responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("Database is currently unavailable")));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Internal server error")));
        }
    }

    @Override
    public void getAllMessages(ChatMessagesListRequest request, StreamObserver<ChatMessagesListResponse> responseObserver) {
        try {
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
        } catch (DataAccessException e) {
            responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("Database is currently unavailable")));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Internal server error")));
        }
    }

    @Override
    public void updateMessage(ChatMessage request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            Optional<MessageEntity> existingMessageOpt = messageRepository.findById(request.getId());

            if (existingMessageOpt.isPresent()) {
                MessageEntity existingMessage = existingMessageOpt.get();

                MessageDTO updatedMessageDTO = new MessageDTO();
                updatedMessageDTO.setId(request.getId());
                updatedMessageDTO.setSender(request.getSender());
                updatedMessageDTO.setReceiver(request.getReceiver());
                updatedMessageDTO.setMessageText(request.getMessageText());
                updatedMessageDTO.setTimestamp(LocalDateTime.now().toString());

                modelMapper.map(updatedMessageDTO, existingMessage);

                MessageEntity updatedEntity = messageRepository.save(existingMessage);

                ChatMessageResponse response = ChatMessageResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Message updated successfully.")
                        .setId(updatedEntity.getId())
                        .setSender(updatedEntity.getSender())
                        .setReceiver(updatedEntity.getReceiver())
                        .setMessageText(updatedEntity.getMessageText())
                        .setTimestamp(updatedEntity.getTimestamp())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Message not found")));
            }
        } catch (DataAccessException e) {
            responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("Database is currently unavailable")));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Internal server error")));
        }
    }

    @Override
    public void deleteMessage(ChatMessageByIdRequest request, StreamObserver<ChatMessageResponse> responseObserver) {
        try {
            Optional<MessageEntity> message = messageRepository.findById(request.getId());
            if (message.isPresent()) {
                messageRepository.deleteById(request.getId());
                MessageDTO messageDTO = modelMapper.map(message.get(), MessageDTO.class);

                ChatMessageResponse response = ChatMessageResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Message deleted successfully")
                        .setId(messageDTO.getId())
                        .setSender(messageDTO.getSender())
                        .setReceiver(messageDTO.getReceiver())
                        .setMessageText(messageDTO.getMessageText())
                        .setTimestamp(messageDTO.getTimestamp())
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Message not found")));
            }
        } catch (DataAccessException e) {
            responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("Database is currently unavailable")));
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription("Internal server error")));
        }
    }
}
