package com.financeiro.infrastructure.repositories.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.financeiro.application.repositories.TransacaoRepository;
import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.infrastructure.repositories.JpaTransacaoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementação do repositório de Transacao.
 * Adapta as operações JPA para as interfaces da camada de aplicação.
 */
@Component
@RequiredArgsConstructor
public class TransacaoRepositoryImpl implements TransacaoRepository {

    private final JpaTransacaoRepository jpaTransacaoRepository;

    @Override
    public Optional<Transacao> findById(UUID id) {
        return jpaTransacaoRepository.findById(id);
    }

    @Override
    public List<Transacao> findByUsuario(Usuario usuario) {
        return jpaTransacaoRepository.findByUsuario(usuario);
    }

    @Override
    public List<Transacao> findByUsuarioAndDataTransacaoBetween(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        return jpaTransacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);
    }

    @Override
    public List<Transacao> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria) {
        return jpaTransacaoRepository.findByUsuarioAndCategoria(usuario, categoria);
    }

    @Override
    public List<Transacao> findByUsuarioAndTipo(Usuario usuario, Transacao.TipoTransacao tipo) {
        return jpaTransacaoRepository.findByUsuarioAndTipo(usuario, tipo);
    }

    @Override
    public BigDecimal calculateTotalReceitasByUsuarioAndPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        return jpaTransacaoRepository.calculateTotalReceitasByUsuarioAndPeriodo(usuario, dataInicio, dataFim);
    }

    @Override
    public BigDecimal calculateTotalDespesasByUsuarioAndPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        return jpaTransacaoRepository.calculateTotalDespesasByUsuarioAndPeriodo(usuario, dataInicio, dataFim);
    }

    @Override
    public List<Transacao> findByUsuarioOrderByDataTransacaoDesc(Usuario usuario) {
        return jpaTransacaoRepository.findByUsuarioOrderByDataTransacaoDesc(usuario);
    }

    @Override
    public Transacao save(Transacao transacao) {
        return jpaTransacaoRepository.save(transacao);
    }

    @Override
    public void deleteById(UUID id) {
        jpaTransacaoRepository.deleteById(id);
    }

    @Override
    public List<Transacao> findByUsuarioAndDescricaoContainingIgnoreCase(Usuario usuario, String descricao) {
        return jpaTransacaoRepository.findByUsuarioAndDescricaoContainingIgnoreCase(usuario, descricao);
    }
}