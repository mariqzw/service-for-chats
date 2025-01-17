package org.mariqzw.domainservice.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.mariqzw.domainservice.services.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcConfiguration implements CommandLineRunner {
    private final ChatServiceImpl chatService;

    @Autowired
    public GrpcConfiguration(ChatServiceImpl chatService) {
        this.chatService = chatService;
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(chatService)
                .build();

        server.start();
        System.out.println("Server started, listening on " + server.getPort());
        server.awaitTermination();
    }
}
