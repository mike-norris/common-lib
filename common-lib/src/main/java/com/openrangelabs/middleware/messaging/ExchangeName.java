package com.openrangelabs.middleware.messaging;

/**
 * Enumeration of all message exchange names used in the system
 */
public enum ExchangeName {
    // Logging exchanges
    LOGGING("x.logging", "Main exchange for logging messages"),
    LOGGING_DLX("x.logging-dlx", "Dead letter exchange for logging messages"),

    // User creation exchanges
    CREATE_USER("x.create-user", "Exchange for user creation events"),
    CREATE_USER_DLX("x.create-user-dlx", "Dead letter exchange for user creation events");

    private final String exchangeName;
    private final String description;

    ExchangeName(String exchangeName, String description) {
        this.exchangeName = exchangeName;
        this.description = description;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get ExchangeName from string representation
     * @param exchangeName the string representation of the exchange name
     * @return the corresponding ExchangeName enum value
     * @throws IllegalArgumentException if the exchange name is invalid
     */
    public static ExchangeName fromExchangeName(String exchangeName) {
        if (exchangeName == null) {
            throw new IllegalArgumentException("Exchange name cannot be null");
        }

        for (ExchangeName exchange : ExchangeName.values()) {
            if (exchange.exchangeName.equals(exchangeName)) {
                return exchange;
            }
        }

        throw new IllegalArgumentException("Invalid exchange name: " + exchangeName);
    }

    /**
     * Check if a string is a valid exchange name
     * @param exchangeName the exchange name to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidExchangeName(String exchangeName) {
        if (exchangeName == null) {
            return false;
        }

        for (ExchangeName exchange : ExchangeName.values()) {
            if (exchange.exchangeName.equals(exchangeName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the corresponding dead letter exchange for a main exchange
     * @return the dead letter exchange if this is a main exchange, null otherwise
     */
    public ExchangeName getDeadLetterExchange() {
        switch (this) {
            case LOGGING:
                return LOGGING_DLX;
            case CREATE_USER:
                return CREATE_USER_DLX;
            default:
                return null; // This is already a DLX
        }
    }

    /**
     * Check if this exchange is a dead letter exchange
     * @return true if this is a DLX, false otherwise
     */
    public boolean isDeadLetterExchange() {
        return exchangeName.endsWith("-dlx");
    }

    @Override
    public String toString() {
        return exchangeName;
    }
}