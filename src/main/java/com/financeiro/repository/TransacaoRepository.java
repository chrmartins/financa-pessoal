package com.financeiro.repository;

import com.financeiro.domain.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    List<Transacao> findByDataTransacaoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = 'RECEITA'")
    BigDecimal calcularTotalReceitas();

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = 'DESPESA'")
    BigDecimal calcularTotalDespesas();

    @Query("SELECT (COALESCE(SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END), 0)) FROM Transacao t")
    BigDecimal calcularSaldo();
}
