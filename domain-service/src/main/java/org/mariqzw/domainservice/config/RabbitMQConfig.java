package org.mariqzw.domainservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.create.routingkey}")
    private String createRoutingKey;

    @Value("${spring.rabbitmq.update.routingkey}")
    private String updateRoutingKey;

    @Value("${spring.rabbitmq.delete.routingkey}")
    private String deleteRoutingKey;

    @Value("${spring.rabbitmq.create.queue}")
    private String createQueueName;

    @Value("${spring.rabbitmq.update.queue}")
    private String updateQueueName;

    @Value("${spring.rabbitmq.delete.queue}")
    private String deleteQueueName;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue createQueue() {
        return new Queue(createQueueName, true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue(updateQueueName, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(deleteQueueName, true);
    }

    @Bean
    public Binding createBinding(Queue createQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createQueue).to(exchange).with(createRoutingKey);
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, DirectExchange exchange) {
        return BindingBuilder.bind(updateQueue).to(exchange).with(updateRoutingKey);
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deleteQueue).to(exchange).with(deleteRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
