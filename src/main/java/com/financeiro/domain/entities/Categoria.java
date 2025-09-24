package com.financeiro.domain.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade de domínio para representar uma categoria financeira.
 * Categorias são usadas para classificar receitas e despesas.
 */
@Entity
@Table(name = "categorias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Column(name = "descricao", length = 200)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCategoria tipo;

    @Column(name = "ativa", nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public enum TipoCategoria {
        RECEITA("Receita"),
        DESPESA("Despesa");

        private final String descricao;

        TipoCategoria(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Regra de negócio: Desativa a categoria
     */
    public void desativar() {
        this.ativa = false;
        this.dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Regra de negócio: Ativa a categoria
     */
    public void ativar() {
        this.ativa = true;
        this.dataAtualizacao = LocalDateTime.now();
    }
}