package com.openrangelabs.middleware.config;

/**
 * Enumeration of deployment environments
 */
public enum Environment {
    DEVELOPMENT("development", "dev", "Development environment"),
    TESTING("testing", "test", "Testing environment"),
    STAGING("staging", "stage", "Staging environment"),
    PRODUCTION("production", "prod", "Production environment");

    private final String name;
    private final String shortName;
    private final String description;

    Environment(String name, String shortName, String description) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get Environment from string representation
     * @param name the string representation of the environment
     * @return the corresponding Environment enum value
     * @throws IllegalArgumentException if the environment name is invalid
     */
    public static Environment fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Environment name cannot be null");
        }

        String trimmedName = name.trim().toLowerCase();

        for (Environment env : Environment.values()) {
            if (env.name.equals(trimmedName) || env.shortName.equals(trimmedName)) {
                return env;
            }
        }

        throw new IllegalArgumentException("Invalid environment: " + name);
    }

    /**
     * Check if a string is a valid environment name
     * @param name the environment name to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidEnvironment(String name) {
        if (name == null) {
            return false;
        }

        String trimmedName = name.trim().toLowerCase();

        for (Environment env : Environment.values()) {
            if (env.name.equals(trimmedName) || env.shortName.equals(trimmedName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if this is a production-like environment
     * @return true if production or staging
     */
    public boolean isProductionLike() {
        return this == PRODUCTION || this == STAGING;
    }

    /**
     * Check if this is a development-like environment
     * @return true if development or testing
     */
    public boolean isDevelopmentLike() {
        return this == DEVELOPMENT || this == TESTING;
    }

    @Override
    public String toString() {
        return name;
    }
}