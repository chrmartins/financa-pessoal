package com.financeiro.presentation.controllers;

import java.security.Principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financeiro.infrastructure.config.CustomUserDetailsService;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public TestController(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth")
    public String testAuth(Principal principal) {
        if (principal != null) {
            return "Autenticado como: " + principal.getName();
        }
        return "Não autenticado";
    }

    @GetMapping("/user")
    public String testUser(@RequestParam String email) {
        try {
            UserDetails user = userDetailsService.loadUserByUsername(email);
            return "Usuário encontrado: " + user.getUsername() + ", senha: " + user.getPassword().substring(0, 10) + "..., ativo: " + user.isEnabled();
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    @GetMapping("/password")
    public String testPassword(@RequestParam String raw, @RequestParam String encoded) {
        boolean matches = passwordEncoder.matches(raw, encoded);
        return "Senha confere: " + matches;
    }

    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}