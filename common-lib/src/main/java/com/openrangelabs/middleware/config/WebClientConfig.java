package com.openrangelabs.middleware.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Simplified WebClient configuration - no separate properties class needed
 * Uses @Value annotations for direct property injection
 */
@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${domain:http://localhost}")
    private String domain;

    // Timeout settings
    @Value("${webclient.timeout.connect:10s}")
    private Duration connectTimeout;

    @Value("${webclient.timeout.read:30s}")
    private Duration readTimeout;

    @Value("${webclient.timeout.write:30s}")
    private Duration writeTimeout;

    // Connection pool settings
    @Value("${webclient.connection-pool.max-connections:100}")
    private int maxConnections;

    @Value("${webclient.connection-pool.keep-alive:5m}")
    private Duration keepAlive;

    @Value("${webclient.connection-pool.evict-idle-connections:1m}")
    private Duration evictIdleConnections;

    // Other settings
    @Value("${webclient.user-agent:OpenRangeLabs-Middleware/1.0}")
    private String userAgent;

    @Value("${webclient.enable-logging:false}")
    private boolean enableLogging;

    /**
     * Create a configured HttpClient with connection pooling and timeouts
     */
    private HttpClient createHttpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(maxConnections)
                .maxIdleTime(keepAlive)
                .maxLifeTime(evictIdleConnections)
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        return HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.toMillis())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout.toSeconds(), TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout.toSeconds(), TimeUnit.SECONDS)))
                .compress(true)
                .followRedirect(true);
    }

    /**
     * Create a WebClient builder with enhanced configuration
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = createHttpClient();

        WebClient.Builder builder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ORIGIN, domain)
                .defaultHeader(HttpHeaders.REFERER, domain)
                .defaultHeader("DNT", "1");

        // Add error handling
        builder.filter(errorHandlingFilter());

        // Add logging if enabled
        if (enableLogging) {
            builder.filter(loggingFilter());
        }

        return builder;
    }

    /**
     * Error handling filter for standardized error responses
     */
    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            String errorMessage = String.format(
                                    "HTTP %d: %s",
                                    clientResponse.statusCode().value(),
                                    errorBody
                            );

                            if (clientResponse.statusCode().is4xxClientError()) {
                                return reactor.core.publisher.Mono.error(
                                        new WebClientClientException(errorMessage, clientResponse.statusCode()));
                            } else {
                                return reactor.core.publisher.Mono.error(
                                        new WebClientServerException(errorMessage, clientResponse.statusCode()));
                            }
                        });
            }
            return reactor.core.publisher.Mono.just(clientResponse);
        });
    }

    /**
     * Logging filter for request/response logging
     */
    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (logger.isDebugEnabled()) {
                logger.debug("WebClient Request: {} {} {}",
                        clientRequest.method(),
                        clientRequest.url(),
                        clientRequest.headers());
            }
            return reactor.core.publisher.Mono.just(clientRequest);
        });
    }

    /**
     * Create a WebClient with base URL
     */
    public static WebClient build(String baseUrl) {
        return createDefaultBuilder().baseUrl(baseUrl).build();
    }

    /**
     * Create a WebClient with base URL
     */
    public static WebClient build(URL baseUrl) {
        return build(baseUrl.toString());
    }

    /**
     * Create a WebClient with custom configuration
     */
    public WebClient buildWithCustomization(String baseUrl,
                                            java.util.function.Consumer<WebClient.Builder> customizer) {
        WebClient.Builder builder = webClientBuilder().baseUrl(baseUrl);
        customizer.accept(builder);
        return builder.build();
    }

    /**
     * Create a default builder for static methods (backward compatibility)
     */
    private static WebClient.Builder createDefaultBuilder() {
        HttpClient defaultHttpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(defaultHttpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .defaultHeader("DNT", "1");
    }

    /**
     * Custom exception for 4xx client errors
     */
    public static class WebClientClientException extends RuntimeException {
        private final org.springframework.http.HttpStatusCode statusCode;

        public WebClientClientException(String message, org.springframework.http.HttpStatusCode statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public org.springframework.http.HttpStatusCode getStatusCode() {
            return statusCode;
        }
    }

    /**
     * Custom exception for 5xx server errors
     */
    public static class WebClientServerException extends RuntimeException {
        private final org.springframework.http.HttpStatusCode statusCode;

        public WebClientServerException(String message, org.springframework.http.HttpStatusCode statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public org.springframework.http.HttpStatusCode getStatusCode() {
            return statusCode;
        }
    }
}