package com.financeiro.presentation.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.application.services.UsuarioService;
import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.usuario.AtualizarUsuarioRequest;
import com.financeiro.presentation.dto.usuario.CriarUsuarioRequest;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de usuários.
 * Operações de criação, atualização e exclusão requerem papel ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Listar todos os usuários ativos
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarAtivos();
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
        
        return usuarioService.buscarPorEmail(principal.getName())
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuario(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id)
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar usuário por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email)
                .filter(Usuario::getAtivo)
                .map(usuario -> ResponseEntity.ok(UsuarioResponse.fromEntity(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Criar novo usuário com categorias padrão (apenas ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        try {
            Usuario novoUsuario = usuarioService.criarUsuarioComCategoriasDefault(
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha(),
                    request.getPapel()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UsuarioResponse.fromEntity(novoUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Atualizar usuário existente (apenas ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable UUID id, 
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        
        try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(
                    id,
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha(),
                    request.getPapel()
            );
            return ResponseEntity.ok(UsuarioResponse.fromEntity(usuarioAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desativar usuário (soft delete - apenas ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desativarUsuario(@PathVariable UUID id) {
        try {
            usuarioService.desativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Deletar permanentemente um usuário e todos os seus dados (transações, categorias).
     * ATENÇÃO: Esta operação é irreversível e remove completamente o usuário do banco de dados.
     * Apenas usuários com papel ADMIN podem executar esta operação.
     * O usuário admin@financeiro.com não pode ser deletado.
     */
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletarUsuarioPermanentemente(@PathVariable UUID id) {
        try {
            usuarioService.deletarUsuarioPermanentemente(id);
            return ResponseEntity.ok()
                    .body("Usuário deletado permanentemente com sucesso. Todas as transações e categorias relacionadas foram removidas.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}