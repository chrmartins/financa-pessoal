# 🏗️ Arquitetura Visual - Sistema Financeiro Pessoal

## 🌍 Visão Geral dos Ambientes

```
╔════════════════════════════════════════════════════════════════════════╗
║                        AMBIENTE DE DESENVOLVIMENTO                     ║
╚════════════════════════════════════════════════════════════════════════╝

    ┌─────────────────────┐         ┌──────────────────────────┐
    │   Frontend Local    │         │    Backend Local         │
    │  localhost:3000     │────────▶│   localhost:8080         │
    │                     │         │   (Spring Boot)          │
    │  React/Vue/Angular  │         │   Profile: dev           │
    └─────────────────────┘         └──────────────────────────┘
                                                │
                                                │ JDBC
                                                ▼
                                    ┌──────────────────────────┐
                                    │  PostgreSQL Local        │
                                    │  localhost:5432          │
                                    │  (Docker Container)      │
                                    │                          │
                                    │  • 3 usuários            │
                                    │  • 11 categorias         │
                                    │  • 30 transações         │
                                    └──────────────────────────┘

    Arquivos de Configuração:
    • .env.development (Frontend)
    • application-dev.yml (Backend)
    • docker-compose.yml (Database)


╔════════════════════════════════════════════════════════════════════════╗
║                         AMBIENTE DE PRODUÇÃO                           ║
╚════════════════════════════════════════════════════════════════════════╝

    ┌─────────────────────┐         ┌──────────────────────────────────┐
    │   Frontend          │         │    Backend Railway               │
    │  (Vercel/Netlify)   │────────▶│   financa-pessoal-production     │
    │  🔧 A configurar    │         │   .up.railway.app                │
    │                     │         │   (Spring Boot)                  │
    │  React/Vue/Angular  │         │   Profile: prod                  │
    └─────────────────────┘         └──────────────────────────────────┘
                                                │
                                                │ DATABASE_URL
                                                ▼
                                    ┌──────────────────────────────────┐
                                    │  PostgreSQL Railway              │
                                    │  (Managed Service)               │
                                    │                                  │
                                    │  • Mesmos dados iniciais         │
                                    │  • Backup automático             │
                                    │  • Alta disponibilidade          │
                                    └──────────────────────────────────┘

    Arquivos de Configuração:
    • .env.production (Frontend)
    • Railway Variables (Backend)
    • Railway Postgres (Database)
```

---

## 🔄 Fluxo de Autenticação

```
┌─────────────┐                           ┌─────────────┐
│  Frontend   │                           │   Backend   │
└──────┬──────┘                           └──────┬──────┘
       │                                         │
       │  1. POST /api/auth                      │
       │     { email, senha }                    │
       │────────────────────────────────────────▶│
       │                                         │
       │                                         │  2. Validação
       │                                         │     BCrypt
       │                                         │
       │  3. Response                            │
       │     { usuario, token: null }            │
       │◀────────────────────────────────────────│
       │                                         │
       │  4. Salvar credenciais                  │
       │     localStorage/sessionStorage         │
       │                                         │
       │  5. Próximas requisições                │
       │     Header: Authorization Basic ...     │
       │────────────────────────────────────────▶│
       │                                         │
       │  6. Response com dados                  │
       │◀────────────────────────────────────────│
       │                                         │
```

**Nota:** Atualmente usa Basic Auth. JWT será implementado no futuro.

---

## 📡 Endpoints Principais

```
┌──────────────────────────────────────────────────────────────────┐
│                         API REST                                 │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Autenticação                                                    │
│  POST   /api/auth              Login com email/senha             │
│                                                                  │
│  Usuários                                                        │
│  GET    /api/usuarios          Listar todos                      │
│  GET    /api/usuarios/{id}     Buscar por ID                     │
│  POST   /api/usuarios          Criar novo                        │
│                                                                  │
│  Categorias                                                      │
│  GET    /api/categorias        Listar todas                      │
│  GET    /api/categorias?tipo=RECEITA  Filtrar por tipo           │
│  GET    /api/categorias/{id}   Buscar por ID                     │
│  POST   /api/categorias        Criar nova                        │
│  PUT    /api/categorias/{id}   Atualizar                         │
│  PATCH  /api/categorias/{id}/ativar    Ativar                    │
│  PATCH  /api/categorias/{id}/desativar Desativar                 │
│  DELETE /api/categorias/{id}   Deletar                           │
│                                                                  │
│  Transações                                                      │
│  GET    /api/transacoes        Listar todas                      │
│  GET    /api/transacoes?tipo=RECEITA   Filtrar por tipo          │
│  GET    /api/transacoes?dataInicio=...&dataFim=... Por período   │
│  GET    /api/transacoes/{id}   Buscar por ID                     │
│  POST   /api/transacoes        Criar nova                        │
│  PUT    /api/transacoes/{id}   Atualizar                         │
│  DELETE /api/transacoes/{id}   Deletar                           │
│                                                                  │
│  Relatórios                                                      │
│  GET    /api/transacoes/resumo-mensal?ano=2025&mes=8             │
│         → { totalReceitas, totalDespesas, saldo, ... }           │
│                                                                  │
│  Utilidades                                                      │
│  GET    /actuator/health       Status da aplicação               │
│  GET    /swagger-ui.html       Documentação interativa           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🗄️ Modelo de Dados

```
┌─────────────────────────────────────────────────────────────────┐
│                         USUÁRIOS                                │
├─────────────────────────────────────────────────────────────────┤
│  id              UUID (PK)                                      │
│  nome            VARCHAR(100)                                   │
│  email           VARCHAR(255) UNIQUE                            │
│  senha           VARCHAR(255) - BCrypt hash                     │
│  papel           ENUM(USER, ADMIN)                              │
│  ativo           BOOLEAN                                        │
│  dataCriacao     TIMESTAMP                                      │
│  ultimoAcesso    TIMESTAMP                                      │
└─────────────────────────────────────────────────────────────────┘
                    │
                    │ 1:N
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                        TRANSAÇÕES                               │
├─────────────────────────────────────────────────────────────────┤
│  id              UUID (PK)                                      │
│  descricao       VARCHAR(200)                                   │
│  valor           DECIMAL(15,2)                                  │
│  tipo            ENUM(RECEITA, DESPESA)                         │
│  dataTransacao   DATE                                           │
│  usuarioId       UUID (FK) → USUÁRIOS                           │
│  categoriaId     UUID (FK) → CATEGORIAS                         │
│  dataCriacao     TIMESTAMP                                      │
└─────────────────────────────────────────────────────────────────┘
                    │
                    │ N:1
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                        CATEGORIAS                               │
├─────────────────────────────────────────────────────────────────┤
│  id              UUID (PK)                                      │
│  nome            VARCHAR(50)                                    │
│  descricao       VARCHAR(200)                                   │
│  tipo            ENUM(RECEITA, DESPESA)                         │
│  ativa           BOOLEAN                                        │
│  dataCriacao     TIMESTAMP                                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔧 Configuração do Frontend

### Estrutura de Arquivos Recomendada

```
seu-projeto-frontend/
├── .env.development           # Variáveis DEV
├── .env.production            # Variáveis PROD
├── .env.local                 # Sobrescreve local (não commitar)
├── .gitignore                 # Incluir .env.local
├── src/
│   ├── config/
│   │   └── api.js            # Configuração Axios/Fetch
│   ├── services/
│   │   ├── categoriaService.js
│   │   ├── transacaoService.js
│   │   └── usuarioService.js
│   ├── components/
│   │   ├── Categorias/
│   │   ├── Transacoes/
│   │   └── EnvironmentBadge.jsx  # Indicador DEV/PROD
│   └── App.js
└── package.json
```

### Exemplo de Configuração

**`.env.development`**

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=development
```

**`.env.production`**

```env
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=production
```

**`src/config/api.js`**

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
});

api.interceptors.request.use((config) => {
  const username = process.env.REACT_APP_API_USER;
  const password = process.env.REACT_APP_API_PASSWORD;
  const credentials = btoa(`${username}:${password}`);
  config.headers.Authorization = `Basic ${credentials}`;
  return config;
});

export default api;
```

---

## 🚀 Deploy Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    WORKFLOW DE DESENVOLVIMENTO                  │
└─────────────────────────────────────────────────────────────────┘

1. DESENVOLVIMENTO LOCAL
   ├─ Código no branch 'feature/nova-funcionalidade'
   ├─ Backend local rodando (./gradlew bootRun)
   ├─ PostgreSQL local (docker-compose up)
   └─ Frontend local (npm start)
        │
        │ Testes locais OK
        ▼
2. COMMIT E PUSH
   ├─ git add .
   ├─ git commit -m "feat: nova funcionalidade"
   └─ git push origin feature/nova-funcionalidade
        │
        │ Pull Request → main
        ▼
3. MERGE TO MAIN
   ├─ Code review aprovado
   └─ Merge para main
        │
        │ Webhook do Railway
        ▼
4. DEPLOY AUTOMÁTICO (Railway)
   ├─ Detecta push em main
   ├─ Executa Dockerfile
   ├─ Build da aplicação
   ├─ Deploy no Railway
   └─ Healthcheck /actuator/health
        │
        │ Deploy com sucesso
        ▼
5. TESTE EM PRODUÇÃO
   ├─ Frontend local aponta para Railway
   ├─ Validação de funcionalidades
   └─ Monitoramento de logs
        │
        │ Tudo funcionando
        ▼
6. DEPLOY FRONTEND (Futuro)
   └─ Hospedar em Vercel/Netlify
```

---

## 📊 Comparação de Ambientes

```
┌────────────────┬─────────────────────┬──────────────────────────┐
│    Aspecto     │         DEV         │           PROD           │
├────────────────┼─────────────────────┼──────────────────────────┤
│ Backend URL    │ localhost:8080      │ Railway (HTTPS)          │
│ Database       │ Docker PostgreSQL   │ Railway PostgreSQL       │
│ Profile        │ dev                 │ prod                     │
│ CORS           │ localhost:3000      │ Domínio do frontend      │
│ Logs           │ Terminal local      │ Railway Dashboard        │
│ Deploy         │ Manual              │ Automático (Git push)    │
│ Hot Reload     │ Sim                 │ Não                      │
│ Debugging      │ IDE + DevTools      │ Logs remotos             │
│ SSL/HTTPS      │ Não                 │ Sim (Railway)            │
│ Monitoramento  │ Local               │ Railway Metrics          │
└────────────────┴─────────────────────┴──────────────────────────┘
```

---

## 🎯 Próximos Passos

### Imediato (Faça agora)

```
1. ✅ Configure .env no frontend
2. ✅ Implemente cliente HTTP
3. ✅ Teste em DEV
4. ✅ Teste em PROD
```

### Curto Prazo

```
5. ⚠️ Hospede frontend
6. ⚠️ Configure CI/CD frontend
7. ⚠️ Implemente JWT
```

### Longo Prazo

```
8. ⚠️ Testes E2E
9. ⚠️ Monitoramento
10. ⚠️ Analytics
```

---

**Última Atualização:** 04 de Outubro de 2025  
**Status:** ✅ Backend DEV e PROD operacionais
