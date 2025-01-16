package org.mariqzw.gateway.controllers;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mariqzw.gateway.models.dtos.MessageDTO;
import org.mariqzw.grpc.ChatProto;
import org.mariqzw.grpc.ChatServiceGrpc;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/messages")
public class ChatsController {

    private final ChatServiceGrpc.ChatServiceBlockingStub stub;

    public ChatsController() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("domain-service", 8080)
                .usePlaintext()
                .build();

        this.stub = ChatServiceGrpc.newBlockingStub(channel);
    }

    @PostMapping
    @CacheEvict(value = {"messagesList", "messages"}, allEntries = true)
    public String createMessage(@RequestBody MessageDTO chatMessageRequest) {
        ChatProto.ChatMessageRequest request =
                ChatProto.ChatMessageRequest.newBuilder()
                        .setSender(chatMessageRequest.getSender())
                        .setReceiver(chatMessageRequest.getReceiver())
                        .setMessageText(chatMessageRequest.getMessageText())
                        .build();

        ChatProto.ChatMessageResponse response = stub.createMessage(request);
        return response.getMessage();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "messages", key = "#id", unless = "#result == null")
    public MessageDTO getMessageById(@PathVariable Long id) {
        ChatProto.ChatMessageByIdRequest request =
                ChatProto.ChatMessageByIdRequest.newBuilder()
                        .setId(id)
                        .build();

        ChatProto.ChatMessageResponse response = stub.getMessageById(request);

        return new MessageDTO(
                response.getId(),
                response.getSender(),
                response.getReceiver(),
                response.getMessageText(),
                response.getTimestamp()
        );
    }

    @GetMapping
    @Cacheable(value = "messagesList", unless = "#result == null || #result.isEmpty()")
    public List<MessageDTO> getAllTransactions() {
        ChatProto.ChatMessagesListRequest request =
                ChatProto.ChatMessagesListRequest.newBuilder()
                        .build();

        ChatProto.ChatMessagesListResponse response = stub.getAllMessages(request);

        List<MessageDTO> transactions = new ArrayList<>();
        for (ChatProto.ChatMessage tr : response.getMessagesList()) {
            transactions.add(new MessageDTO(
                    tr.getId(),
                    tr.getSender(),
                    tr.getReceiver(),
                    tr.getMessageText(),
                    tr.getTimestamp()
            ));
        }
        return transactions;
    }

    @PutMapping("/{id}")
    @CacheEvict(value = {"messages", "messagesList"}, key = "#id", allEntries = true)
    public String updateTransaction(@PathVariable Long id, @RequestBody MessageDTO chatMessageRequest) {
        ChatProto.ChatMessage request =
                ChatProto.ChatMessage.newBuilder()
                        .setId(id)
                        .setSender(chatMessageRequest.getSender())
                        .setReceiver(chatMessageRequest.getReceiver())
                        .setMessageText(chatMessageRequest.getMessageText())
                        .build();

        ChatProto.ChatMessageResponse response = stub.updateMessage(request);
        return response.getMessage();
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = {"messagesList", "messages"}, key = "#id", allEntries = true)
    public String deleteTransaction(@PathVariable Long id) {
        ChatProto.ChatMessageByIdRequest request =
                ChatProto.ChatMessageByIdRequest.newBuilder()
                        .setId(id)
                        .build();

        ChatProto.ChatMessageResponse response = stub.deleteMessage(request);
        return response.getMessage();
    }
}
