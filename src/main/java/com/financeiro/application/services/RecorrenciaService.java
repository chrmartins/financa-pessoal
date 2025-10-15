package com.financeiro.application.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.enums.TipoRecorrencia;
import com.financeiro.repository.TransacaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por processar transações recorrentes fixas automaticamente
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecorrenciaService {

    private final TransacaoRepository transacaoRepository;

    /**
     * Job executado diariamente às 02:00 para COMPLEMENTAR transações fixas
     * Cron: segundo, minuto, hora, dia do mês, mês, dia da semana
     * 
     * Agora o JOB apenas COMPLEMENTA: se faltam ocorrências, cria mais
     * A criação inicial já cria 12 meses adiantados
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processarRecorrenciasFixas() {
        log.info("🔄 Iniciando processamento de recorrências fixas (complementação)...");
        
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusMonths(12); // Mantém sempre 12 meses no futuro
        
        // Buscar todas as transações FIXA que estão ativas E são "origem" (transacaoPaiId é nulo)
        List<Transacao> transacoesOrigem = transacaoRepository.findByTipoRecorrenciaAndAtiva(
                TipoRecorrencia.FIXA, true);
        
        // Filtrar apenas as transações origem (que não têm pai)
        transacoesOrigem = transacoesOrigem.stream()
                .filter(t -> t.getTransacaoPaiId() == null)
                .toList();
        
        log.info("📋 Encontradas {} transações FIXA ativas (origem)", transacoesOrigem.size());
        
        int totalGeradas = 0;
        
        for (Transacao transacaoOrigem : transacoesOrigem) {
            try {
                int geradas = processarTransacaoFixa(transacaoOrigem, hoje, dataLimite);
                totalGeradas += geradas;
                
                if (geradas > 0) {
                    log.info("📊 '{}' criou {} novas ocorrências (complementação)", 
                            transacaoOrigem.getDescricao(), geradas);
                }
            } catch (Exception e) {
                log.error("❌ Erro ao processar recorrência da transação {}: {}", 
                        transacaoOrigem.getId(), e.getMessage(), e);
            }
        }
        
        log.info("✅ Processamento concluído. {} novas transações geradas no total", totalGeradas);
    }
    
    /**
     * Processa uma transação FIXA, criando todas as ocorrências necessárias
     */
    private int processarTransacaoFixa(Transacao origem, LocalDate hoje, LocalDate dataLimite) {
        // Encontra a última ocorrência criada
        LocalDate ultimaData = encontrarUltimaOcorrencia(origem);
        
        // Se não houver ocorrências além da original, começa da data original
        if (ultimaData == null) {
            ultimaData = origem.getDataTransacao();
        }
        
        // Calcula a próxima data a partir da última
        LocalDate proximaData = origem.getFrequencia().calcularProximaData(ultimaData);
        
        int criadas = 0;
        
        // ✅ CORREÇÃO: Cria TODAS as ocorrências até a data limite
        while (!proximaData.isAfter(dataLimite)) {
            
            // Verifica se já existe (evita duplicatas)
            boolean jaExiste = transacaoRepository.existsByTransacaoPaiIdAndDataTransacao(
                    origem.getId(), proximaData);
            
            if (!jaExiste) {
                criarProximaOcorrencia(origem, proximaData);
                criadas++;
                log.debug("✅ Criada ocorrência: {} - {}", origem.getDescricao(), proximaData);
            } else {
                log.debug("⏭️  Ocorrência já existe: {} - {}", origem.getDescricao(), proximaData);
            }
            
            // Calcula a próxima data (continua o loop)
            proximaData = origem.getFrequencia().calcularProximaData(proximaData);
        }
        
        return criadas;
    }
    
    /**
     * Encontra a última ocorrência criada para esta transação origem
     */
    private LocalDate encontrarUltimaOcorrencia(Transacao origem) {
        return transacaoRepository.findTopByTransacaoPaiIdOrderByDataTransacaoDesc(origem.getId())
                .map(Transacao::getDataTransacao)
                .orElse(null);
    }

    /**
     * Cria a próxima ocorrência de uma transação fixa
     */
    private void criarProximaOcorrencia(Transacao original, LocalDate proximaData) {
        Transacao novaOcorrencia = Transacao.builder()
                .descricao(original.getDescricao())
                .valor(original.getValor())
                .dataTransacao(proximaData)
                .tipo(original.getTipo())
                .observacoes(original.getObservacoes())
                .categoria(original.getCategoria())
                .usuario(original.getUsuario())
                .recorrente(true)  // ✅ Marca como recorrente
                .tipoRecorrencia(TipoRecorrencia.FIXA)
                .frequencia(original.getFrequencia())
                .transacaoPaiId(original.getId())  // Referencia a transação original
                .ativa(true)
                .build();
        
        transacaoRepository.save(novaOcorrencia);
        
        log.debug("💾 Nova ocorrência salva: ID={}, Data={}", novaOcorrencia.getId(), proximaData);
    }

    /**
     * Método auxiliar para executar o job manualmente (para testes)
     */
    public void executarManualmente() {
        log.info("Execução manual do job de recorrências solicitada");
        processarRecorrenciasFixas();
    }
}
