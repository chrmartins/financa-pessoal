#!/bin/bash
# Script para executar migração no banco de produção Railway

echo "🔍 Obtendo DATABASE_URL do Railway..."
export DATABASE_URL=$(railway variables --json | jq -r '.DATABASE_URL')

if [ -z "$DATABASE_URL" ]; then
    echo "❌ Erro: DATABASE_URL não encontrada"
    exit 1
fi

echo "✅ Conexão configurada"
echo ""
echo "📊 Executando migração..."
echo ""

psql "$DATABASE_URL" -f migration_categoria_usuario.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Migração executada com sucesso!"
    echo ""
    echo "🔍 Verificando resultado..."
    psql "$DATABASE_URL" -c "SELECT u.email, COUNT(c.id) as total_categorias FROM categorias c JOIN usuarios u ON u.id = c.usuario_id GROUP BY u.email ORDER BY total_categorias DESC;"
else
    echo ""
    echo "❌ Erro na migração. Verifique os logs acima."
    exit 1
fi
