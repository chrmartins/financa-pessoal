# 💰 Sistema de Gestão Financeira Pessoal

Sistema desenvolvido com Spring Boot 3.3, Java 21 e Clean Architecture para gestão financeira pessoal.

## 🚀 **Deploy e Ambientes**

### 🌍 **Ambientes Disponíveis**

#### **Development (Local)**

- **Profile**: `dev`
- **Database**: H2 em memória
- **URL**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html

#### **Production (Railway)**

- **Profile**: `prod`
- **Database**: PostgreSQL
- **URL**: https://financa-pessoal-production.up.railway.app
- **Health Check**: /actuator/health

### 🔧 **Variáveis de Ambiente**

#### **Development**

```bash
SPRING_PROFILES_ACTIVE=dev
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
SWAGGER_ENABLED=true
```

#### **Production**

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://user:pass@host:port/db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
CORS_ALLOWED_ORIGINS=https://your-frontend.com
JWT_SECRET=your-super-secret-jwt-key
SWAGGER_ENABLED=false
```

## 🚀 Tecnologias

- **Java 21** (LTS)
- **Spring Boot 3.3.5**
- **Spring Security 6.2**
- **Spring Data JPA**
- **Gradle 8.10**
- **PostgreSQL** (produção)
- **H2** (desenvolvimento)
- **Docker & Docker Compose**
- **Lombok**

## 🏗️ Arquitetura

O projeto segue os princípios da **Clean Architecture**:

```
src/main/java/com/financeiro/
├── domain/           # Entidades de domínio e regras de negócio
├── application/      # Casos de uso e interfaces de repositórios
├── infrastructure/   # Implementações de repositórios e configurações
└── presentation/     # Controllers REST e DTOs
```

## 🐳 Docker Setup

### Pré-requisitos

- Docker
- Docker Compose

### Subir os serviços

```bash
# Subir PostgreSQL, pgAdmin e Redis
docker-compose up -d

# Verificar se os containers estão rodando
docker-compose ps
```

### Serviços disponíveis

- **PostgreSQL**: `localhost:5432`

  - Database: `financeiro_db`
  - User: `financeiro_user`
  - Password: `financeiro_pass`

- **pgAdmin**: `http://localhost:5050`

  - Email: `admin@financeiro.com`
  - Password: `admin123`

- **Redis**: `localhost:6379`

### Parar os serviços

```bash
docker-compose down
```

### Limpar dados (volumes)

```bash
docker-compose down -v
```

## 📊 Dados de Exemplo

O sistema inclui dados de exemplo pré-configurados:

- **3 usuários** (João, Maria, Admin)
- **19 categorias** (7 receitas + 12 despesas)
- **40+ transações** dos últimos 3 meses
- **Cenários realistas** para testes

Para usar os dados de exemplo:

```bash
# 1. Subir PostgreSQL (script auxiliar)
./start-postgres.sh

# 2. Executar aplicação (PostgreSQL é padrão)
./gradlew bootRun
```

**Usuários de teste**:

- João Silva: `joao.silva@email.com` (saldo: ~R$ 8.665)
- Maria Santos: `maria.santos@email.com` (saldo: ~R$ 3.104)

## 🏃‍♂️ Executando a Aplicação

## 🏃‍♂️ Executando a Aplicação

### Modo Desenvolvimento (PostgreSQL - Padrão)

```bash
# 1. Subir PostgreSQL
./start-postgres.sh

# 2. Executar aplicação
./gradlew bootRun
```

- **PostgreSQL**: `localhost:5432`
- **pgAdmin**: `http://localhost:5050`
- **Dados de exemplo**: Carregados automaticamente

### Modo H2 (Testes Rápidos)

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

- **Console H2**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:financeiro_db`

### Comandos Docker Manuais

```bash
# PostgreSQL
docker compose up -d postgres

# Todos os serviços
docker compose up -d
```

## 📡 API Endpoints

### Categorias

- `GET /api/categorias` - Listar categorias ativas
- `GET /api/categorias/{id}` - Buscar categoria por ID
- `GET /api/categorias/tipo/{tipo}` - Listar por tipo (RECEITA/DESPESA)
- `POST /api/categorias` - Criar categoria
- `PUT /api/categorias/{id}` - Atualizar categoria
- `PATCH /api/categorias/{id}/ativar` - Ativar categoria
- `PATCH /api/categorias/{id}/desativar` - Desativar categoria

### Transações

- `GET /api/transacoes/usuario/{usuarioId}` - Listar transações do usuário
- `GET /api/transacoes/usuario/{usuarioId}/periodo?dataInicio=2024-01-01&dataFim=2024-12-31` - Por período
- `GET /api/transacoes/usuario/{usuarioId}/saldo` - Calcular saldo
- `POST /api/transacoes?usuarioId={uuid}` - Criar transação
- `PUT /api/transacoes/{id}` - Atualizar transação
- `DELETE /api/transacoes/{id}` - Remover transação

## 🛠️ Desenvolvimento

### Build

```bash
./gradlew build
```

### Testes

```bash
./gradlew test
```

### Compilação

```bash
./gradlew compileJava
```

## 📝 Modelos de Dados

### Categoria

- **ID**: UUID
- **Nome**: String (2-50 chars)
- **Descrição**: String (máx 200 chars)
- **Tipo**: RECEITA | DESPESA
- **Ativa**: Boolean

### Transação

- **ID**: UUID
- **Descrição**: String (2-100 chars)
- **Valor**: BigDecimal (positivo)
- **Data**: LocalDate
- **Tipo**: RECEITA | DESPESA
- **Categoria**: UUID (referência)
- **Usuario**: UUID (referência)
- **Observações**: String (máx 500 chars)

### Usuário

- **ID**: UUID
- **Nome**: String (2-100 chars)
- **Email**: String (único)
- **Senha**: String (hash)
- **Papel**: USER | ADMIN
- **Ativo**: Boolean

## 🔒 Segurança

Durante o desenvolvimento, todas as rotas estão liberadas. Para produção, configure adequadamente o Spring Security.

## 📊 Funcionalidades

- ✅ Gestão de categorias financeiras
- ✅ Lançamento de receitas e despesas
- ✅ Cálculo de saldos
- ✅ Relatórios por período
- ✅ Busca por descrição
- ✅ Filtros por categoria e tipo
- ✅ API REST completa
- ✅ Validações de entrada
- ✅ Clean Architecture
- ✅ UUID como identificadores
- ✅ Multi-profile (H2/PostgreSQL)

## 🎯 Próximos Passos

- [ ] Implementar JWT Authentication
- [ ] Adicionar testes unitários e integração
- [ ] Criar dashboards/relatórios
- [ ] Implementar cache com Redis
- [ ] Adicionar métricas (Actuator)
- [ ] Frontend (React/Angular)
