#!/bin/bash

echo "🔍 Testando JWT com Authorities"
echo "================================"
echo ""

# Fazer login
echo "1️⃣ Fazendo login..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@financeiro.com","senha":"admin123"}')

TOKEN=$(echo "$RESPONSE" | jq -r '.token')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
  echo "❌ Erro ao obter token"
  exit 1
fi

echo "✅ Token obtido com sucesso!"
echo ""

# Decodificar payload
echo "2️⃣ Decodificando o payload do JWT..."
PAYLOAD=$(echo "$TOKEN" | cut -d'.' -f2)

# Adicionar padding
while [ $((${#PAYLOAD} % 4)) -ne 0 ]; do
  PAYLOAD="${PAYLOAD}="
done

echo ""
echo "📄 Payload completo do JWT:"
echo "----------------------------"
echo "$PAYLOAD" | base64 -d 2>/dev/null | jq .
echo ""

# Verificar authorities
echo "3️⃣ Verificando campo 'authorities'..."
AUTHORITIES=$(echo "$PAYLOAD" | base64 -d 2>/dev/null | jq -r '.authorities')

if [ -z "$AUTHORITIES" ] || [ "$AUTHORITIES" == "null" ]; then
  echo "❌ ERRO: Campo 'authorities' NÃO encontrado!"
  exit 1
else
  echo "✅ ✅ ✅ SUCCESS! Campo 'authorities' encontrado:"
  echo "$AUTHORITIES" | jq .
fi

echo ""
echo "4️⃣ Testando acesso a endpoint protegido..."
echo ""
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN" | jq '.[0] | {id, nome, email, papel}'

echo ""
echo "====================================="
echo "✅ Teste concluído com SUCESSO!"
echo "====================================="
