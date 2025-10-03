package com.financeiro.presentation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload para autenticação de usuários.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email(message = "E-mail deve ser válido")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
