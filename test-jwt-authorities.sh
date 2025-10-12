#!/bin/bash

echo "🔐 Testando geração de JWT com authorities..."
echo ""

# Fazer login com o admin
echo "1️⃣ Fazendo login com admin@financeiro.com..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "admin123"
  }')

echo "Response completa:"
echo "$RESPONSE" | jq .

# Extrair o token
TOKEN=$(echo "$RESPONSE" | jq -r '.token')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
  echo "❌ Erro: Não foi possível obter o token"
  exit 1
fi

echo ""
echo "✅ Token obtido com sucesso!"
echo ""
echo "2️⃣ Decodificando o payload do JWT..."
echo ""

# Extrair e decodificar o payload (segunda parte do JWT)
PAYLOAD=$(echo "$TOKEN" | cut -d'.' -f2)

# Adicionar padding se necessário para base64
while [ $((${#PAYLOAD} % 4)) -ne 0 ]; do
  PAYLOAD="${PAYLOAD}="
done

# Decodificar e formatar
echo "Payload decodificado:"
echo "$PAYLOAD" | base64 -d 2>/dev/null | jq .

echo ""
echo "3️⃣ Verificando se 'authorities' está presente no token..."
AUTHORITIES=$(echo "$PAYLOAD" | base64 -d 2>/dev/null | jq -r '.authorities')

if [ -z "$AUTHORITIES" ] || [ "$AUTHORITIES" == "null" ]; then
  echo "❌ ERRO: Campo 'authorities' NÃO encontrado no token!"
  exit 1
else
  echo "✅ SUCCESS: Campo 'authorities' encontrado no token:"
  echo "$AUTHORITIES" | jq .
fi

echo ""
echo "4️⃣ Testando acesso a endpoint protegido com o token..."
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN" | jq .

echo ""
echo "✅ Teste concluído com sucesso!"
