package com.financeiro.infrastructure.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente temporário para gerar hash de senha admin
 * Executar e depois remover/comentar
 */
@Component
public class PasswordHashGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=====================================");
        System.out.println("🔐 HASHES DE SENHA GERADOS:");
        System.out.println("=====================================");
        
        // Hash para "password" (senha de produção)
        String hashPassword = encoder.encode("password");
        System.out.println("Senha 'password': " + hashPassword);
        
        // Hash para "admin123" (senha de desenvolvimento)
        String hashAdmin123 = encoder.encode("admin123");
        System.out.println("Senha 'admin123': " + hashAdmin123);
        
        System.out.println("=====================================");
        System.out.println("📝 SQL para atualizar no banco:");
        System.out.println("=====================================");
        System.out.println("-- Para produção (senha: password):");
        System.out.println("UPDATE usuarios SET senha = '" + hashPassword + "', ativo = true WHERE email = 'admin@financeiro.com';");
        System.out.println("");
        System.out.println("-- Para desenvolvimento (senha: admin123):");
        System.out.println("UPDATE usuarios SET senha = '" + hashAdmin123 + "', ativo = true WHERE email = 'admin@financeiro.com';");
        System.out.println("=====================================");
    }
}
