package com.financeiro.application.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;

/**
 * Interface de repositório para a entidade Transacao.
 * Define operações de acesso a dados seguindo os princípios da Clean Architecture.
 */
public interface TransacaoRepository {

    /**
     * Busca uma transação por ID
     */
    Optional<Transacao> findById(UUID id);

    /**
     * Busca todas as transações de um usuário
     */
    List<Transacao> findByUsuario(Usuario usuario);

    /**
     * Busca transações de um usuário por período
     */
    List<Transacao> findByUsuarioAndDataTransacaoBetween(Usuario usuario, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca transações de um usuário por categoria
     */
    List<Transacao> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);

    /**
     * Busca transações de um usuário por tipo
     */
    List<Transacao> findByUsuarioAndTipo(Usuario usuario, Transacao.TipoTransacao tipo);

    /**
     * Calcula o total de receitas de um usuário em um período
     */
    BigDecimal calculateTotalReceitasByUsuarioAndPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Calcula o total de despesas de um usuário em um período
     */
    BigDecimal calculateTotalDespesasByUsuarioAndPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca transações de um usuário ordenadas por data (mais recentes primeiro)
     */
    List<Transacao> findByUsuarioOrderByDataTransacaoDesc(Usuario usuario);

    /**
     * Salva uma transação
     */
    Transacao save(Transacao transacao);

    /**
     * Remove uma transação por ID
     */
    void deleteById(UUID id);

    /**
     * Busca transações por descrição contendo texto
     */
    List<Transacao> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao);
}