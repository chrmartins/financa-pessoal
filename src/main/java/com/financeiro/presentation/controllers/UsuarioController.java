package com.financeiro.presentation.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;
import com.financeiro.repository.UsuarioRepository;

/**
 * Controller REST para gerenciamento de usuários.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
}