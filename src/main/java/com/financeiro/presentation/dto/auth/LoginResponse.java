package com.financeiro.presentation.dto.auth;

import com.financeiro.presentation.dto.usuario.UsuarioResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resposta para autenticação de usuários com JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UsuarioResponse usuario;
    private String token;
    private String refreshToken;
    private Long expiresIn;
}
