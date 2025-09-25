package com.financeiro.presentation.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.financeiro.application.services.UsuarioService;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.transacao.CreateTransacaoRequest;
import com.financeiro.presentation.dto.transacao.TransacaoResponse;
import com.financeiro.presentation.dto.transacao.UpdateTransacaoRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de transações financeiras.
 * Expõe endpoints para CRUD de transações.
 */
@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final UsuarioService usuarioService;
        /**
         * Resumo financeiro por período
         */
        @GetMapping("/resumo")
        public ResponseEntity<?> resumoFinanceiro(
            @RequestParam UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
        ) {
            try {
                Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

                var resumo = transacaoService.resumoFinanceiro(usuario, dataInicio, dataFim);
                return ResponseEntity.ok(resumo);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

    /**
     * Criar nova transação (com suporte a recorrência)
     */
    @Operation(summary = "Criar nova transação", 
               description = "Cria uma nova transação. Suporta transações recorrentes (parceladas) quando recorrente=true e quantidadeParcelas > 1")
    @PostMapping
    public ResponseEntity<TransacaoResponse> criarTransacao(
            @Valid @RequestBody CreateTransacaoRequest request,
            @RequestParam UUID usuarioId) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            Transacao transacao = transacaoService.criarTransacaoComRecorrencia(
                    request.getDescricao(),
                    request.getValor(),
                    request.getDataTransacao(),
                    request.getTipo(),
                    request.getCategoriaId(),
                    usuario,
                    request.getObservacoes(),
                    request.getRecorrente(),
                    request.getQuantidadeParcelas()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(TransacaoResponse.fromEntity(transacao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Buscar transação por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> buscarTransacao(@PathVariable UUID id) {
        return transacaoService.buscarPorId(id)
                .map(transacao -> ResponseEntity.ok(TransacaoResponse.fromEntity(transacao)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar transações de um usuário com filtros opcionais de data
     */
    @Operation(summary = "Listar transações do usuário", 
               description = "Lista as transações de um usuário. Opcionalmente filtra por período usando dataInicio e dataFim.")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponse>> listarTransacoesDoUsuario(
            @PathVariable UUID usuarioId,
            @Parameter(description = "Data de início do período (formato: YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim do período (formato: YYYY-MM-DD)", example = "2025-01-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Transacao> transacoes;
            
            // Se ambas as datas foram fornecidas, usa filtro por período
            if (dataInicio != null && dataFim != null) {
                System.out.println("🔍 DEBUG: Filtrando transações por período - " + 
                                 "Início: " + dataInicio + ", Fim: " + dataFim + 
                                 ", Usuário: " + usuario.getNome());
                transacoes = transacaoService.listarTransacoesPorPeriodo(usuario, dataInicio, dataFim);
                System.out.println("📊 DEBUG: Encontradas " + transacoes.size() + " transações no período");
            } else {
                System.out.println("📋 DEBUG: Listando todas as transações do usuário: " + usuario.getNome());
                transacoes = transacaoService.listarTransacoesDoUsuario(usuario);
                System.out.println("📊 DEBUG: Total de transações do usuário: " + transacoes.size());
            }
            
            List<TransacaoResponse> response = transacoes.stream()
                    .map(TransacaoResponse::fromEntitySimple)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Listar transações de um usuário por período
     */
    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<TransacaoResponse>> listarTransacoesPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Transacao> transacoes = transacaoService.listarTransacoesPorPeriodo(usuario, dataInicio, dataFim);
            List<TransacaoResponse> response = transacoes.stream()
                    .map(TransacaoResponse::fromEntitySimple)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Listar transações por categoria
     */
    @GetMapping("/usuario/{usuarioId}/categoria/{categoriaId}")
    public ResponseEntity<List<TransacaoResponse>> listarTransacoesPorCategoria(
            @PathVariable UUID usuarioId,
            @PathVariable UUID categoriaId) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Transacao> transacoes = transacaoService.listarTransacoesPorCategoria(usuario, categoriaId);
            List<TransacaoResponse> response = transacoes.stream()
                    .map(TransacaoResponse::fromEntitySimple)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Listar transações por tipo
     */
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<TransacaoResponse>> listarTransacoesPorTipo(
            @PathVariable UUID usuarioId,
            @PathVariable Transacao.TipoTransacao tipo) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Transacao> transacoes = transacaoService.listarTransacoesPorTipo(usuario, tipo);
            List<TransacaoResponse> response = transacoes.stream()
                    .map(TransacaoResponse::fromEntitySimple)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calcular saldo de um usuário
     */
    @GetMapping("/usuario/{usuarioId}/saldo")
    public ResponseEntity<BigDecimal> calcularSaldo(@PathVariable UUID usuarioId) {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            BigDecimal saldo = transacaoService.calcularSaldo(usuario);
            return ResponseEntity.ok(saldo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Calcular saldo de um usuário por período
     */
    @GetMapping("/usuario/{usuarioId}/saldo/periodo")
    public ResponseEntity<BigDecimal> calcularSaldoPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            BigDecimal saldo = transacaoService.calcularSaldoPorPeriodo(usuario, dataInicio, dataFim);
            return ResponseEntity.ok(saldo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualizar transação
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponse> atualizarTransacao(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransacaoRequest request) {
        
        try {
            Transacao transacao = transacaoService.atualizarTransacao(
                    id,
                    request.getDescricao(),
                    request.getValor(),
                    request.getDataTransacao(),
                    request.getCategoriaId(),
                    request.getObservacoes()
            );
            return ResponseEntity.ok(TransacaoResponse.fromEntity(transacao));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remover transação
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerTransacao(@PathVariable UUID id) {
        try {
            transacaoService.removerTransacao(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar transações por descrição
     */
    @GetMapping("/usuario/{usuarioId}/buscar")
    public ResponseEntity<List<TransacaoResponse>> buscarPorDescricao(
            @PathVariable UUID usuarioId,
            @RequestParam String descricao) {
        
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Transacao> transacoes = transacaoService.buscarPorDescricao(usuario, descricao);
            List<TransacaoResponse> response = transacoes.stream()
                    .map(TransacaoResponse::fromEntitySimple)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}