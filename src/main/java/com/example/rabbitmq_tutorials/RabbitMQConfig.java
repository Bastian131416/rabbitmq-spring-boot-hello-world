package com.example.rabbitmq_tutorials;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String HELLO_QUEUE = "hello";

    @Bean
    public Queue helloQueue() {
        return new Queue(HELLO_QUEUE, false);
    }
}