package com.openrangelabs.middleware.messaging;

// NOTE: This configuration is OPTIONAL - only include if using RabbitMQ
// Requires: implementation 'org.springframework.boot:spring-boot-starter-amqp'

// /* UNCOMMENT THIS ENTIRE FILE IF USING RABBITMQ

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    // TTL for messages in milliseconds (30 seconds)
    private static final int MESSAGE_TTL = 30000;

    // Maximum retry attempts
    private static final int MAX_RETRIES = 3;

    // Exchanges
    @Bean
    public DirectExchange loggingExchange() {
        return new DirectExchange(ExchangeName.LOGGING.getExchangeName());
    }

    @Bean
    public DirectExchange loggingDlxExchange() {
        return new DirectExchange(ExchangeName.LOGGING_DLX.getExchangeName());
    }

    @Bean
    public DirectExchange createUserExchange() {
        return new DirectExchange(ExchangeName.CREATE_USER.getExchangeName());
    }

    @Bean
    public DirectExchange createUserDlxExchange() {
        return new DirectExchange(ExchangeName.CREATE_USER_DLX.getExchangeName());
    }

    // User Logs Queues
    @Bean
    public Queue userLogsQueue() {
        return QueueBuilder.durable(QueueName.USER_LOGS.getQueueName())
                .withArgument("x-dead-letter-exchange", ExchangeName.LOGGING_DLX.getExchangeName())
                .withArgument("x-dead-letter-routing-key", QueueName.USER_LOGS_DLQ.getQueueName())
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }

    @Bean
    public Queue userLogsDlqQueue() {
        return QueueBuilder.durable(QueueName.USER_LOGS_DLQ.getQueueName())
                .withArgument("x-max-retries", MAX_RETRIES)
                .build();
    }

    // System Logs Queues
    @Bean
    public Queue systemLogsQueue() {
        return QueueBuilder.durable(QueueName.SYSTEM_LOGS.getQueueName())
                .withArgument("x-dead-letter-exchange", ExchangeName.LOGGING_DLX.getExchangeName())
                .withArgument("x-dead-letter-routing-key", QueueName.SYSTEM_LOGS_DLQ.getQueueName())
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }

    @Bean
    public Queue systemLogsDlqQueue() {
        return QueueBuilder.durable(QueueName.SYSTEM_LOGS_DLQ.getQueueName())
                .withArgument("x-max-retries", MAX_RETRIES)
                .build();
    }

    // Portal User Queues
    @Bean
    public Queue portalUserQueue() {
        return QueueBuilder.durable(QueueName.PORTAL_USER.getQueueName())
                .withArgument("x-dead-letter-exchange", ExchangeName.CREATE_USER_DLX.getExchangeName())
                .withArgument("x-dead-letter-routing-key", QueueName.PORTAL_USER_DLQ.getQueueName())
                .withArgument("x-message-ttl", MESSAGE_TTL)
                .build();
    }

    @Bean
    public Queue portalUserDlqQueue() {
        return QueueBuilder.durable(QueueName.PORTAL_USER_DLQ.getQueueName())
                .withArgument("x-max-retries", MAX_RETRIES)
                .build();
    }

    // Bindings
    @Bean
    public Binding userLogsBinding() {
        return BindingBuilder
                .bind(userLogsQueue())
                .to(loggingExchange())
                .with(QueueName.USER_LOGS.getQueueName());
    }

    @Bean
    public Binding userLogsDlqBinding() {
        return BindingBuilder
                .bind(userLogsDlqQueue())
                .to(loggingDlxExchange())
                .with(QueueName.USER_LOGS_DLQ.getQueueName());
    }

    @Bean
    public Binding systemLogsBinding() {
        return BindingBuilder
                .bind(systemLogsQueue())
                .to(loggingExchange())
                .with(QueueName.SYSTEM_LOGS.getQueueName());
    }

    @Bean
    public Binding systemLogsDlqBinding() {
        return BindingBuilder
                .bind(systemLogsDlqQueue())
                .to(loggingDlxExchange())
                .with(QueueName.SYSTEM_LOGS_DLQ.getQueueName());
    }

    @Bean
    public Binding portalUserBinding() {
        return BindingBuilder
                .bind(portalUserQueue())
                .to(createUserExchange())
                .with(QueueName.PORTAL_USER.getQueueName());
    }

    @Bean
    public Binding portalUserDlqBinding() {
        return BindingBuilder
                .bind(portalUserDlqQueue())
                .to(createUserDlxExchange())
                .with(QueueName.PORTAL_USER_DLQ.getQueueName());
    }
}

// */

// Placeholder class when RabbitMQ is not used
/*
public class MessagingConfig {
    // This class is intentionally empty when RabbitMQ is not configured
    // The Queue and Exchange enums can still be used for constants
}
*/