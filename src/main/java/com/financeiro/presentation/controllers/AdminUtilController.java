package com.financeiro.presentation.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.domain.entities.Usuario;
import com.financeiro.repository.UsuarioRepository;

/**
 * ENDPOINT TEMPORÁRIO APENAS PARA DESENVOLVIMENTO/TESTE
 * Permite resetar senha do admin sem acesso ao banco
 * REMOVER EM PRODUÇÃO!
 */
@RestController
@RequestMapping("/api/admin")
@Profile({"dev", "prod"}) // Permite apenas em dev e prod (temporariamente)
public class AdminUtilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Endpoint para resetar senha do admin
     * URL: POST /api/admin/reset-password
     * Body: { "email": "admin@financeiro.com", "novaSenha": "password" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String novaSenha = request.get("novaSenha");

        if (email == null || novaSenha == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "Email e novaSenha são obrigatórios",
                "exemplo", Map.of("email", "admin@financeiro.com", "novaSenha", "password")
            ));
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

        // Atualizar senha com BCrypt hash
        String senhaHasheada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaHasheada);
        usuario.setAtivo(true);
        usuario.setDataAtualizacao(LocalDateTime.now());
        
        usuarioRepository.save(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", true);
        response.put("mensagem", "Senha atualizada com sucesso");
        response.put("usuario", Map.of(
            "email", usuario.getEmail(),
            "ativo", usuario.getAtivo(),
            "senhaBcryptPreview", senhaHasheada.substring(0, 30) + "..."
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar estado do usuário
     * URL: GET /api/admin/check/{email}
     */
    @GetMapping("/check/{email}")
    public ResponseEntity<?> checkUser(@PathVariable String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

        Map<String, Object> response = new HashMap<>();
        response.put("email", usuario.getEmail());
        response.put("nome", usuario.getNome());
        response.put("ativo", usuario.getAtivo());
        response.put("papel", usuario.getPapel());
        response.put("senhaBcryptPreview", usuario.getSenha().substring(0, 30) + "...");
        response.put("ultimoAcesso", usuario.getUltimoAcesso());

        return ResponseEntity.ok(response);
    }
}
