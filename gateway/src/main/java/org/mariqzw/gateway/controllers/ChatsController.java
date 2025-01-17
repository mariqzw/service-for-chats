package org.mariqzw.gateway.controllers;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mariqzw.gateway.models.dtos.MessageDTO;
import org.mariqzw.grpc.ChatProto;
import org.mariqzw.grpc.ChatServiceGrpc;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/messages")
public class ChatsController {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.create.routingkey}")
    private String createRoutingKey;

    @Value("${spring.rabbitmq.update.routingkey}")
    private String updateRoutingKey;

    @Value("${spring.rabbitmq.delete.routingkey}")
    private String deleteRoutingKey;

    private final ChatServiceGrpc.ChatServiceBlockingStub stub;

    public ChatsController(RabbitTemplate rabbitTemplate) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("domain-service", 9090)
                .usePlaintext()
                .build();

        this.stub = ChatServiceGrpc.newBlockingStub(channel);
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    @CacheEvict(value = {"messagesList", "messages"}, allEntries = true)
    public String createMessage(@RequestBody MessageDTO chatMessageRequest) {
        rabbitTemplate.convertAndSend(exchange, createRoutingKey, chatMessageRequest);

        return "Message creation request sent to RabbitMQ";
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
        chatMessageRequest.setId(id);
        rabbitTemplate.convertAndSend(exchange, updateRoutingKey, chatMessageRequest);

        return "Message update request sent to RabbitMQ";
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = {"messagesList", "messages"}, key = "#id", allEntries = true)
    public String deleteTransaction(@PathVariable Long id) {
        rabbitTemplate.convertAndSend(exchange, deleteRoutingKey, id);

        return "Message deletion request sent to RabbitMQ";
    }
}
