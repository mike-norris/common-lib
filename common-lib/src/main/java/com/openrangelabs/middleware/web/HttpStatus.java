package com.openrangelabs.middleware.web;

/**
 * Enumeration of common HTTP status codes
 */
public enum HttpStatus {
    // 2xx Success
    OK(200, "OK", "Request succeeded"),
    CREATED(201, "Created", "Resource created successfully"),
    ACCEPTED(202, "Accepted", "Request accepted for processing"),
    NO_CONTENT(204, "No Content", "Request succeeded with no content"),

    // 3xx Redirection
    MOVED_PERMANENTLY(301, "Moved Permanently", "Resource moved permanently"),
    FOUND(302, "Found", "Resource found at different URI"),
    NOT_MODIFIED(304, "Not Modified", "Resource not modified"),

    // 4xx Client Error
    BAD_REQUEST(400, "Bad Request", "Invalid request"),
    UNAUTHORIZED(401, "Unauthorized", "Authentication required"),
    FORBIDDEN(403, "Forbidden", "Access denied"),
    NOT_FOUND(404, "Not Found", "Resource not found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "HTTP method not allowed"),
    CONFLICT(409, "Conflict", "Request conflicts with current state"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "Request validation failed"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "Rate limit exceeded"),

    // 5xx Server Error
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "Server error occurred"),
    NOT_IMPLEMENTED(501, "Not Implemented", "Feature not implemented"),
    BAD_GATEWAY(502, "Bad Gateway", "Invalid response from upstream server"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "Service temporarily unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "Upstream server timeout");

    private final int code;
    private final String reason;
    private final String description;

    HttpStatus(int code, String reason, String description) {
        this.code = code;
        this.reason = reason;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get HttpStatus from status code
     * @param code the HTTP status code
     * @return the corresponding HttpStatus enum value
     * @throws IllegalArgumentException if the status code is not found
     */
    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No HTTP status found for code: " + code);
    }

    /**
     * Check if a status code exists in this enum
     * @param code the status code to check
     * @return true if the code exists, false otherwise
     */
    public static boolean isValidCode(int code) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.code == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this is a success status (2xx)
     * @return true if success status
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * Check if this is a redirection status (3xx)
     * @return true if redirection status
     */
    public boolean isRedirection() {
        return code >= 300 && code < 400;
    }

    /**
     * Check if this is a client error status (4xx)
     * @return true if client error status
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * Check if this is a server error status (5xx)
     * @return true if server error status
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    /**
     * Check if this is an error status (4xx or 5xx)
     * @return true if error status
     */
    public boolean isError() {
        return isClientError() || isServerError();
    }

    /**
     * Get the status category (e.g., "2xx Success")
     * @return the status category
     */
    public String getCategory() {
        if (isSuccess()) return "2xx Success";
        if (isRedirection()) return "3xx Redirection";
        if (isClientError()) return "4xx Client Error";
        if (isServerError()) return "5xx Server Error";
        return "Unknown";
    }

    @Override
    public String toString() {
        return code + " " + reason;
    }
}