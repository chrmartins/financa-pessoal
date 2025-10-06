package com.financeiro.presentation.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.application.services.TransacaoService;
import com.financeiro.presentation.dto.transacao.CreateTransacaoRequest;
import com.financeiro.presentation.dto.transacao.ResumoFinanceiroResponse;
import com.financeiro.presentation.dto.transacao.TransacaoResponse;
import com.financeiro.presentation.dto.transacao.UpdateTransacaoRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> listarTodas() {
        List<TransacaoResponse> transacoes = transacaoService.listarTransacoes(null, null);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponse>> listarPorUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        try {
            List<TransacaoResponse> transacoes = transacaoService.listarTransacoesPorUsuario(usuarioId, dataInicio, dataFim);
            return ResponseEntity.ok(transacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> buscarTransacao(@PathVariable UUID id) {
        try {
            TransacaoResponse transacao = transacaoService.buscarPorId(id);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TransacaoResponse> criarTransacao(@Valid @RequestBody CreateTransacaoRequest request) {
        try {
            TransacaoResponse transacao = transacaoService.criarTransacao(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<TransacaoResponse> criarTransacaoPorUsuario(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody CreateTransacaoRequest request) {
        try {
            TransacaoResponse transacao = transacaoService.criarTransacao(request, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponse> atualizarTransacao(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransacaoRequest request) {
        try {
            TransacaoResponse transacao = transacaoService.atualizarTransacao(id, request);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable UUID id) {
        try {
            transacaoService.deletarTransacao(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<TransacaoResponse>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        List<TransacaoResponse> transacoes = transacaoService.listarTransacoes(inicio, fim);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> calcularSaldo() {
        BigDecimal saldo = transacaoService.calcularSaldo();
        return ResponseEntity.ok(saldo);
    }

    @GetMapping("/usuario/{usuarioId}/saldo")
    public ResponseEntity<BigDecimal> calcularSaldoPorUsuario(@PathVariable UUID usuarioId) {
        try {
            BigDecimal saldo = transacaoService.calcularSaldoPorUsuario(usuarioId);
            return ResponseEntity.ok(saldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroResponse> resumoFinanceiro(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) UUID usuarioId) {
        try {
            ResumoFinanceiroResponse resumo;
            
            // Se usuarioId foi fornecido, filtrar por usuário específico
            if (usuarioId != null) {
                resumo = transacaoService.obterResumoFinanceiroPorUsuario(usuarioId, dataInicio, dataFim);
            } else {
                // Caso contrário, retornar resumo geral
                resumo = transacaoService.obterResumoFinanceiro(dataInicio, dataFim);
            }
            
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/resumo")
    public ResponseEntity<ResumoFinanceiroResponse> resumoFinanceiroPorUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        try {
            ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiroPorUsuario(usuarioId, dataInicio, dataFim);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
