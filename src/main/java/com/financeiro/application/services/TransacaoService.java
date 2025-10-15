package com.financeiro.application.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Transacao;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.domain.enums.TipoRecorrencia;
import com.financeiro.presentation.dto.transacao.CreateTransacaoRequest;
import com.financeiro.presentation.dto.transacao.ResumoFinanceiroResponse;
import com.financeiro.presentation.dto.transacao.TransacaoResponse;
import com.financeiro.presentation.dto.transacao.UpdateTransacaoRequest;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        
        // DEPRECATED: Este método não deve ser usado, pois pega sempre o primeiro usuário
        // Use criarTransacaoParaUsuarioAutenticado() ou criarTransacao(request, usuarioId)
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

    public TransacaoResponse criarTransacaoParaUsuarioAutenticado(CreateTransacaoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));

        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(request.getCategoriaId(), usuario.getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada para o usuário autenticado"));

        // Validar request com base no tipo de recorrência
        validarRequest(request);

        // Rotear para método apropriado baseado no tipo de recorrência
        return switch (request.getTipoRecorrencia()) {
            case NAO_RECORRENTE -> criarTransacaoSimples(request, usuario, categoria);
            case PARCELADA -> criarTransacaoParcelada(request, usuario, categoria);
            case FIXA -> criarTransacaoFixa(request, usuario, categoria);
        };
    }

    /**
     * Valida os campos obrigatórios conforme o tipo de recorrência
     */
    private void validarRequest(CreateTransacaoRequest request) {
        if (request.getTipoRecorrencia() == TipoRecorrencia.PARCELADA) {
            if (request.getQuantidadeParcelas() == null) {
                throw new IllegalArgumentException("Quantidade de parcelas é obrigatória para transações PARCELADA");
            }
            if (request.getQuantidadeParcelas() < 2 || request.getQuantidadeParcelas() > 60) {
                throw new IllegalArgumentException("Quantidade de parcelas deve estar entre 2 e 60");
            }
        }
        
        if (request.getTipoRecorrencia() == TipoRecorrencia.FIXA) {
            if (request.getFrequencia() == null) {
                throw new IllegalArgumentException("Frequência é obrigatória para transações FIXA");
            }
        }
    }

    /**
     * Cria uma transação simples (não recorrente)
     */
    private TransacaoResponse criarTransacaoSimples(CreateTransacaoRequest request, Usuario usuario, Categoria categoria) {
        log.info("Criando transação simples para usuário {}", usuario.getEmail());
        
        Transacao transacao = Transacao.builder()
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .dataTransacao(request.getDataTransacao())
                .tipo(request.getTipo())
                .observacoes(request.getObservacoes())
                .categoria(categoria)
                .usuario(usuario)
                .recorrente(false)  // ✅ Transação única não é recorrente
                .tipoRecorrencia(TipoRecorrencia.NAO_RECORRENTE)
                .ativa(true)
                .build();
        
        Transacao salva = transacaoRepository.save(transacao);
        log.info("Transação simples criada com ID: {}", salva.getId());
        
        return TransacaoResponse.fromEntity(salva);
    }

    /**
     * Cria todas as parcelas de uma transação parcelada
     */
    private TransacaoResponse criarTransacaoParcelada(CreateTransacaoRequest request, Usuario usuario, Categoria categoria) {
        log.info("Criando transação parcelada com {} parcelas para usuário {}", 
                request.getQuantidadeParcelas(), usuario.getEmail());
        
        List<Transacao> parcelas = new ArrayList<>();
        
        // 1. Criar primeira parcela (pai)
        Transacao primeira = Transacao.builder()
                .descricao(request.getDescricao() + " (1/" + request.getQuantidadeParcelas() + ")")
                .valor(request.getValor())
                .dataTransacao(request.getDataTransacao())
                .tipo(request.getTipo())
                .observacoes(request.getObservacoes())
                .categoria(categoria)
                .usuario(usuario)
                .recorrente(true)  // ✅ Transação parcelada É recorrente
                .tipoRecorrencia(TipoRecorrencia.PARCELADA)
                .quantidadeParcelas(request.getQuantidadeParcelas())
                .parcelaAtual(1)
                .ativa(true)
                .build();
        
        Transacao primeiraSalva = transacaoRepository.save(primeira);
        parcelas.add(primeiraSalva);
        
        // 2. Criar demais parcelas (2 a N)
        for (int i = 2; i <= request.getQuantidadeParcelas(); i++) {
            LocalDate dataParcela = request.getDataTransacao().plusMonths(i - 1);
            
            Transacao parcela = Transacao.builder()
                    .descricao(request.getDescricao() + " (" + i + "/" + request.getQuantidadeParcelas() + ")")
                    .valor(request.getValor())
                    .dataTransacao(dataParcela)
                    .tipo(request.getTipo())
                    .observacoes(request.getObservacoes())
                    .categoria(categoria)
                    .usuario(usuario)
                    .recorrente(true)  // ✅ Parcela também é recorrente
                    .tipoRecorrencia(TipoRecorrencia.PARCELADA)
                    .quantidadeParcelas(request.getQuantidadeParcelas())
                    .parcelaAtual(i)
                    .transacaoPaiId(primeiraSalva.getId())
                    .ativa(true)
                    .build();
            
            Transacao parcelaSalva = transacaoRepository.save(parcela);
            parcelas.add(parcelaSalva);
        }
        
        log.info("Transação parcelada criada: {} parcelas geradas", parcelas.size());
        
        return TransacaoResponse.fromEntity(primeiraSalva);
    }

    /**
     * Cria transação fixa + próximas 12 ocorrências para visualização no frontend
     * Frontend navega mês a mês, então criamos antecipadamente as próximas ocorrências
     */
    private TransacaoResponse criarTransacaoFixa(CreateTransacaoRequest request, Usuario usuario, Categoria categoria) {
        log.info("Criando transação fixa com frequência {} para usuário {}", 
                request.getFrequencia(), usuario.getEmail());
        
        // 1. Criar transação original (pai)
        Transacao transacaoOriginal = Transacao.builder()
                .descricao(request.getDescricao())
                .valor(request.getValor())
                .dataTransacao(request.getDataTransacao())
                .tipo(request.getTipo())
                .observacoes(request.getObservacoes())
                .categoria(categoria)
                .usuario(usuario)
                .recorrente(true)
                .tipoRecorrencia(TipoRecorrencia.FIXA)
                .frequencia(request.getFrequencia())
                .ativa(true)
                .build();
        
        Transacao original = transacaoRepository.save(transacaoOriginal);
        log.info("Transação fixa original criada com ID: {}", original.getId());
        
        // 2. Criar próximas 12 ocorrências imediatamente para visualização no frontend
        int ocorrenciasCriadas = criarOcorrenciasFuturas(original, 12);
        
        log.info("Transação fixa criada: 1 original + {} ocorrências futuras", ocorrenciasCriadas);
        
        return TransacaoResponse.fromEntity(original);
    }
    
    /**
     * Cria N ocorrências futuras de uma transação FIXA
     * Usado tanto na criação inicial quanto pelo JOB
     */
    private int criarOcorrenciasFuturas(Transacao original, int quantidadeOcorrencias) {
        LocalDate proximaData = original.getDataTransacao();
        int criadas = 0;
        
        for (int i = 1; i <= quantidadeOcorrencias; i++) {
            // Calcula próxima data
            proximaData = original.getFrequencia().calcularProximaData(proximaData);
            
            // Verifica se já existe (evita duplicação)
            boolean jaExiste = transacaoRepository.existsByTransacaoPaiIdAndDataTransacao(
                    original.getId(), proximaData);
            
            if (!jaExiste) {
                Transacao ocorrencia = Transacao.builder()
                        .descricao(original.getDescricao())
                        .valor(original.getValor())
                        .dataTransacao(proximaData)
                        .tipo(original.getTipo())
                        .observacoes(original.getObservacoes())
                        .categoria(original.getCategoria())
                        .usuario(original.getUsuario())
                        .recorrente(true)
                        .tipoRecorrencia(TipoRecorrencia.FIXA)
                        .frequencia(original.getFrequencia())
                        .transacaoPaiId(original.getId())
                        .ativa(true)
                        .build();
                
                transacaoRepository.save(ocorrencia);
                criadas++;
                
                log.debug("Ocorrência criada: {} - {}", original.getDescricao(), proximaData);
            }
        }
        
        return criadas;
    }

    /**
     * DEPRECATED: Método antigo de criar transação recorrente
     * Use criarTransacaoParcelada() através de criarTransacaoParaUsuarioAutenticado()
     */
    @Deprecated
    private TransacaoResponse criarTransacaoRecorrente(CreateTransacaoRequest request, Usuario usuario, Categoria categoria) {
        // 1. Criar a primeira parcela (transação principal)
        Transacao principal = criarTransacaoEntity(
                request, 
                usuario, 
                categoria, 
                true, 
                request.getQuantidadeParcelas(), 
                1, 
                null
        );
        Transacao principalSalva = transacaoRepository.save(principal);

        // 2. Criar as parcelas futuras (2, 3, 4, ..., quantidadeParcelas)
        for (int i = 2; i <= request.getQuantidadeParcelas(); i++) {
            LocalDate dataFutura = request.getDataTransacao().plusMonths(i - 1);
            
            Transacao parcela = criarTransacaoEntity(
                    request,
                    usuario,
                    categoria,
                    true,
                    request.getQuantidadeParcelas(),
                    i,
                    principalSalva.getId() // ID da transação pai
            );
            parcela.setDataTransacao(dataFutura);
            
            transacaoRepository.save(parcela);
        }

        return TransacaoResponse.fromEntity(principalSalva);
    }

    /**
     * Método auxiliar para criar uma entidade Transacao
     */
    private Transacao criarTransacaoEntity(
            CreateTransacaoRequest request, 
            Usuario usuario, 
            Categoria categoria,
            boolean recorrente,
            Integer quantidadeParcelas,
            Integer parcelaAtual,
            UUID transacaoPaiId) {
        
        Transacao transacao = new Transacao();
        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setDataTransacao(request.getDataTransacao());
        transacao.setTipo(request.getTipo());
        transacao.setObservacoes(request.getObservacoes());
        transacao.setCategoria(categoria);
        transacao.setUsuario(usuario);
        transacao.setRecorrente(recorrente);
        transacao.setQuantidadeParcelas(quantidadeParcelas);
        transacao.setParcelaAtual(parcelaAtual);
        transacao.setTransacaoPaiId(transacaoPaiId);
        
        return transacao;
    }

    public TransacaoResponse criarTransacao(CreateTransacaoRequest request, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    Categoria categoria = categoriaRepository.findByIdAndUsuarioId(request.getCategoriaId(), usuario.getId())
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada para o usuário informado"));

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
            transacoes = transacaoRepository.findAllWithRelations();
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
        Transacao transacao = transacaoRepository.findByIdWithRelations(id)
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
            transacoes = transacaoRepository.findAllWithRelations();
        }

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas, saldo);
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
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas, saldo);
    }

    // ====================================================================
    // MÉTODOS SEGUROS - Usam o usuário autenticado do JWT
    // ====================================================================

    /**
     * Lista transações do usuário autenticado com filtro opcional de período
     * ✅ SEGURO: Usa email do JWT, não aceita usuarioId do frontend
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponse> listarTransacoesDoUsuarioAutenticado(
            String emailUsuario, LocalDate dataInicio, LocalDate dataFim) {
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        List<Transacao> transacoes;
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByUsuarioIdAndDataTransacaoBetween(
                    usuario.getId(), dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findByUsuarioId(usuario.getId());
        }
        
        return transacoes.stream()
                .map(TransacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca transação por ID validando se pertence ao usuário autenticado
     * ✅ SEGURO: Valida propriedade da transação
     */
    @Transactional(readOnly = true)
    public TransacaoResponse buscarPorIdDoUsuarioAutenticado(UUID id, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        Transacao transacao = transacaoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Validar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: transação pertence a outro usuário");
        }
        
        return TransacaoResponse.fromEntity(transacao);
    }

    /**
     * Atualiza transação validando se pertence ao usuário autenticado
     * ✅ SEGURO: Valida propriedade antes de atualizar
     */
    public TransacaoResponse atualizarTransacaoDoUsuarioAutenticado(
            UUID id, UpdateTransacaoRequest request, String emailUsuario) {
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Validar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: transação pertence a outro usuário");
        }

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
            // Validar se a categoria pertence ao usuário
            Categoria categoria = categoriaRepository.findByIdAndUsuarioId(
                    request.getCategoriaId(), usuario.getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada para o usuário autenticado"));
            transacao.setCategoria(categoria);
        }
        if (request.getObservacoes() != null) {
            transacao.setObservacoes(request.getObservacoes());
        }

        Transacao atualizada = transacaoRepository.save(transacao);
        return TransacaoResponse.fromEntity(atualizada);
    }

    /**
     * Deleta transação validando se pertence ao usuário autenticado
     * ✅ SEGURO: Valida propriedade antes de deletar
     */
    public void deletarTransacaoDoUsuarioAutenticado(UUID id, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        
        // Validar se a transação pertence ao usuário autenticado
        if (!transacao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado: transação pertence a outro usuário");
        }
        
        transacaoRepository.deleteById(id);
    }

    /**
     * Calcula saldo do usuário autenticado
     * ✅ SEGURO: Usa email do JWT
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoDoUsuarioAutenticado(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        BigDecimal saldo = transacaoRepository.calcularSaldoPorUsuario(usuario.getId());
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    /**
     * Obtém resumo financeiro do usuário autenticado
     * ✅ SEGURO: Usa email do JWT
     */
    @Transactional(readOnly = true)
    public ResumoFinanceiroResponse obterResumoFinanceiroDoUsuarioAutenticado(
            String emailUsuario, LocalDate dataInicio, LocalDate dataFim) {
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
        
        List<Transacao> transacoes;
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByUsuarioIdAndDataTransacaoBetween(
                    usuario.getId(), dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findByUsuarioId(usuario.getId());
        }

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        return new ResumoFinanceiroResponse(totalReceitas, totalDespesas, saldo);
    }
}