#!/bin/bash

echo "🚀 Testando Autenticação JWT"
echo "=============================="
echo ""

# 1. Login e captura do token
echo "1️⃣ Fazendo login..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@financeiro.com","senha":"admin123"}')

echo "Resposta do login:"
echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
echo ""

# Extrai o token JWT
TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('token', ''))" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "❌ Falha ao obter token JWT!"
  exit 1
fi

echo "✅ Token JWT obtido com sucesso!"
echo "Token: ${TOKEN:0:50}..."
echo ""

# 2. Testa endpoint protegido COM token
echo "2️⃣ Testando endpoint /api/categorias COM token..."
curl -s -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | python3 -m json.tool 2>/dev/null | head -30
echo ""

# 3. Testa endpoint protegido SEM token (deve dar 401/403)
echo "3️⃣ Testando endpoint /api/categorias SEM token (deve falhar)..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET http://localhost:8080/api/categorias)
echo "Status HTTP: $HTTP_CODE"
if [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
  echo "✅ Correto! Sem token retornou $HTTP_CODE"
else
  echo "⚠️  Inesperado: esperava 401 ou 403, recebeu $HTTP_CODE"
fi
echo ""

echo "=============================="
echo "✅ Testes concluídos!"
