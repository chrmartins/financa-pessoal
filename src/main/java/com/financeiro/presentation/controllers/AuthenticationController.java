package com.financeiro.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.infrastructure.config.SecurityProperties;
import com.financeiro.infrastructure.security.GoogleAuthService;
import com.financeiro.infrastructure.security.JwtService;
import com.financeiro.presentation.dto.auth.GoogleAuthRequest;
import com.financeiro.presentation.dto.auth.LoginRequest;
import com.financeiro.presentation.dto.auth.LoginResponse;
import com.financeiro.presentation.dto.auth.RefreshTokenRequest;
import com.financeiro.presentation.dto.usuario.UsuarioResponse;
import com.financeiro.repository.UsuarioRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

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
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final GoogleAuthService googleAuthService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            SecurityProperties securityProperties,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            GoogleAuthService googleAuthService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.securityProperties = securityProperties;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.googleAuthService = googleAuthService;
    }

    /**
     * Endpoint de login que retorna JWT token
     */
    @PostMapping
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha()));

            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Gera o token JWT
            String jwtToken = jwtService.generateToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);
            
            // Atualiza último acesso
            usuario.atualizarUltimoAcesso();
            usuarioRepository.save(usuario);

            LoginResponse response = LoginResponse.builder()
                    .usuario(UsuarioResponse.fromEntity(usuario))
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .expiresIn(securityProperties.getJwt().getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    /**
     * Endpoint para renovar o token usando o refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            // Extrai o email do refresh token
            String userEmail = jwtService.extractUsername(request.getRefreshToken());
            
            if (userEmail == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
            }

            // Carrega o usuário
            Usuario usuario = (Usuario) userDetailsService.loadUserByUsername(userEmail);
            
            // Valida o refresh token
            if (!jwtService.isTokenValid(request.getRefreshToken(), usuario)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado ou inválido");
            }

            // Gera novos tokens
            String newJwtToken = jwtService.generateToken(usuario);
            String newRefreshToken = jwtService.generateRefreshToken(usuario);

            LoginResponse response = LoginResponse.builder()
                    .usuario(UsuarioResponse.fromEntity(usuario))
                    .token(newJwtToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(securityProperties.getJwt().getExpirationTime())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erro ao renovar token: " + ex.getMessage());
        }
    }

    /**
     * Endpoint para autenticação com Google OAuth
     */
    @PostMapping("/google")
    public ResponseEntity<LoginResponse> authenticateWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            // Verifica o token do Google
            GoogleIdToken.Payload payload = googleAuthService.verifyToken(request.getToken());
            
            String email = payload.getEmail();
            String nome = (String) payload.get("name");
            String foto = (String) payload.get("picture");
            String googleId = payload.getSubject();
            
            // Busca ou cria o usuário
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .map(u -> {
                        // Atualiza a foto se mudou
                        if (foto != null && !foto.equals(u.getFoto())) {
                            u.setFoto(foto);
                        }
                        // Atualiza o googleId se não estava setado
                        if (u.getGoogleId() == null) {
                            u.setGoogleId(googleId);
                        }
                        u.atualizarUltimoAcesso();
                        return usuarioRepository.save(u);
                    })
                    .orElseGet(() -> {
                        // Cria novo usuário
                        Usuario novoUsuario = Usuario.builder()
                                .nome(nome)
                                .email(email)
                                .googleId(googleId)
                                .foto(foto)
                                .papel(Usuario.Papel.USER)
                                .ativo(true)
                                .build();
                        return usuarioRepository.save(novoUsuario);
                    });
            
            // Gera tokens JWT
            String jwtToken = jwtService.generateToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);
            
            LoginResponse response = LoginResponse.builder()
                    .usuario(UsuarioResponse.fromEntity(usuario))
                    .token(jwtToken)
                    .refreshToken(refreshToken)
                    .expiresIn(securityProperties.getJwt().getExpirationTime())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falha na autenticação com Google: " + ex.getMessage());
        }
    }
}
