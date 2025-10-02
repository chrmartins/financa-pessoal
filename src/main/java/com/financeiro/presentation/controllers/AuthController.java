package com.financeiro.presentation.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;

/**
 * Controller para operações do usuário autenticado
 */
@RestController
@RequestMapping("/api/me")
public class AuthController {

    @GetMapping
    public ResponseEntity<UsuarioResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            UsuarioResponse response = UsuarioResponse.fromEntity(usuario);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/info")
    public ResponseEntity<String> getUserInfo(Principal principal) {
        if (principal != null) {
            return ResponseEntity.ok("Usuário logado: " + principal.getName());
        }
        return ResponseEntity.ok("Nenhum usuário logado");
    }
}