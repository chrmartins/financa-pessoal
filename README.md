# 💰## 📚 Documentação

### 🎯 Início Rápido

**📑 COMECE AQUI:** [DOCS_MASTER_INDEX.md](./DOCS_MASTER_INDEX.md) - Índice completo de TODA documentação

**Para Time de Frontend:**

- **[FRONTEND_README.md](./FRONTEND_README.md)** - 🎨 README específico do frontend (TL;DR)
- **[EXECUTIVE_SUMMARY.md](./EXECUTIVE_SUMMARY.md)** - 📊 Visão executiva do projeto
- **[FRONTEND_QUICKSTART.md](./FRONTEND_QUICKSTART.md)** - ⚡ Setup em 5 minutos
- **[REACT_EXAMPLE.md](./REACT_EXAMPLE.md)** - 💻 Código completo React

**Guias Detalhados:**

- **[FRONTEND_DOCS_INDEX.md](./FRONTEND_DOCS_INDEX.md)** - 📑 Índice dos guias frontend
- **[FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)** - 🔧 Configuração completa DEV/PROD
- **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** - 📖 Referência completa da API
- **[FRONTEND_CHECKLIST.md](./FRONTEND_CHECKLIST.md)** - ✅ Checklist de integração

**Arquitetura:**

- **[ENVIRONMENTS.md](./ENVIRONMENTS.md)** - 🌍 Visão geral dos ambientes DEV e PROD
- **[ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md)** - 📊 Diagramas e fluxos do sistema

**Backend/Deploy:**

- **[RAILWAY_DEPLOY_GUIDE.md](./RAILWAY_DEPLOY_GUIDE.md)** - 🚀 Como fazer deploy no Railwayma de Gestão Financeira Pessoal

Sistema desenvolvido com Spring Boot 3.3, Java 21 e Clean Architecture para gestão financeira pessoal.

## � Documentação

- **[ENVIRONMENTS.md](./ENVIRONMENTS.md)** - Visão geral completa dos ambientes DEV e PROD
- **[FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)** - Guia de configuração do frontend para ambos ambientes
- **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** - Documentação detalhada da API (endpoints, exemplos, autenticação)
- **[RAILWAY_DEPLOY_GUIDE.md](./RAILWAY_DEPLOY_GUIDE.md)** - Como fazer deploy no Railway

## �🚀 **Deploy e Ambientes**

### 🌍 **Ambientes Disponíveis**

#### **Development (Local)**

- **Profile**: `dev`
- **Database**: PostgreSQL (Docker) + H2 (opcional)
- **URL Backend**: http://localhost:8080
- **URL Frontend**: http://localhost:3000 (configurar)
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Credenciais**: admin@financeiro.com / password

#### **Production (Railway)**

- **Profile**: `prod`
- **Database**: PostgreSQL Railway
- **URL Backend**: https://financa-pessoal-production.up.railway.app
- **URL Frontend**: 🔧 A configurar (Vercel/Netlify)
- **Swagger**: https://financa-pessoal-production.up.railway.app/swagger-ui.html
- **Health Check**: /actuator/health
- **Credenciais**: admin@financeiro.com / password

##### Checklist rápido de deploy

1. Provisionar o serviço PostgreSQL no projeto (template oficial) e garantir a variável `DATABASE_URL`.
2. No serviço backend, definir as variáveis:

- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET=<mínimo 32 caracteres>`
- `CORS_ALLOWED_ORIGINS`, `SWAGGER_ENABLED`, `JWT_EXPIRATION` conforme necessidade.

3. Confirmar que o deploy utiliza o `Dockerfile` da raiz (multi-stage com Temurin 21).
4. Realizar o deploy via GitHub ou CLI `railway up`. Healthcheck configurado em `/actuator/health`.
5. Monitorar os logs: a mensagem `DATABASE_URL detectado` indica conexão ao Postgres fornecido.

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
# Railway/Heroku style URL (será convertido automaticamente para JDBC)
DATABASE_URL=postgresql://user:pass@host:port/db
# (Opcional) Defina explicitamente se preferir manter credenciais separadas
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
# (Opcional) Use DATABASE_JDBC_URL se já possuir a URL no formato jdbc:postgresql://...
# DATABASE_JDBC_URL=jdbc:postgresql://host:port/db
CORS_ALLOWED_ORIGINS=https://your-frontend.com
JWT_SECRET=your-super-secret-jwt-key
SWAGGER_ENABLED=false
```

> 💡 **Railway**: ao usar o plugin de PostgreSQL, a plataforma já fornece a variável `DATABASE_URL`. O aplicativo converte esse valor automaticamente para o formato aceito pelo driver JDBC e extrai usuário/senha, dispensando ajustes adicionais.

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

Quando a aplicação inicia com um banco vazio, uma rotina de bootstrap garante um mínimo de dados para navegação:

- **Usuário administrador padrão**
  - Email: `admin@financeiro.com`
  - Senha: `admin123`
  - Papel: `ADMIN`
- **Categorias iniciais**: 12 itens (5 receitas + 7 despesas) cobrindo os principais grupos financeiros
- **Transações de exemplo**: 1 receita e 1 despesa associadas ao usuário administrador

> 🔐 As senhas são armazenadas com BCrypt. Ao alterar a senha padrão, a aplicação persistirá o novo hash automaticamente.

Para utilizar os dados de exemplo em ambiente local:

```bash
# 1. Subir PostgreSQL (script auxiliar)
./start-postgres.sh

# 2. Executar aplicação (PostgreSQL é padrão)
./gradlew bootRun
```

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

### Autenticação

- `POST /api/auth` - Autentica com `email` e `senha`, retornando os dados do usuário e espaço reservado para token JWT

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

### ✅ Concluído

- [x] Implementar JWT Authentication
- [x] API REST completa com validações
- [x] Clean Architecture
- [x] Deploy automático no Railway
- [x] Configuração de ambientes DEV/PROD
- [x] Documentação completa para frontend

### 🔧 Configurar Agora

- [ ] **Configure o frontend seguindo [FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)**
- [ ] Teste o ambiente DEV completo (Backend local + Frontend local)
- [ ] Teste o ambiente PROD (Backend Railway + Frontend local/hospedado)

### 📋 Roadmap Futuro

- [ ] Hospedar frontend (Vercel/Netlify)
- [ ] Adicionar testes E2E
- [ ] Implementar cache com Redis
- [ ] Criar dashboards/relatórios avançados
- [ ] Adicionar métricas e monitoramento
- [ ] CI/CD para frontend

---

## 🆘 Suporte e Troubleshooting

### Backend

- **Logs DEV**: Terminal local onde rodou `./gradlew bootRun`
- **Logs PROD**: Railway Dashboard → Service → Deployments → View Logs
- **Health**: `/actuator/health` (DEV e PROD)

### Frontend

- Veja guias em [FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)
- Troubleshooting CORS, autenticação e variáveis de ambiente

### Comandos Úteis

```bash
# Backend local
./gradlew bootRun --args='--spring.profiles.active=dev'

# Railway CLI
railway logs
railway status

# Database local
docker exec -it financa-postgres psql -U postgres -d financeiro

# Exportar dados
docker exec financa-postgres pg_dump -U postgres -d financeiro > backup.sql
```

---

**Última Atualização:** 04 de Outubro de 2025  
**Versão:** 1.0.0  
**Ambiente PROD:** https://financa-pessoal-production.up.railway.app
