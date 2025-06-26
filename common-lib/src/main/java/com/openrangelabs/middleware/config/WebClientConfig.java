package com.openrangelabs.middleware.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;

import java.net.URL;

@Configuration
public class WebClientConfig {

    @Value("${domain}")
    String domain;

    private static final String charset = "utf-8";

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, charset)
                .defaultHeader(HttpHeaders.ORIGIN, domain)
                .defaultHeader(HttpHeaders.REFERER, domain)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
                .defaultHeader("DNT", "1"); //Something Bonita sometimes looks for but other apps ignore
    }

    public static WebClient build(String baseUrl) {
        WebClient.Builder builder = new WebClientConfig().webClientBuilder();
        return builder.baseUrl(baseUrl).build();
    }

    public static WebClient build(URL baseUrl) {
        return WebClientConfig.build(baseUrl.toString());
    }
}
