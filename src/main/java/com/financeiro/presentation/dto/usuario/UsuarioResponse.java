package com.financeiro.presentation.dto.usuario;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financeiro.domain.entities.Usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para usuário (sem dados sensíveis)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private UUID id;
    private String nome;
    private String email;
    private Usuario.Papel papel;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime ultimoAcesso;
    private String foto; // URL da foto do perfil

    /**
     * Converte entidade para DTO de resposta (sem senha)
     */
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .papel(usuario.getPapel())
                .ativo(usuario.getAtivo())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .ultimoAcesso(usuario.getUltimoAcesso())
                .foto(usuario.getFoto())
                .build();
    }
}