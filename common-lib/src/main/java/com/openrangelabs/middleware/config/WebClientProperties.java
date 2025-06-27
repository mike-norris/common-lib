package com.openrangelabs.middleware.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Configuration properties for WebClient
 */
@Component
@ConfigurationProperties(prefix = "webclient")
public class WebClientProperties {

    private Timeout timeout = new Timeout();
    private ConnectionPool connectionPool = new ConnectionPool();
    private String userAgent = "OpenRangeLabs-Middleware/1.0";
    private boolean enableLogging = false;

    public static class Timeout {
        private Duration connect = Duration.ofSeconds(10);
        private Duration read = Duration.ofSeconds(30);
        private Duration write = Duration.ofSeconds(30);

        public Duration getConnect() {
            return connect;
        }

        public void setConnect(Duration connect) {
            this.connect = connect;
        }

        public Duration getRead() {
            return read;
        }

        public void setRead(Duration read) {
            this.read = read;
        }

        public Duration getWrite() {
            return write;
        }

        public void setWrite(Duration write) {
            this.write = write;
        }
    }

    public static class ConnectionPool {
        private int maxConnections = 100;
        private int maxConnectionsPerRoute = 20;
        private Duration keepAlive = Duration.ofMinutes(3);
        private Duration evictIdleConnections = Duration.ofMinutes(1);

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getMaxConnectionsPerRoute() {
            return maxConnectionsPerRoute;
        }

        public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
            this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        }

        public Duration getKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(Duration keepAlive) {
            this.keepAlive = keepAlive;
        }

        public Duration getEvictIdleConnections() {
            return evictIdleConnections;
        }

        public void setEvictIdleConnections(Duration evictIdleConnections) {
            this.evictIdleConnections = evictIdleConnections;
        }
    }

    // Getters and Setters
    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
}