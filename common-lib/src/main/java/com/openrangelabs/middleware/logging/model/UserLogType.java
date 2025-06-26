package com.openrangelabs.middleware.logging.model;

/**
 * Enumeration of user log types
 */
public enum UserLogType {
    LOGIN("LOGIN", "User login event"),
    LOGOUT("LOGOUT", "User logout event"),
    CREATE("CREATE", "Resource creation"),
    UPDATE("UPDATE", "Resource update"),
    DELETE("DELETE", "Resource deletion"),
    VIEW("VIEW", "Resource view/read"),
    DOWNLOAD("DOWNLOAD", "File download"),
    UPLOAD("UPLOAD", "File upload"),
    ERROR("ERROR", "Error event"),
    AUDIT("AUDIT", "Audit event");

    private final String code;
    private final String description;

    UserLogType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get UserLogType from string code
     * @param code the string representation of the log type
     * @return the corresponding UserLogType enum value
     * @throws IllegalArgumentException if the code is invalid
     */
    public static UserLogType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Log type code cannot be null");
        }

        for (UserLogType type : UserLogType.values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Invalid log type code: " + code);
    }

    /**
     * Check if a string is a valid log type code
     * @param code the code to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }

        for (UserLogType type : UserLogType.values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return code;
    }
}