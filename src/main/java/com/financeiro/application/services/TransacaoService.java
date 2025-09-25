package com.financeiro.application.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
     * Caso de uso: Resumo financeiro por período
     */
    @Transactional(readOnly = true)
    public com.financeiro.presentation.dto.transacao.ResumoFinanceiroResponse resumoFinanceiro(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes = transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);

        BigDecimal totalReceitas = transacoes.stream()
            .filter(t -> t.getTipo() == Transacao.TipoTransacao.RECEITA)
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
            .filter(t -> t.getTipo() == Transacao.TipoTransacao.DESPESA)
            .map(Transacao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new com.financeiro.presentation.dto.transacao.ResumoFinanceiroResponse(totalReceitas, totalDespesas, saldo);
    }

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
     * Caso de uso: Criar transação com suporte a recorrência
     */
    public Transacao criarTransacaoComRecorrencia(String descricao, BigDecimal valor, LocalDate dataTransacao, 
                                                Transacao.TipoTransacao tipo, UUID categoriaId, Usuario usuario, 
                                                String observacoes, Boolean recorrente, Integer quantidadeParcelas) {
        
        // Validações para transações recorrentes
        if (recorrente != null && recorrente) {
            if (quantidadeParcelas == null || quantidadeParcelas <= 1) {
                throw new IllegalArgumentException("Transações recorrentes devem ter ao menos 2 parcelas");
            }
            if (quantidadeParcelas > 60) {
                throw new IllegalArgumentException("Número máximo de parcelas é 60");
            }
        }

        // Se não é recorrente, usa o método padrão
        if (recorrente == null || !recorrente || quantidadeParcelas == null || quantidadeParcelas <= 1) {
            return criarTransacao(descricao, valor, dataTransacao, tipo, categoriaId, usuario, observacoes);
        }

        // Criar transações recorrentes em uma transação atômica
        return criarTransacoesRecorrentes(descricao, valor, dataTransacao, tipo, categoriaId, usuario, observacoes, quantidadeParcelas);
    }

    /**
     * Cria múltiplas transações recorrentes (parceladas) de forma atômica
     */
    private Transacao criarTransacoesRecorrentes(String descricao, BigDecimal valor, LocalDate dataTransacao,
                                               Transacao.TipoTransacao tipo, UUID categoriaId, Usuario usuario,
                                               String observacoes, Integer quantidadeParcelas) {
        
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        // Validar se o tipo da transação é compatível com o tipo da categoria
        boolean categoriaReceita = categoria.getTipo() == Categoria.TipoCategoria.RECEITA;
        boolean transacaoReceita = tipo == Transacao.TipoTransacao.RECEITA;
        
        if (categoriaReceita != transacaoReceita) {
            throw new IllegalArgumentException("Tipo da transação deve ser compatível com o tipo da categoria");
        }

        Transacao primeiraParcela = null;
        
        // Criar todas as parcelas
        for (int i = 1; i <= quantidadeParcelas; i++) {
            LocalDate dataParcela = dataTransacao.plusMonths(i - 1);
            String descricaoParcela = String.format("%s (%d/%d)", descricao, i, quantidadeParcelas);
            
            Transacao parcela = Transacao.builder()
                    .descricao(descricaoParcela)
                    .valor(valor)
                    .dataTransacao(dataParcela)
                    .tipo(tipo)
                    .categoria(categoria)
                    .usuario(usuario)
                    .observacoes(observacoes)
                    .build();

            parcela = transacaoRepository.save(parcela);
            
            // Guardar referência da primeira parcela para retornar
            if (i == 1) {
                primeiraParcela = parcela;
            }
        }
        
        return primeiraParcela;
    }

    /**
     * Caso de uso: Buscar transação por ID
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Transacao> buscarPorId(UUID id) {
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
        System.out.println("🔍 SERVICE DEBUG: Buscando transações - Usuário: " + usuario.getNome() + 
                         ", DataInício: " + dataInicio + ", DataFim: " + dataFim);
        
        List<Transacao> resultado = transacaoRepository.findByUsuarioAndDataTransacaoBetween(usuario, dataInicio, dataFim);
        
        System.out.println("📊 SERVICE DEBUG: Query retornou " + resultado.size() + " transações");
        resultado.forEach(t -> System.out.println("  📅 " + t.getDataTransacao() + " - " + t.getDescricao() + " - R$ " + t.getValor()));
        
        return resultado;
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