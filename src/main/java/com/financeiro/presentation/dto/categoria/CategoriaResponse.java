package com.financeiro.presentation.dto.categoria;

import java.time.LocalDateTime;
import java.util.UUID;

import com.financeiro.domain.entities.Categoria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para categoria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {

    private UUID id;
    private String nome;
    private String descricao;
    private Categoria.TipoCategoria tipo;
    private Boolean ativa;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private UUID usuarioId;
    private String cor;

    /**
     * Converte entidade para DTO de resposta
     */
    public static CategoriaResponse fromEntity(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .tipo(categoria.getTipo())
                .ativa(categoria.getAtiva())
                .dataCriacao(categoria.getDataCriacao())
                .dataAtualizacao(categoria.getDataAtualizacao())
                .usuarioId(categoria.getUsuario() != null ? categoria.getUsuario().getId() : null)
                .cor(categoria.getCor())
                .build();
    }
}