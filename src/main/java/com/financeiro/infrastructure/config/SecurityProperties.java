package com.financeiro.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    
    private Jwt jwt = new Jwt();
    
    @Data
    public static class Jwt {
        // Chave secreta para assinar os tokens JWT (deve ser base64 encoded e ter pelo menos 256 bits)
        private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        
        // Tempo de expiração do token de acesso (24 horas em millisegundos)
        private long expirationTime = 86400000; // 24 hours
        
        // Tempo de expiração do refresh token (7 dias em millisegundos)
        private long refreshExpirationTime = 604800000; // 7 days
    }
}