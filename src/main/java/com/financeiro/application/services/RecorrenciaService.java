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
     * Job executado diariamente às 02:00 para gerar transações fixas
     * Cron: segundo, minuto, hora, dia do mês, mês, dia da semana
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processarRecorrenciasFixas() {
        log.info("Iniciando processamento de recorrências fixas...");
        
        LocalDate hoje = LocalDate.now();
        
        // Buscar todas as transações FIXA que estão ativas
        List<Transacao> transacoesFixas = transacaoRepository.findByTipoRecorrenciaAndAtiva(
                TipoRecorrencia.FIXA, true);
        
        log.info("Encontradas {} transações fixas ativas", transacoesFixas.size());
        
        int geradas = 0;
        
        for (Transacao transacaoOriginal : transacoesFixas) {
            try {
                // Calcular a próxima data de ocorrência
                LocalDate proximaData = transacaoOriginal.getFrequencia()
                        .calcularProximaData(transacaoOriginal.getDataTransacao());
                
                // Se a próxima data já passou ou é hoje, criar nova transação
                if (!proximaData.isAfter(hoje)) {
                    // Verificar se já existe transação para essa data (evitar duplicação)
                    boolean jaExiste = transacaoRepository.existsByTransacaoPaiIdAndDataTransacao(
                            transacaoOriginal.getId(), proximaData);
                    
                    if (!jaExiste) {
                        criarProximaOcorrencia(transacaoOriginal, proximaData);
                        geradas++;
                        log.info("Criada nova ocorrência para transação {} na data {}", 
                                transacaoOriginal.getId(), proximaData);
                    } else {
                        log.debug("Ocorrência já existe para transação {} na data {}", 
                                transacaoOriginal.getId(), proximaData);
                    }
                }
            } catch (Exception e) {
                log.error("Erro ao processar recorrência da transação {}: {}", 
                        transacaoOriginal.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Processamento de recorrências fixas concluído. {} novas transações geradas", geradas);
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
                .tipoRecorrencia(TipoRecorrencia.FIXA)
                .frequencia(original.getFrequencia())
                .transacaoPaiId(original.getId())
                .ativa(true)
                .build();
        
        transacaoRepository.save(novaOcorrencia);
        
        // Atualizar a data da transação original para a próxima ocorrência
        original.setDataTransacao(proximaData);
        transacaoRepository.save(original);
    }

    /**
     * Método auxiliar para executar o job manualmente (para testes)
     */
    public void executarManualmente() {
        log.info("Execução manual do job de recorrências solicitada");
        processarRecorrenciasFixas();
    }
}
