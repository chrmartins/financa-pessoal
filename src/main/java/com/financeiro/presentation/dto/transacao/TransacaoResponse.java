package com.financeiro.presentation.dto.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financeiro.domain.entities.Transacao;
import com.financeiro.presentation.dto.categoria.CategoriaResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para transação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoResponse {

    private UUID id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataTransacao;
    private Transacao.TipoTransacao tipo;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private CategoriaResponse categoria;

    /**
     * Converte entidade para DTO de resposta
     */
    public static TransacaoResponse fromEntity(Transacao transacao) {
        return TransacaoResponse.builder()
                .id(transacao.getId())
                .descricao(transacao.getDescricao())
                .valor(transacao.getValor())
                .dataTransacao(transacao.getDataTransacao())
                .tipo(transacao.getTipo())
                .observacoes(transacao.getObservacoes())
                .dataCriacao(transacao.getDataCriacao())
                .dataAtualizacao(transacao.getDataAtualizacao())
                .categoria(CategoriaResponse.fromEntity(transacao.getCategoria()))
                .build();
    }

    /**
     * Converte entidade para DTO de resposta simples (sem categoria detalhada)
     */
    public static TransacaoResponse fromEntitySimple(Transacao transacao) {
        return TransacaoResponse.builder()
                .id(transacao.getId())
                .descricao(transacao.getDescricao())
                .valor(transacao.getValor())
                .dataTransacao(transacao.getDataTransacao())
                .tipo(transacao.getTipo())
                .observacoes(transacao.getObservacoes())
                .dataCriacao(transacao.getDataCriacao())
                .dataAtualizacao(transacao.getDataAtualizacao())
                .categoria(CategoriaResponse.builder()
                        .id(transacao.getCategoria().getId())
                        .nome(transacao.getCategoria().getNome())
                        .tipo(transacao.getCategoria().getTipo())
                        .build())
                .build();
    }
}