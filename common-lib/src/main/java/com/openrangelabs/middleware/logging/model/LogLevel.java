package com.openrangelabs.middleware.logging.model;

/**
 * Enumeration of log levels following standard logging frameworks
 */
public enum LogLevel {
    TRACE("TRACE", 0),
    DEBUG("DEBUG", 1),
    INFO("INFO", 2),
    WARN("WARN", 3),
    ERROR("ERROR", 4),
    FATAL("FATAL", 5);

    private final String level;
    private final int severity;

    LogLevel(String level, int severity) {
        this.level = level;
        this.severity = severity;
    }

    public String getLevel() {
        return level;
    }

    public int getSeverity() {
        return severity;
    }

    /**
     * Check if this log level is enabled for the given minimum level
     * @param minimumLevel the minimum log level to check against
     * @return true if this level's severity is greater than or equal to the minimum level
     */
    public boolean isEnabledFor(LogLevel minimumLevel) {
        return this.severity >= minimumLevel.severity;
    }

    /**
     * Get LogLevel from string representation
     * @param level the string representation of the log level
     * @return the corresponding LogLevel enum value
     * @throws IllegalArgumentException if the level string is invalid
     */
    public static LogLevel fromString(String level) {
        if (level == null) {
            throw new IllegalArgumentException("Log level cannot be null");
        }

        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.level.equalsIgnoreCase(level.trim())) {
                return logLevel;
            }
        }

        throw new IllegalArgumentException("Invalid log level: " + level);
    }

    @Override
    public String toString() {
        return level;
    }
}