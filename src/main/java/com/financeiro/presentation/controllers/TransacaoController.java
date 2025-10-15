package com.financeiro.presentation.controllers;

import java.math.BigDecimal;
import java.security.Principal;
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

/**
 * Controller REST para gerenciamento de transações.
 * SEGURANÇA: Todos os endpoints usam o usuário autenticado do JWT,
 * ignorando qualquer usuarioId enviado pelo frontend.
 */
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * Lista todas as transações do usuário autenticado
     * ✅ SEGURO: Usa o email do JWT token
     */
    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> listarMinhasTransacoes(
            Principal principal,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            List<TransacaoResponse> transacoes = transacaoService.listarTransacoesDoUsuarioAutenticado(
                    emailUsuarioAutenticado, dataInicio, dataFim);
            return ResponseEntity.ok(transacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Busca uma transação por ID
     * ✅ SEGURO: Valida se a transação pertence ao usuário autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> buscarTransacao(
            @PathVariable UUID id,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            TransacaoResponse transacao = transacaoService.buscarPorIdDoUsuarioAutenticado(id, emailUsuarioAutenticado);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria uma nova transação para o usuário autenticado
     * ✅ SEGURO: Usa o email do JWT token, ignora qualquer usuarioId do request
     */
    @PostMapping
    public ResponseEntity<TransacaoResponse> criarTransacao(
            @Valid @RequestBody CreateTransacaoRequest request,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            TransacaoResponse transacao = transacaoService.criarTransacaoParaUsuarioAutenticado(
                    request, emailUsuarioAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualiza uma transação
     * ✅ SEGURO: Valida se a transação pertence ao usuário autenticado
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponse> atualizarTransacao(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransacaoRequest request,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            TransacaoResponse transacao = transacaoService.atualizarTransacaoDoUsuarioAutenticado(
                    id, request, emailUsuarioAutenticado);
            return ResponseEntity.ok(transacao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta uma transação
     * ✅ SEGURO: Valida se a transação pertence ao usuário autenticado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(
            @PathVariable UUID id,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            transacaoService.deletarTransacaoDoUsuarioAutenticado(id, emailUsuarioAutenticado);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista transações por período do usuário autenticado
     * ✅ SEGURO: Usa o email do JWT token
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<TransacaoResponse>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            Principal principal) {
        String emailUsuarioAutenticado = principal.getName();
        List<TransacaoResponse> transacoes = transacaoService.listarTransacoesDoUsuarioAutenticado(
                emailUsuarioAutenticado, inicio, fim);
        return ResponseEntity.ok(transacoes);
    }

    /**
     * Calcula o saldo do usuário autenticado
     * ✅ SEGURO: Usa o email do JWT token
     */
    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> calcularMeuSaldo(Principal principal) {
        String emailUsuarioAutenticado = principal.getName();
        BigDecimal saldo = transacaoService.calcularSaldoDoUsuarioAutenticado(emailUsuarioAutenticado);
        return ResponseEntity.ok(saldo);
    }

    /**
     * Obtém resumo financeiro do usuário autenticado
     * ✅ SEGURO: Usa o email do JWT token
     */
    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroResponse> resumoFinanceiro(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            ResumoFinanceiroResponse resumo = transacaoService.obterResumoFinanceiroDoUsuarioAutenticado(
                    emailUsuarioAutenticado, dataInicio, dataFim);
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Previsão de transações FIXAS para qualquer período futuro
     * Calcula em tempo real SEM salvar no banco de dados
     * ✅ SEGURO: Usa o email do JWT token
     * 
     * @param mes Mês desejado (1-12)
     * @param ano Ano desejado (ex: 2026)
     * @return Lista de transações previstas (reais + simuladas) para aquele mês
     */
    @GetMapping("/preview")
    public ResponseEntity<List<TransacaoResponse>> previsaoTransacoes(
            @RequestParam int mes,
            @RequestParam int ano,
            Principal principal) {
        try {
            String emailUsuarioAutenticado = principal.getName();
            
            // Valida o mês
            if (mes < 1 || mes > 12) {
                return ResponseEntity.badRequest().build();
            }
            
            List<TransacaoResponse> transacoes = transacaoService.previsaoTransacoesParaMes(
                    emailUsuarioAutenticado, mes, ano);
            return ResponseEntity.ok(transacoes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
