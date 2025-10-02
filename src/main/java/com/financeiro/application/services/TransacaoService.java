package com.financeiro.application.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.transacao.CreateTransacaoRequest;
import com.financeiro.presentation.dto.transacao.ResumoFinanceiroResponse;
import com.financeiro.presentation.dto.transacao.TransacaoResponse;
import com.financeiro.presentation.dto.transacao.UpdateTransacaoRequest;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

@Service
@Transactional
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, 
                           CategoriaRepository categoriaRepository,
                           UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public TransacaoResponse criarTransacao(CreateTransacaoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        Usuario usuario = usuarioRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Transacao transacao = new Transacao();
        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setDataTransacao(request.getDataTransacao());
        transacao.setTipo(request.getTipo());
        transacao.setObservacoes(request.getObservacoes());
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);

        Transacao salva = transacaoRepository.save(transacao);
        return TransacaoResponse.fromEntity(salva);
    }

    public TransacaoResponse criarTransacao(CreateTransacaoRequest request, UUID usuarioId) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Transacao transacao = new Transacao();
        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setDataTransacao(request.getDataTransacao());
        transacao.setTipo(request.getTipo());
        transacao.setObservacoes(request.getObservacoes());
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);

        Transacao salva = transacaoRepository.save(transacao);
        return TransacaoResponse.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> listarTransacoes(LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes;
        
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findAll();
        }
        
        return transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> listarTransacoesPorUsuario(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes;
        
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByUsuarioIdAndDataTransacaoBetween(usuarioId, dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findByUsuarioId(usuarioId);
        }
        
        return transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransacaoResponse buscarPorId(UUID id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        return TransacaoResponse.fromEntity(transacao);
    }

    public TransacaoResponse atualizarTransacao(UUID id, UpdateTransacaoRequest request) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        if (request.getDescricao() != null) {
            transacao.setDescricao(request.getDescricao());
        }
        if (request.getValor() != null) {
            transacao.setValor(request.getValor());
        }
        if (request.getDataTransacao() != null) {
            transacao.setDataTransacao(request.getDataTransacao());
        }
        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            transacao.setCategoria(categoria);
        }
        if (request.getObservacoes() != null) {
            transacao.setObservacoes(request.getObservacoes());
        }

        Transacao atualizada = transacaoRepository.save(transacao);
        return TransacaoResponse.fromEntity(atualizada);
    }

    public void deletarTransacao(UUID id) {
        if (!transacaoRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada");
        }
        transacaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo() {
        List<Transacao> transacoes = transacaoRepository.findAll();
        
        return transacoes.stream()
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoPorUsuario(UUID usuarioId) {
        BigDecimal saldo = transacaoRepository.calcularSaldoPorUsuario(usuarioId);
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse obterResumoFinanceiro(LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes;
        
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findAll();
        }

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getValor().compareTo(BigDecimal.ZERO) > 0)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getValor().compareTo(BigDecimal.ZERO) < 0)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.add(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas.abs(), saldo);
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse obterResumoFinanceiroPorUsuario(UUID usuarioId, LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes;
        
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByUsuarioIdAndDataTransacaoBetween(usuarioId, dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findByUsuarioId(usuarioId);
        }

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getValor().compareTo(BigDecimal.ZERO) > 0)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getValor().compareTo(BigDecimal.ZERO) < 0)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.add(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas.abs(), saldo);
    }
}