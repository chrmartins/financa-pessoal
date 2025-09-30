package com.financeiro.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private Cors cors = new Cors();
    private Swagger swagger = new Swagger();
    
    @Data
    public static class Cors {
        private String allowedOrigins = "http://localhost:3000,http://localhost:5173";
    }
    
    @Data
    public static class Swagger {
        private boolean enabled = true;
    }
}