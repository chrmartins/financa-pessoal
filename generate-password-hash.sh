#!/bin/bash

# Script para gerar hash BCrypt de uma senha
# Uso: ./generate-password-hash.sh "sua_senha"

SENHA="${1:-admin123}"

cat > /tmp/GenerateHash.java << 'EOF'
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java GenerateHash <senha>");
            System.exit(1);
        }
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(args[0]);
        System.out.println("Senha: " + args[0]);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println();
        System.out.println("SQL para atualizar no banco:");
        System.out.println("UPDATE usuarios SET senha = '" + hash + "' WHERE email = 'admin@financeiro.com';");
    }
}
EOF

# Compilar e executar
cd /tmp
javac -cp ~/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-crypto/*/spring-security-crypto-*.jar GenerateHash.java 2>/dev/null

if [ $? -eq 0 ]; then
    java -cp .:~/.gradle/caches/modules-2/files-2.1/org.springframework.security/spring-security-crypto/*/spring-security-crypto-*.jar GenerateHash "$SENHA"
else
    echo "❌ Erro ao compilar. Tentando método alternativo..."
    echo ""
    echo "Execute este comando no terminal da aplicação Spring Boot:"
    echo ""
    echo "String hash = new BCryptPasswordEncoder().encode(\"$SENHA\");"
    echo "System.out.println(hash);"
fi
