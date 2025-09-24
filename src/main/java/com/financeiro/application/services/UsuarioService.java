package com.financeiro.application.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financeiro.application.repositories.UsuarioRepository;
import com.financeiro.domain.entities.Usuario;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para gerenciamento de usuários.
 * Implementa os casos de uso relacionados aos usuários do sistema.
 * Também implementa UserDetailsService para integração com Spring Security.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Caso de uso: Criar novo usuário
     */
    public Usuario criarUsuario(String nome, String email, String senha) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha(passwordEncoder.encode(senha))
                .papel(Usuario.Papel.USER)
                .build();

        return usuarioRepository.save(usuario);
    }

    /**
     * Caso de uso: Buscar usuário por ID
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Caso de uso: Buscar usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Caso de uso: Atualizar dados do usuário
     */
    public Usuario atualizarUsuario(UUID id, String nome, String email) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Verificar se o email já existe (exceto para o próprio usuário)
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }

        usuario.setNome(nome);
        usuario.setEmail(email);

        return usuarioRepository.save(usuario);
    }

    /**
     * Caso de uso: Alterar senha do usuário
     */
    public void alterarSenha(UUID id, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    /**
     * Caso de uso: Desativar usuário
     */
    public void desativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.desativar();
        usuarioRepository.save(usuario);
    }

    /**
     * Caso de uso: Ativar usuário
     */
    public void ativarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.ativar();
        usuarioRepository.save(usuario);
    }

    /**
     * Caso de uso: Atualizar último acesso
     */
    public void atualizarUltimoAcesso(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.atualizarUltimoAcesso();
        usuarioRepository.save(usuario);
    }

    /**
     * Implementação do UserDetailsService para Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailAndAtivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}