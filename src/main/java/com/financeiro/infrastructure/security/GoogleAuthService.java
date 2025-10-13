package com.financeiro.infrastructure.security;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.financeiro.infrastructure.config.GoogleProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para autenticação com Google OAuth 2.0
 */
@Slf4j
@Service
public class GoogleAuthService {

    private final String clientId;

    public GoogleAuthService(GoogleProperties googleProperties) {
        this.clientId = googleProperties.getClient().getId();
    }

    /**
     * Verifica e valida o token JWT do Google
     * 
     * @param idTokenString Token JWT recebido do frontend (do Google Sign-In)
     * @return Payload do token contendo informações do usuário
     * @throws Exception se o token for inválido
     */
    public GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        log.info("Verificando token do Google...");
        
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        
        if (idToken != null) {
            log.info("Token do Google verificado com sucesso");
            return idToken.getPayload();
        } else {
            log.error("Token do Google inválido");
            throw new Exception("Token inválido");
        }
    }
}
