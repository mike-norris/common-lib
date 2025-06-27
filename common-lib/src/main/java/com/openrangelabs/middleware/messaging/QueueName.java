package com.openrangelabs.middleware.messaging;

/**
 * Enumeration of all message queue names used in the system
 */
public enum QueueName {
    // User logging queues
    USER_LOGS("q.logs-user", "Queue for user activity logs"),
    USER_LOGS_DLQ("q.logs-user-dlq", "Dead letter queue for user logs"),

    // System logging queues
    SYSTEM_LOGS("q.logs-system", "Queue for system logs"),
    SYSTEM_LOGS_DLQ("q.logs-system-dlq", "Dead letter queue for system logs"),

    // Portal user queues
    PORTAL_USER("q.portal-user", "Queue for portal user operations"),
    PORTAL_USER_DLQ("q.portal-user-dlq", "Dead letter queue for portal user operations");

    private final String queueName;
    private final String description;

    QueueName(String queueName, String description) {
        this.queueName = queueName;
        this.description = description;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get QueueName from string representation
     * @param queueName the string representation of the queue name
     * @return the corresponding QueueName enum value
     * @throws IllegalArgumentException if the queue name is invalid
     */
    public static QueueName fromQueueName(String queueName) {
        if (queueName == null) {
            throw new IllegalArgumentException("Queue name cannot be null");
        }

        for (QueueName queue : QueueName.values()) {
            if (queue.queueName.equals(queueName)) {
                return queue;
            }
        }

        throw new IllegalArgumentException("Invalid queue name: " + queueName);
    }

    /**
     * Check if a string is a valid queue name
     * @param queueName the queue name to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidQueueName(String queueName) {
        if (queueName == null) {
            return false;
        }

        for (QueueName queue : QueueName.values()) {
            if (queue.queueName.equals(queueName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the corresponding dead letter queue for a main queue
     * @return the dead letter queue if this is a main queue, null otherwise
     */
    public QueueName getDeadLetterQueue() {
        switch (this) {
            case USER_LOGS:
                return USER_LOGS_DLQ;
            case SYSTEM_LOGS:
                return SYSTEM_LOGS_DLQ;
            case PORTAL_USER:
                return PORTAL_USER_DLQ;
            default:
                return null; // This is already a DLQ
        }
    }

    /**
     * Check if this queue is a dead letter queue
     * @return true if this is a DLQ, false otherwise
     */
    public boolean isDeadLetterQueue() {
        return queueName.endsWith("-dlq");
    }

    @Override
    public String toString() {
        return queueName;
    }
}