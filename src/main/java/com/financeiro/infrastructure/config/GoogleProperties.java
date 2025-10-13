package com.financeiro.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configurações do Google OAuth.
 */
@Configuration
@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleProperties {
    
    private Client client = new Client();
    
    @Getter
    @Setter
    public static class Client {
        private String id;
        private String secret;
    }
}
