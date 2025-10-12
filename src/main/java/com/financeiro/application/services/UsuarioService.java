package com.financeiro.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.domain.entities.Categoria;
import com.financeiro.domain.entities.Categoria.TipoCategoria;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço de aplicação para gerenciar usuários.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário com categorias padrão
     */
    @Transactional
    public Usuario criarUsuarioComCategoriasDefault(String nome, String email, String senha, Usuario.Papel papel) {
        log.info("Criando novo usuário: {}", email);
        
        // Verificar se email já existe
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }

        // Criar usuário
        Usuario novoUsuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .papel(papel)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        log.info("Usuário criado com ID: {}", usuarioSalvo.getId());

        // Criar categorias padrão
        criarCategoriasDefault(usuarioSalvo);
        
        return usuarioSalvo;
    }

    /**
     * Cria categorias padrão para o usuário
     */
    private void criarCategoriasDefault(Usuario usuario) {
        log.info("Criando categorias padrão para usuário: {}", usuario.getEmail());
        
        List<Categoria> categoriasDefault = List.of(
                // Categorias de RECEITA
                Categoria.builder()
                        .nome("Salário")
                        .descricao("Receita de salário mensal")
                        .tipo(TipoCategoria.RECEITA)
                        .cor("#10b981")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Freelance")
                        .descricao("Trabalhos extras e freelances")
                        .tipo(TipoCategoria.RECEITA)
                        .cor("#3b82f6")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Investimentos")
                        .descricao("Rendimentos de investimentos")
                        .tipo(TipoCategoria.RECEITA)
                        .cor("#8b5cf6")
                        .usuario(usuario)
                        .build(),
                
                // Categorias de DESPESA
                Categoria.builder()
                        .nome("Alimentação")
                        .descricao("Gastos com alimentação e supermercado")
                        .tipo(TipoCategoria.DESPESA)
                        .cor("#ef4444")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Transporte")
                        .descricao("Combustível, uber, transporte público")
                        .tipo(TipoCategoria.DESPESA)
                        .cor("#f59e0b")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Saúde")
                        .descricao("Plano de saúde, medicamentos, consultas")
                        .tipo(TipoCategoria.DESPESA)
                        .cor("#ec4899")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Moradia")
                        .descricao("Aluguel, condomínio, IPTU")
                        .tipo(TipoCategoria.DESPESA)
                        .cor("#6366f1")
                        .usuario(usuario)
                        .build(),
                
                Categoria.builder()
                        .nome("Contas")
                        .descricao("Energia, água, internet, telefone")
                        .tipo(TipoCategoria.DESPESA)
                        .cor("#14b8a6")
                        .usuario(usuario)
                        .build()
        );

        List<Categoria> salvas = categoriaRepository.saveAll(categoriasDefault);
        log.info("{} categorias padrão criadas para usuário {}", salvas.size(), usuario.getEmail());
    }

    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca usuário por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Lista todos os usuários ativos
     */
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    /**
     * Atualiza dados do usuário
     */
    @Transactional
    public Usuario atualizarUsuario(UUID id, String nome, String email, String senha, Usuario.Papel papel) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (nome != null) {
            usuario.setNome(nome);
        }
        
        if (email != null && !email.equals(usuario.getEmail())) {
            // Verificar se novo email já está em uso
            usuarioRepository.findByEmail(email)
                    .filter(u -> !u.getId().equals(id))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Email já está em uso");
                    });
            usuario.setEmail(email);
        }
        
        if (senha != null) {
            usuario.setSenha(passwordEncoder.encode(senha));
        }
        
        if (papel != null) {
            usuario.setPapel(papel);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Desativa um usuário
     */
    @Transactional
    public void desativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário desativado: {}", usuario.getEmail());
    }

    /**
     * Ativa um usuário
     */
    @Transactional
    public void ativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuário ativado: {}", usuario.getEmail());
    }
    
    /**
     * Deleta permanentemente um usuário e todos os seus dados relacionados (transações e categorias).
     * ATENÇÃO: Esta operação é irreversível e remove completamente o usuário do banco de dados.
     * 
     * @param id UUID do usuário a ser deletado
     * @throws IllegalArgumentException se o usuário não for encontrado
     * @throws IllegalStateException se tentar deletar o usuário admin
     */
    @Transactional
    public void deletarUsuarioPermanentemente(UUID id) {
        log.warn("Iniciando deleção permanente do usuário com ID: {}", id);
        
        // Buscar usuário
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Proteção: não permitir deletar o admin
        if ("admin@financeiro.com".equals(usuario.getEmail())) {
            log.error("Tentativa de deletar o usuário admin bloqueada");
            throw new IllegalStateException("Não é permitido deletar o usuário administrador do sistema");
        }
        
        String email = usuario.getEmail();
        
        // Contar registros antes de deletar (para logging)
        long qtdTransacoes = transacaoRepository.countByUsuarioId(id);
        long qtdCategorias = categoriaRepository.countByUsuarioId(id);
        
        log.info("Deletando {} transação(ões) do usuário {}", qtdTransacoes, email);
        transacaoRepository.deleteByUsuarioId(id);
        
        log.info("Deletando {} categoria(s) do usuário {}", qtdCategorias, email);
        categoriaRepository.deleteByUsuarioId(id);
        
        log.info("Deletando usuário {}", email);
        usuarioRepository.delete(usuario);
        
        log.warn("Usuário {} deletado permanentemente com sucesso. " +
                "Removidas {} transação(ões) e {} categoria(s)", 
                email, qtdTransacoes, qtdCategorias);
    }
}
