package com.financeiro.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade de domínio para representar uma transação financeira.
 * Uma transação pode ser uma receita ou despesa.
 */
@Entity
@Table(name = "transacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 2, max = 100, message = "Descrição deve ter entre 2 e 100 caracteres")
    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data da transação é obrigatória")
    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;

    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false, columnDefinition = "UUID")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, columnDefinition = "UUID")
    private Usuario usuario;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public enum TipoTransacao {
        RECEITA("Receita"),
        DESPESA("Despesa");

        private final String descricao;

        TipoTransacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Regra de negócio: Valida se o tipo da transação é compatível com o tipo da categoria
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    public void validarTipoCategoria() {
        if (categoria != null) {
            boolean categoriaReceita = categoria.getTipo() == Categoria.TipoCategoria.RECEITA;
            boolean transacaoReceita = this.tipo == TipoTransacao.RECEITA;
            
            if (categoriaReceita != transacaoReceita) {
                throw new IllegalStateException("Tipo da transação deve ser compatível com o tipo da categoria");
            }
        }
    }

    /**
     * Regra de negócio: Obter valor com sinal correto (negativo para despesas)
     */
    public BigDecimal getValorComSinal() {
        return tipo == TipoTransacao.DESPESA ? valor.negate() : valor;
    }
}