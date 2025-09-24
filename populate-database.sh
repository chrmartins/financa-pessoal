#!/bin/bash

# Script para popular o banco de dados com dados de exemplo
echo "🔄 Populando o banco de dados com dados de exemplo..."

# Verificar se a aplicação está executando
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "❌ Aplicação não está executando. Inicie com ./gradlew bootRun"
    exit 1
fi

# Criar usuário de exemplo
echo "👤 Criando usuário..."
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com",
    "senha": "123456"
  }' > /dev/null 2>&1

# Criar categorias de receita
echo "💰 Criando categorias de receita..."
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Salário",
    "descricao": "Salário mensal",
    "tipo": "RECEITA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Freelance",
    "descricao": "Trabalhos extras",
    "tipo": "RECEITA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Investimentos",
    "descricao": "Rendimentos de investimentos",
    "tipo": "RECEITA"
  }' > /dev/null 2>&1

# Criar categorias de despesa
echo "💸 Criando categorias de despesa..."
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Alimentação",
    "descricao": "Gastos com comida",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Transporte",
    "descricao": "Gastos com transporte",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Moradia",
    "descricao": "Aluguel, condomínio, IPTU",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Saúde",
    "descricao": "Gastos com saúde",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Educação",
    "descricao": "Cursos, livros, estudos",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Lazer",
    "descricao": "Entretenimento, cinema, viagens",
    "tipo": "DESPESA"
  }' > /dev/null 2>&1

echo "✅ Banco de dados populado com sucesso!"
echo "📊 Verifique os dados em:"
echo "   http://localhost:8080/api/categorias"
echo "   http://localhost:8080/swagger-ui.html"