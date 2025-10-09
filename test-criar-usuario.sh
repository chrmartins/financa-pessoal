#!/bin/bash

# Script para testar criação de usuário com categorias padrão

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Teste de Criação de Usuário com Categorias Padrão ===${NC}\n"

# 1. Fazer login como admin
echo -e "${BLUE}1. Fazendo login como admin...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "admin123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo -e "${RED}❌ Erro ao fazer login. Verifique se a aplicação está rodando.${NC}"
  exit 1
fi

echo -e "${GREEN}✅ Login bem-sucedido${NC}\n"

# 2. Criar novo usuário
echo -e "${BLUE}2. Criando novo usuário de teste...${NC}"
EMAIL="teste_$(date +%s)@example.com"

CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Usuário Teste\",
    \"email\": \"$EMAIL\",
    \"senha\": \"senha123\",
    \"papel\": \"USER\"
  }")

USUARIO_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -z "$USUARIO_ID" ]; then
  echo -e "${RED}❌ Erro ao criar usuário${NC}"
  echo "Resposta: $CREATE_RESPONSE"
  exit 1
fi

echo -e "${GREEN}✅ Usuário criado: $EMAIL (ID: $USUARIO_ID)${NC}\n"

# 3. Fazer login com o novo usuário
echo -e "${BLUE}3. Fazendo login com o novo usuário...${NC}"
USER_LOGIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"senha\": \"senha123\"
  }")

USER_TOKEN=$(echo $USER_LOGIN | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -z "$USER_TOKEN" ]; then
  echo -e "${RED}❌ Erro ao fazer login com novo usuário${NC}"
  exit 1
fi

echo -e "${GREEN}✅ Login do novo usuário bem-sucedido${NC}\n"

# 4. Verificar categorias do usuário
echo -e "${BLUE}4. Verificando categorias do novo usuário...${NC}"
CATEGORIAS=$(curl -s -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer $USER_TOKEN")

NUM_CATEGORIAS=$(echo $CATEGORIAS | grep -o '"id"' | wc -l | tr -d ' ')

echo -e "\n${BLUE}Categorias encontradas: $NUM_CATEGORIAS${NC}\n"

if [ "$NUM_CATEGORIAS" -eq "8" ]; then
  echo -e "${GREEN}✅✅✅ SUCESSO! 8 categorias padrão foram criadas!${NC}\n"
  echo "$CATEGORIAS" | python3 -m json.tool 2>/dev/null || echo "$CATEGORIAS"
else
  echo -e "${RED}❌ ERRO! Esperado: 8 categorias, Encontrado: $NUM_CATEGORIAS${NC}\n"
  echo "Categorias: $CATEGORIAS"
  exit 1
fi

echo -e "\n${GREEN}=== Teste Concluído com Sucesso ===${NC}"
