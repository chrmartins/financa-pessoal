#!/bin/bash

# Script para configurar variáveis de ambiente no Railway
# Uso: ./railway_setup_vars.sh

echo "🚀 Configurando variáveis de ambiente no Railway..."

# Verificar se está logado
echo "📋 Verificando autenticação..."
railway whoami

echo ""
echo "📊 Status do projeto:"
railway status

echo ""
echo "🔧 Configurando variáveis de ambiente..."

# Configurar variáveis uma por uma
echo "Configurando JWT_SECRET..."
railway variables set JWT_SECRET="financa-pessoal-super-secret-jwt-key-2025-muito-seguro-para-producao"

echo "Configurando SPRING_PROFILES_ACTIVE..."
railway variables set SPRING_PROFILES_ACTIVE="prod"

echo "Configurando CORS_ALLOWED_ORIGINS..."
railway variables set CORS_ALLOWED_ORIGINS="http://localhost:5173"

echo "Configurando DATABASE_URL..."
railway variables set DATABASE_URL="postgresql://\${PGUSER}:\${PGPASSWORD}@\${PGHOST}:\${PGPORT}/\${PGDATABASE}"

echo "Configurando DATABASE_USERNAME..."
railway variables set DATABASE_USERNAME="\${PGUSER}"

echo "Configurando DATABASE_PASSWORD..."
railway variables set DATABASE_PASSWORD="\${PGPASSWORD}"

echo ""
echo "✅ Configuração concluída!"
echo ""
echo "📝 Para verificar as variáveis configuradas:"
echo "railway variables"
echo ""
echo "🚀 Para fazer deploy:"
echo "railway up"