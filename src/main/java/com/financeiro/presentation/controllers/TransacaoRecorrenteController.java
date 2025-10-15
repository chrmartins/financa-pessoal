package com.financeiro.presentation.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.application.services.RecorrenciaService;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.enums.TipoRecorrencia;
import com.financeiro.repository.TransacaoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller para gerenciar transações recorrentes
 */
@Slf4j
@RestController
@RequestMapping("/api/transacoes/recorrentes")
@RequiredArgsConstructor
@Tag(name = "Transações Recorrentes", description = "Endpoints para gerenciar transações recorrentes")
@SecurityRequirement(name = "bearer-key")
public class TransacaoRecorrenteController {

    private final TransacaoRepository transacaoRepository;
    private final RecorrenciaService recorrenciaService;

    @PatchMapping("/{id}/pausar")
    @Operation(summary = "Pausar recorrência", 
               description = "Pausa a geração automática de uma transação recorrente FIXA")
    public ResponseEntity<Map<String, Object>> pausarRecorrencia(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Solicitação de pausa de recorrência para transação {}", id);
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Verificar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getEmail().equals(userDetails.getUsername())) {
            log.warn("Usuário {} tentou pausar transação {} de outro usuário", 
                    userDetails.getUsername(), id);
            return ResponseEntity.status(403).body(Map.of(
                    "erro", "Você não tem permissão para pausar esta transação"
            ));
        }
        
        // Verificar se é uma transação FIXA
        if (transacao.getTipoRecorrencia() != TipoRecorrencia.FIXA) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Apenas transações FIXA podem ser pausadas"
            ));
        }
        
        // Pausar
        transacao.setAtiva(false);
        transacaoRepository.save(transacao);
        
        log.info("Recorrência da transação {} pausada com sucesso", id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Recorrência pausada com sucesso");
        response.put("transacaoId", id);
        response.put("ativa", false);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar recorrência", 
               description = "Reativa a geração automática de uma transação recorrente FIXA pausada")
    public ResponseEntity<Map<String, Object>> reativarRecorrencia(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Solicitação de reativação de recorrência para transação {}", id);
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Verificar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getEmail().equals(userDetails.getUsername())) {
            log.warn("Usuário {} tentou reativar transação {} de outro usuário", 
                    userDetails.getUsername(), id);
            return ResponseEntity.status(403).body(Map.of(
                    "erro", "Você não tem permissão para reativar esta transação"
            ));
        }
        
        // Verificar se é uma transação FIXA
        if (transacao.getTipoRecorrencia() != TipoRecorrencia.FIXA) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Apenas transações FIXA podem ser reativadas"
            ));
        }
        
        // Reativar
        transacao.setAtiva(true);
        transacaoRepository.save(transacao);
        
        log.info("Recorrência da transação {} reativada com sucesso", id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Recorrência reativada com sucesso");
        response.put("transacaoId", id);
        response.put("ativa", true);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/cancelar-serie")
    @Operation(summary = "Cancelar série", 
               description = "Cancela todas as parcelas futuras de uma transação PARCELADA ou todas as ocorrências futuras de uma FIXA")
    public ResponseEntity<Map<String, Object>> cancelarSerie(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Solicitação de cancelamento de série para transação {}", id);
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Verificar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getEmail().equals(userDetails.getUsername())) {
            log.warn("Usuário {} tentou cancelar série da transação {} de outro usuário", 
                    userDetails.getUsername(), id);
            return ResponseEntity.status(403).body(Map.of(
                    "erro", "Você não tem permissão para cancelar esta série"
            ));
        }
        
        int canceladas = 0;
        
        if (transacao.getTipoRecorrencia() == TipoRecorrencia.PARCELADA) {
            // Buscar todas as parcelas futuras (data > hoje)
            List<Transacao> parcelasFuturas = transacaoRepository
                    .findByUsuarioId(transacao.getUsuario().getId())
                    .stream()
                    .filter(t -> t.getTransacaoPaiId() != null && 
                                 t.getTransacaoPaiId().equals(transacao.getId()) &&
                                 t.getDataTransacao().isAfter(java.time.LocalDate.now()))
                    .toList();
            
            transacaoRepository.deleteAll(parcelasFuturas);
            canceladas = parcelasFuturas.size();
            
            log.info("Canceladas {} parcelas futuras da transação {}", canceladas, id);
            
        } else if (transacao.getTipoRecorrencia() == TipoRecorrencia.FIXA) {
            // Pausar a transação para não gerar mais ocorrências
            transacao.setAtiva(false);
            transacaoRepository.save(transacao);
            
            // Buscar e deletar ocorrências futuras
            List<Transacao> ocorrenciasFuturas = transacaoRepository
                    .findByUsuarioId(transacao.getUsuario().getId())
                    .stream()
                    .filter(t -> t.getTransacaoPaiId() != null && 
                                 t.getTransacaoPaiId().equals(id) &&
                                 t.getDataTransacao().isAfter(java.time.LocalDate.now()))
                    .toList();
            
            transacaoRepository.deleteAll(ocorrenciasFuturas);
            canceladas = ocorrenciasFuturas.size();
            
            log.info("Pausada transação FIXA {} e canceladas {} ocorrências futuras", id, canceladas);
            
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Apenas transações PARCELADA ou FIXA podem ter séries canceladas"
            ));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Série cancelada com sucesso");
        response.put("transacaoId", id);
        response.put("quantidadeCancelada", canceladas);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/executar-job")
    @Operation(summary = "Executar job manualmente", 
               description = "Executa manualmente o job de geração de recorrências fixas (apenas para testes)")
    public ResponseEntity<Map<String, String>> executarJobManualmente() {
        log.info("Execução manual do job de recorrências solicitada");
        
        recorrenciaService.executarManualmente();
        
        return ResponseEntity.ok(Map.of(
                "mensagem", "Job executado com sucesso"
        ));
    }
}
