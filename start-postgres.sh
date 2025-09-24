#!/bin/bash

echo "🐘 Iniciando PostgreSQL para desenvolvimento..."

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker daemon não está rodando!"
    echo ""
    echo "📋 Para iniciar o Docker:"
    echo "1. Abra o Docker Desktop"
    echo "2. Ou execute: open /Applications/Docker.app"
    echo ""
    echo "🔄 Aguarde o Docker inicializar e execute novamente:"
    echo "./start-postgres.sh"
    exit 1
fi

# Subir PostgreSQL
echo "🚀 Subindo PostgreSQL..."
docker compose up -d postgres

# Verificar se subiu
if [ $? -eq 0 ]; then
    echo "✅ PostgreSQL rodando em localhost:5432"
    echo "📊 pgAdmin disponível em http://localhost:5050"
    echo ""
    echo "🔧 Para conectar via pgAdmin:"
    echo "   Host: postgres"
    echo "   Database: financeiro_db"
    echo "   Username: financeiro_user"
    echo "   Password: financeiro_pass"
    echo ""
    echo "▶️  Agora execute: ./gradlew bootRun"
else
    echo "❌ Erro ao subir PostgreSQL"
    exit 1
fi