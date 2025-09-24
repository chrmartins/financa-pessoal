package com.financeiro.infrastructure.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;

/**
 * Repositório JPA para a entidade Transacao.
 * Extends JpaRepository para operações básicas de CRUD.
 */
@Repository
public interface JpaTransacaoRepository extends JpaRepository<Transacao, UUID> {

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
     * Busca transações de um usuário ordenadas por data (mais recentes primeiro)
     */
    List<Transacao> findByUsuarioOrderByDataTransacaoDesc(Usuario usuario);

    /**
     * Busca transações por descrição contendo texto
     */
    List<Transacao> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao);

    /**
     * Calcula o total de receitas de um usuário em um período
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
           "WHERE t.usuario = :usuario AND t.tipo = 'RECEITA' " +
           "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim")
    BigDecimal calculateTotalReceitasByUsuarioAndPeriodo(
            @Param("usuario") Usuario usuario, 
            @Param("dataInicio") LocalDate dataInicio, 
            @Param("dataFim") LocalDate dataFim);

    /**
     * Calcula o total de despesas de um usuário em um período
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
           "WHERE t.usuario = :usuario AND t.tipo = 'DESPESA' " +
           "AND t.dataTransacao BETWEEN :dataInicio AND :dataFim")
    BigDecimal calculateTotalDespesasByUsuarioAndPeriodo(
            @Param("usuario") Usuario usuario, 
            @Param("dataInicio") LocalDate dataInicio, 
            @Param("dataFim") LocalDate dataFim);

    /**
     * Busca transações de um usuário em um mês específico
     */
    @Query("SELECT t FROM Transacao t " +
           "WHERE t.usuario = :usuario " +
           "AND YEAR(t.dataTransacao) = :ano " +
           "AND MONTH(t.dataTransacao) = :mes " +
           "ORDER BY t.dataTransacao DESC")
    List<Transacao> findByUsuarioAndMesAno(
            @Param("usuario") Usuario usuario, 
            @Param("ano") int ano, 
            @Param("mes") int mes);

    /**
     * Calcula o saldo total de um usuário
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE -t.valor END), 0) " +
           "FROM Transacao t WHERE t.usuario = :usuario")
    BigDecimal calculateSaldoByUsuario(@Param("usuario") Usuario usuario);

    /**
     * Busca as últimas N transações de um usuário
     */
    List<Transacao> findTop10ByUsuarioOrderByDataTransacaoDescDataCriacaoDesc(Usuario usuario);

    /**
     * Conta transações de um usuário por tipo
     */
    long countByUsuarioAndTipo(Usuario usuario, Transacao.TipoTransacao tipo);
}