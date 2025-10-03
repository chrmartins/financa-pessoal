package com.financeiro.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.infrastructure.config.SecurityProperties;
import com.financeiro.presentation.dto.auth.LoginRequest;
import com.financeiro.presentation.dto.auth.LoginResponse;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;
import com.financeiro.repository.UsuarioRepository;

import jakarta.validation.Valid;

/**
 * Endpoints responsáveis pela autenticação dos usuários.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final SecurityProperties securityProperties;

    public AuthenticationController(AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            SecurityProperties securityProperties) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.securityProperties = securityProperties;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

            Usuario usuario = (Usuario) authentication.getPrincipal();
            usuario.atualizarUltimoAcesso();
            usuarioRepository.save(usuario);

            LoginResponse response = LoginResponse.builder()
                    .usuario(UsuarioResponse.fromEntity(usuario))
                    .token(null)
                    .expiresIn(securityProperties.getJwt().getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }
}
