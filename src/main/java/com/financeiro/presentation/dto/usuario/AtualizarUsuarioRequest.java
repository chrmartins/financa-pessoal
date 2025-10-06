package com.financeiro.presentation.dto.usuario;

import com.financeiro.domain.entities.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de atualização de usuário
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarUsuarioRequest {

    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Email(message = "Email deve ter formato válido")
    private String email;

    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha;

    private Usuario.Papel papel;

    private Boolean ativo;
}
