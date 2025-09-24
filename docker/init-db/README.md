# Dados de Exemplo - Sistema Financeiro

Este diretório contém scripts SQL para popular o banco de dados PostgreSQL com dados de exemplo.

## 📊 Dados Inclusos

### 👥 Usuários (3)

- **João Silva** (`joao.silva@email.com`) - USER
- **Maria Santos** (`maria.santos@email.com`) - USER
- **Admin Sistema** (`admin@financeiro.com`) - ADMIN

**Senha padrão**: `password123` (hash BCrypt)

### 🏷️ Categorias (19 total)

#### 💰 Receitas (7)

- Salário
- Freelances
- Investimentos
- Vendas
- Aluguéis
- Prêmios
- Restituição

#### 💸 Despesas (12)

- Alimentação
- Transporte
- Moradia
- Saúde
- Educação
- Lazer
- Roupas
- Tecnologia
- Impostos
- Cartão
- Pets
- Limpeza

### 📈 Transações (40+ registros)

#### João Silva (Últimos 3 meses)

**Receitas**: ~R$ 24.535,50

- Salários: R$ 16.500,00
- Freelances: R$ 5.200,00
- Investimentos: R$ 85,50
- Outros: R$ 2.750,00

**Despesas**: ~R$ 15.870,30

- Moradia: R$ 3.600,00
- Alimentação: R$ 1.187,90
- Tecnologia: R$ 6.500,00
- Transporte: R$ 830,00
- Saúde: R$ 840,00
- Outros: R$ 2.912,40

**Saldo**: ~R$ 8.665,20

#### Maria Santos (Setembro 2024)

**Receitas**: R$ 4.800,00
**Despesas**: R$ 1.695,50  
**Saldo**: R$ 3.104,50

## 🚀 Como Usar

### 1. Subir PostgreSQL

```bash
docker compose up -d postgres
```

### 2. Executar a Aplicação

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### 3. Verificar Dados via API

```bash
# Listar categorias
curl http://localhost:8080/api/categorias

# Listar transações do João
curl http://localhost:8080/api/transacoes/usuario/550e8400-e29b-41d4-a716-446655440001

# Calcular saldo do João
curl http://localhost:8080/api/transacoes/usuario/550e8400-e29b-41d4-a716-446655440001/saldo

# Buscar por período (Setembro 2024)
curl "http://localhost:8080/api/transacoes/usuario/550e8400-e29b-41d4-a716-446655440001/periodo?dataInicio=2024-09-01&dataFim=2024-09-30"
```

### 4. Testar no Swagger

Acesse: `http://localhost:8080/swagger-ui/index.html`

### 5. Verificar no pgAdmin

- URL: `http://localhost:5050`
- Email: `admin@financeiro.com`
- Senha: `admin123`

## 🗂️ IDs Úteis

### Usuários

- João Silva: `550e8400-e29b-41d4-a716-446655440001`
- Maria Santos: `550e8400-e29b-41d4-a716-446655440002`

### Categorias Populares

- Salário: `650e8400-e29b-41d4-a716-446655440001`
- Alimentação: `750e8400-e29b-41d4-a716-446655440001`
- Moradia: `750e8400-e29b-41d4-a716-446655440003`
- Transporte: `750e8400-e29b-41d4-a716-446655440002`

## 📊 Cenários de Teste

1. **Relatório Mensal**: Buscar transações de setembro/2024
2. **Análise por Categoria**: Filtrar por "Alimentação" ou "Tecnologia"
3. **Balanço Geral**: Calcular saldo total dos usuários
4. **CRUD Completo**: Criar, editar e excluir transações
5. **Validações**: Testar com valores inválidos

Os dados representam um cenário realista de uso pessoal com transações variadas e categorias bem distribuídas.
