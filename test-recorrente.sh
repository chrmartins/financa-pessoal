#!/bin/bash

echo "🧪 Testando Transações Recorrentes"
echo "=================================="
echo ""

# 1. Login
echo "1️⃣ Fazendo login..."
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@financeiro.com","senha":"admin123"}' | jq -r '.token')

echo "✅ Token obtido!"
echo ""

# 2. Pegar uma categoria
echo "2️⃣ Buscando categorias..."
CATEGORIA_ID=$(curl -s -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer $TOKEN" | jq -r '.[0].id')

echo "✅ Categoria ID: $CATEGORIA_ID"
echo ""

# 3. Criar transação recorrente
echo "3️⃣ Criando transação recorrente com 5 parcelas..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/transacoes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aluguel Recorrente TESTE",
    "valor": 1500.00,
    "dataTransacao": "2025-10-12",
    "tipo": "DESPESA",
    "categoriaId": "'$CATEGORIA_ID'",
    "recorrente": true,
    "quantidadeParcelas": 5
  }')

echo "$RESPONSE" | jq .
echo ""

# 4. Listar todas as transações para verificar se foram criadas 5 parcelas
echo "4️⃣ Listando transações criadas..."
TRANSACOES=$(curl -s -X GET "http://localhost:8080/api/transacoes" \
  -H "Authorization: Bearer $TOKEN")

echo ""
echo "Total de transações: $(echo "$TRANSACOES" | jq '. | length')"
echo ""
echo "Transações recorrentes criadas:"
echo "$TRANSACOES" | jq '[.[] | select(.recorrente == true) | {descricao, dataTransacao, parcelaAtual, quantidadeParcelas, valor}]'

echo ""
echo "✅ Teste concluído!"
