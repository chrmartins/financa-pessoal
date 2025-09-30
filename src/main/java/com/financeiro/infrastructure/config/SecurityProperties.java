package com.financeiro.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    
    private Jwt jwt = new Jwt();
    
    @Data
    public static class Jwt {
        private String secret = "default-secret-key-for-development-only";
        private long expirationTime = 86400000; // 24 hours in milliseconds
    }
}