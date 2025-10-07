package com.financeiro.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financeiro.domain.entities.Transacao;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    // Query para buscar todas as transações com relacionamentos carregados
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.usuario LEFT JOIN FETCH t.categoria")
    List<Transacao> findAllWithRelations();
    
    // Query para buscar uma transação por ID com relacionamentos carregados
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.usuario LEFT JOIN FETCH t.categoria WHERE t.id = :id")
    java.util.Optional<Transacao> findByIdWithRelations(UUID id);

    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.usuario LEFT JOIN FETCH t.categoria WHERE t.dataTransacao BETWEEN :inicio AND :fim")
    List<Transacao> findByDataTransacaoBetween(LocalDate inicio, LocalDate fim);
    
    // Métodos para filtrar por usuário com JOIN FETCH para evitar lazy loading
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.usuario LEFT JOIN FETCH t.categoria WHERE t.usuario.id = :usuarioId")
    List<Transacao> findByUsuarioId(UUID usuarioId);
    
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.usuario LEFT JOIN FETCH t.categoria WHERE t.usuario.id = :usuarioId AND t.dataTransacao BETWEEN :inicio AND :fim")
    List<Transacao> findByUsuarioIdAndDataTransacaoBetween(UUID usuarioId, LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = 'RECEITA'")
    BigDecimal calcularTotalReceitas();

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = 'DESPESA'")
    BigDecimal calcularTotalDespesas();

    @Query("SELECT (COALESCE(SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END), 0)) FROM Transacao t")
    BigDecimal calcularSaldo();
    
    // Métodos para cálculos por usuário
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.usuario.id = :usuarioId AND t.tipo = 'RECEITA'")
    BigDecimal calcularTotalReceitasPorUsuario(UUID usuarioId);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.usuario.id = :usuarioId AND t.tipo = 'DESPESA'")
    BigDecimal calcularTotalDespesasPorUsuario(UUID usuarioId);

    @Query("SELECT (COALESCE(SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END), 0)) FROM Transacao t WHERE t.usuario.id = :usuarioId")
    BigDecimal calcularSaldoPorUsuario(UUID usuarioId);
}
