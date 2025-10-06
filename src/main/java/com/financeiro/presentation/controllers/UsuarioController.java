package com.financeiro.presentation.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.usuario.AtualizarUsuarioRequest;
import com.financeiro.presentation.dto.usuario.CriarUsuarioRequest;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;
import com.financeiro.repository.UsuarioRepository;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de usuários.
 * Operações de criação, atualização e exclusão requerem papel ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Listar todos os usuários ativos
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(Usuario::getAtivo)
                .toList();
        
        List<UsuarioResponse> response = usuarios.stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obter usuário logado atual
     */
    @GetMapping("/atual")
    public ResponseEntity<UsuarioResponse> obterUsuarioAtual(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return usuarioRepository.findByEmail(principal.getName())
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable UUID id) {
        return usuarioRepository.findById(id)
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar usuário por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        return usuarioRepository.findByEmail(email)
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Criar novo usuário (apenas ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        // Verificar se email já existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Criar novo usuário com senha hash BCrypt
        Usuario novoUsuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .papel(request.getPapel())
                .ativo(request.getAtivo())
                .dataCriacao(LocalDateTime.now())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.fromEntity(usuarioSalvo));
    }

    /**
     * Atualizar usuário existente (apenas ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable UUID id, 
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Atualizar apenas campos não nulos
                    if (request.getNome() != null) {
                        usuario.setNome(request.getNome());
                    }
                    if (request.getEmail() != null) {
                        // Verificar se email já está em uso por outro usuário
                        usuarioRepository.findByEmail(request.getEmail())
                                .filter(u -> !u.getId().equals(id))
                                .ifPresent(u -> {
                                    throw new IllegalArgumentException("Email já está em uso");
                                });
                        usuario.setEmail(request.getEmail());
                    }
                    if (request.getSenha() != null) {
                        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
                    }
                    if (request.getPapel() != null) {
                        usuario.setPapel(request.getPapel());
                    }
                    if (request.getAtivo() != null) {
                        usuario.setAtivo(request.getAtivo());
                    }
                    
                    usuario.setDataAtualizacao(LocalDateTime.now());
                    Usuario usuarioAtualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioAtualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Desativar usuário (soft delete - apenas ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativarUsuario(@PathVariable UUID id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setAtivo(false);
                    usuario.setDataAtualizacao(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}