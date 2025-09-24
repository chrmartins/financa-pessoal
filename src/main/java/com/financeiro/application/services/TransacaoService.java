package com.financeiro.application.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.application.repositories.CategoriaRepository;
import com.financeiro.application.repositories.TransacaoRepository;
import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para gerenciamento de transações.
 * Implementa os casos de uso relacionados às transações financeiras.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Caso de uso: Criar nova transação
     */
    public Transacao criarTransacao(String descricao, BigDecimal valor, LocalDate dataTransacao, 
                                  Transacao.TipoTransacao tipo, UUID categoriaId, Usuario usuario, 
                                  String observacoes) {
        
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        // Validar se o tipo da transação é compatível com o tipo da categoria
        boolean categoriaReceita = categoria.getTipo() == Categoria.TipoCategoria.RECEITA;
        boolean transacaoReceita = tipo == Transacao.TipoTransacao.RECEITA;
        
        if (categoriaReceita != transacaoReceita) {
            throw new IllegalArgumentException("Tipo da transação deve ser compatível com o tipo da categoria");
        }

        Transacao transacao = Transacao.builder()
                .descricao(descricao)
                .valor(valor)
                .dataTransacao(dataTransacao)
                .tipo(tipo)
                .categoria(categoria)
                .usuario(usuario)
                .observacoes(observacoes)
                .build();

        return transacaoRepository.save(transacao);
    }

    /**
     * Caso de uso: Buscar transação por ID
     */
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarPorId(UUID id) {
        return transacaoRepository.findById(id);
    }

    /**
     * Caso de uso: Listar transações de um usuário
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarTransacoesDoUsuario(Usuario usuario) {
        return transacaoRepository.findByUsuarioOrderByDataTransacaoDesc(usuario);
    }

    /**
     * Caso de uso: Listar transações de um usuário por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarTransacoesPorPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        return transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);
    }

    /**
     * Caso de uso: Listar transações por categoria
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarTransacoesPorCategoria(Usuario usuario, UUID categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        return transacaoRepository.findByUsuarioAndCategoria(usuario, categoria);
    }

    /**
     * Caso de uso: Listar transações por tipo
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarTransacoesPorTipo(Usuario usuario, Transacao.TipoTransacao tipo) {
        return transacaoRepository.findByUsuarioAndTipo(usuario, tipo);
    }

    /**
     * Caso de uso: Calcular saldo de um usuário
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo(Usuario usuario) {
        List<Transacao> transacoes = transacaoRepository.findByUsuario(usuario);
        
        return transacoes.stream()
                .map(Transacao::getValorComSinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Caso de uso: Calcular saldo de um usuário por período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoPorPeriodo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes = transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);
        
        return transacoes.stream()
                .map(Transacao::getValorComSinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Caso de uso: Atualizar transação
     */
    public Transacao atualizarTransacao(UUID id, String descricao, BigDecimal valor, 
                                      LocalDate dataTransacao, UUID categoriaId, String observacoes) {
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));
        
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        // Validar compatibilidade de tipos
        boolean categoriaReceita = categoria.getTipo() == Categoria.TipoCategoria.RECEITA;
        boolean transacaoReceita = transacao.getTipo() == Transacao.TipoTransacao.RECEITA;
        
        if (categoriaReceita != transacaoReceita) {
            throw new IllegalArgumentException("Tipo da transação deve ser compatível com o tipo da categoria");
        }

        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setDataTransacao(dataTransacao);
        transacao.setCategoria(categoria);
        transacao.setObservacoes(observacoes);

        return transacaoRepository.save(transacao);
    }

    /**
     * Caso de uso: Remover transação
     */
    public void removerTransacao(UUID id) {
        if (!transacaoRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Transação não encontrada");
        }
        
        transacaoRepository.deleteById(id);
    }

    /**
     * Caso de uso: Buscar transações por descrição
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarPorDescricao(Usuario usuario, String descricao) {
        return transacaoRepository.findByUsuarioAndDescricaoContainingIgnoreCase(usuario, descricao);
    }
}