package com.openrangelabs.middleware;

import com.openrangelabs.middleware.messaging.ExchangeName;
import com.openrangelabs.middleware.messaging.QueueName;

public class ORLCommon {

    // Original constants - kept for backward compatibility
    public static final String LOGGING_EXCHANGE = "x.logging";
    public static final String LOGGING_DLX_EXCHANGE = "x.logging-dlx";
    public static final String USER_LOGS_QUEUE = "q.logs-user";
    public static final String USER_LOGS_DLQ_QUEUE = "q.logs-user-dlq";
    public static final String SYSTEM_LOGS_QUEUE = "q.logs-system";
    public static final String SYSTEM_LOGS_DLQ_QUEUE = "q.logs-system-dlq";

    public static final String CREATE_USER_DLX_EXCHANGE = "x.create-user-dlx";
    public static final String CREATE_USER_EXCHANGE = "x.create-user";
    public static final String PORTAL_USER_QUEUE = "q.portal-user";
    public static final String PORTAL_USER_DLQ_QUEUE = "q.portal-user-dlq";

    public ORLCommon() {
    }

    /**
     * Get the ExchangeName enum for a given exchange string
     * @param exchange the exchange name string
     * @return the corresponding ExchangeName enum
     */
    public static ExchangeName getExchange(String exchange) {
        return ExchangeName.fromExchangeName(exchange);
    }

    /**
     * Get the QueueName enum for a given queue string
     * @param queue the queue name string
     * @return the corresponding QueueName enum
     */
    public static QueueName getQueue(String queue) {
        return QueueName.fromQueueName(queue);
    }

    /**
     * Check if an exchange is a dead letter exchange
     * @param exchange the exchange name
     * @return true if it's a DLX
     */
    public static boolean isDeadLetterExchange(String exchange) {
        try {
            return ExchangeName.fromExchangeName(exchange).isDeadLetterExchange();
        } catch (IllegalArgumentException e) {
            return exchange != null && exchange.endsWith("-dlx");
        }
    }

    /**
     * Check if a queue is a dead letter queue
     * @param queue the queue name
     * @return true if it's a DLQ
     */
    public static boolean isDeadLetterQueue(String queue) {
        try {
            return QueueName.fromQueueName(queue).isDeadLetterQueue();
        } catch (IllegalArgumentException e) {
            return queue != null && queue.endsWith("-dlq");
        }
    }
}