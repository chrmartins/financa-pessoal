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

        // Se for transação recorrente com múltiplas parcelas
        if (Boolean.TRUE.equals(request.getRecorrente()) && request.getQuantidadeParcelas() != null && request.getQuantidadeParcelas() > 1) {
            return criarTransacaoRecorrente(request, usuario, categoria);
        }

        // Transação única
        Transacao transacao = criarTransacaoEntity(request, usuario, categoria, false, null, null, null);
        Transacao salva = transacaoRepository.save(transacao);
        return TransacaoResponse.fromEntity(salva);
    }

    /**
     * Cria uma transação recorrente (com múltiplas parcelas)
     */
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