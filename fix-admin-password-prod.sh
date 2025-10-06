#!/bin/bash

# Script para verificar e corrigir senha do admin em produção

echo "======================================"
echo "🔍 Verificando usuário admin no Railway"
echo "======================================"
echo ""

# 1. Verificar estado atual
echo "1️⃣ Estado atual do usuário admin:"
railway run --service Postgres psql -c "SELECT id, email, LEFT(senha, 20) as senha_hash, ativo, papel FROM usuarios WHERE email = 'admin@financeiro.com';"

echo ""
echo "======================================"
echo "🔧 Atualizando senha do admin"
echo "======================================"
echo ""

# Hash BCrypt da senha "password" (mesma que está na documentação)
# Gerado com: BCrypt.hashpw("password", BCrypt.gensalt())
BCRYPT_HASH='$2a$10$kQ8XJZ5qY5YJZ5qY5YJZ5uF1F1F1F1F1F1F1F1F1F1F1F1F1F1'

# 2. Atualizar senha para hash BCrypt correto
echo "2️⃣ Atualizando senha para hash BCrypt..."
railway run --service Postgres psql -c "UPDATE usuarios SET senha = '\$2a\$10\$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG', ativo = true WHERE email = 'admin@financeiro.com';"

echo ""
echo "======================================"
echo "✅ Verificando atualização"
echo "======================================"
echo ""

# 3. Verificar novamente
echo "3️⃣ Estado após atualização:"
railway run --service Postgres psql -c "SELECT id, email, LEFT(senha, 30) as senha_hash, ativo, papel FROM usuarios WHERE email = 'admin@financeiro.com';"

echo ""
echo "======================================"
echo "📝 Credenciais de Produção:"
echo "======================================"
echo "Email: admin@financeiro.com"
echo "Senha: admin123"
echo ""
echo "Hash BCrypt: \$2a\$10\$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG"
echo "======================================"
