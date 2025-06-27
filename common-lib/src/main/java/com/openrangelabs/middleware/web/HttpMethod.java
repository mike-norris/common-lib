package com.openrangelabs.middleware.web;

/**
 * Enumeration of HTTP methods
 */
public enum HttpMethod {
    GET("GET", "Retrieve a resource"),
    POST("POST", "Create a new resource"),
    PUT("PUT", "Update an existing resource"),
    DELETE("DELETE", "Delete a resource"),
    PATCH("PATCH", "Partially update a resource"),
    HEAD("HEAD", "Retrieve headers only"),
    OPTIONS("OPTIONS", "Retrieve allowed methods"),
    TRACE("TRACE", "Perform a message loop-back test"),
    CONNECT("CONNECT", "Establish a tunnel");

    private final String method;
    private final String description;

    HttpMethod(String method, String description) {
        this.method = method;
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get HttpMethod from string representation
     * @param method the string representation of the HTTP method
     * @return the corresponding HttpMethod enum value
     * @throws IllegalArgumentException if the method is invalid
     */
    public static HttpMethod fromString(String method) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }

        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.method.equalsIgnoreCase(method.trim())) {
                return httpMethod;
            }
        }

        throw new IllegalArgumentException("Invalid HTTP method: " + method);
    }

    /**
     * Check if a string is a valid HTTP method
     * @param method the method to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidMethod(String method) {
        if (method == null) {
            return false;
        }

        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.method.equalsIgnoreCase(method.trim())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if this method is idempotent
     * @return true if the method is idempotent
     */
    public boolean isIdempotent() {
        return this == GET || this == PUT || this == DELETE ||
                this == HEAD || this == OPTIONS || this == TRACE;
    }

    /**
     * Check if this method typically has a request body
     * @return true if the method typically has a body
     */
    public boolean hasRequestBody() {
        return this == POST || this == PUT || this == PATCH;
    }

    @Override
    public String toString() {
        return method;
    }
}
