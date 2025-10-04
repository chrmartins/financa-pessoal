# Arquitetura de Ambientes - Sistema Financeiro Pessoal

## 🌍 Visão Geral dos Ambientes

Este projeto possui **dois ambientes completos** para desenvolvimento e produção:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         AMBIENTE DEV                                │
│                                                                     │
│  Frontend (localhost:3000) ──▶ Backend (localhost:8080)            │
│                                     │                               │
│                                     ▼                               │
│                          PostgreSQL (Docker)                        │
│                          localhost:5432                             │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                        AMBIENTE PROD                                │
│                                                                     │
│  Frontend (hospedado) ──▶ Backend Railway                          │
│                          financa-pessoal-production.up.railway.app  │
│                                     │                               │
│                                     ▼                               │
│                          PostgreSQL Railway                         │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📍 URLs e Endpoints

### Ambiente de Desenvolvimento (DEV)

| Componente       | URL                                   | Status                           |
| ---------------- | ------------------------------------- | -------------------------------- |
| **Backend API**  | http://localhost:8080                 | ✅ Rodando localmente            |
| **Swagger UI**   | http://localhost:8080/swagger-ui.html | ✅ Disponível                    |
| **Health Check** | http://localhost:8080/actuator/health | ✅ Disponível                    |
| **PostgreSQL**   | localhost:5432                        | ✅ Docker container              |
| **Frontend**     | http://localhost:3000                 | 🔧 Configure no projeto frontend |

### Ambiente de Produção (PROD)

| Componente       | URL                                                               | Status                     |
| ---------------- | ----------------------------------------------------------------- | -------------------------- |
| **Backend API**  | https://financa-pessoal-production.up.railway.app                 | ✅ Hospedado no Railway    |
| **Swagger UI**   | https://financa-pessoal-production.up.railway.app/swagger-ui.html | ✅ Disponível              |
| **Health Check** | https://financa-pessoal-production.up.railway.app/actuator/health | ✅ Disponível              |
| **PostgreSQL**   | Railway internal                                                  | ✅ Gerenciado pelo Railway |
| **Frontend**     | 🔧 A configurar                                                   | Vercel/Netlify/etc         |

---

## 🔐 Credenciais

### Desenvolvimento

```
Email: admin@financeiro.com
Senha: password

Outros usuários:
- joao.silva@email.com / password
- maria.santos@email.com / password
```

### Produção

```
Email: admin@financeiro.com
Senha: password

Outros usuários:
- joao.silva@email.com / password
- maria.santos@email.com / password
```

---

## 🚀 Como Usar

### 1. Iniciar Ambiente DEV (Backend)

```bash
# Terminal 1: Iniciar PostgreSQL local
cd /Users/chrmartins/projetos/financa-pessoal
docker-compose up postgres

# Terminal 2: Iniciar backend local
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Verificar:**

- Backend: http://localhost:8080/actuator/health
- Swagger: http://localhost:8080/swagger-ui.html

---

### 2. Configurar Frontend para DEV

**Criar arquivo `.env.development` no projeto frontend:**

```env
# React
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=development

# Vue/Vite
VITE_API_URL=http://localhost:8080
VITE_API_USER=admin@financeiro.com
VITE_API_PASSWORD=password

# Angular - use src/environments/environment.ts
```

**Iniciar frontend:**

```bash
cd seu-projeto-frontend
npm install
npm start  # ou npm run dev para Vue/Vite
```

---

### 3. Configurar Frontend para PROD

**Criar arquivo `.env.production` no projeto frontend:**

```env
# React
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=production

# Vue/Vite
VITE_API_URL=https://financa-pessoal-production.up.railway.app
VITE_API_USER=admin@financeiro.com
VITE_API_PASSWORD=password
```

**Testar localmente apontando para produção:**

```bash
cd seu-projeto-frontend
npm run build  # Build de produção
npm run start:prod  # Ou servir o build localmente
```

---

## 📊 Dados nos Ambientes

### DEV (Desenvolvimento)

- **3 usuários** de teste
- **11 categorias** (receitas e despesas)
- **30 transações** de exemplo (Agosto e Setembro 2025)
- Banco de dados local totalmente isolado

### PROD (Produção)

- **Mesmos dados iniciais** (migrados do DEV)
- Banco de dados Railway
- Dados persistentes e sincronizados

---

## 🔄 Workflow de Desenvolvimento

### 1. Desenvolvendo Novas Features

```bash
# 1. Trabalhe no ambiente DEV
# Backend local + Frontend local + PostgreSQL local

# 2. Teste localmente
# Valide todas as funcionalidades

# 3. Commit e Push
git add .
git commit -m "feat: nova funcionalidade"
git push origin main

# 4. Deploy Automático
# Railway detecta o push e faz deploy automático do backend

# 5. Teste em PROD
# Aponte o frontend local para PROD e valide
```

---

### 2. Sincronizando Dados DEV → PROD

Se criar novos dados em DEV e quiser em PROD:

```bash
# Exportar dados do PostgreSQL local
docker exec financa-postgres pg_dump \
  -U postgres -d financeiro \
  --data-only --inserts --column-inserts \
  > /tmp/novos_dados.sql

# Importar para Railway
docker exec -i financa-postgres psql \
  "postgresql://postgres:SENHA@crossover.proxy.rlwy.net:41546/railway" \
  < /tmp/novos_dados.sql
```

---

## 🛠️ Ferramentas e Comandos

### Backend

```bash
# Rodar em DEV
./gradlew bootRun --args='--spring.profiles.active=dev'

# Build para produção
./gradlew clean bootJar

# Ver logs do Railway
railway logs

# Status do deploy
railway status
```

### Banco de Dados

```bash
# Conectar ao PostgreSQL local
docker exec -it financa-postgres psql -U postgres -d financeiro

# Ver tabelas
\dt

# Ver dados de usuários
SELECT * FROM usuarios;

# Exportar dados
docker exec financa-postgres pg_dump -U postgres -d financeiro > backup.sql
```

### Frontend

```bash
# Instalar dependências
npm install

# DEV (backend local)
npm start

# PROD (backend Railway)
npm run build
npm run start:prod

# Verificar variáveis de ambiente
echo $REACT_APP_API_URL
```

---

## 📋 Checklist de Setup do Frontend

- [ ] Clone o repositório do frontend
- [ ] Crie `.env.development` com URL do backend local
- [ ] Crie `.env.production` com URL do Railway
- [ ] Adicione `.env.local` ao `.gitignore`
- [ ] Configure o cliente HTTP (Axios ou Fetch)
- [ ] Implemente os serviços (categorias, transações, usuários)
- [ ] Configure interceptors de autenticação
- [ ] Teste login em DEV
- [ ] Teste CRUD de transações em DEV
- [ ] Teste ambiente PROD
- [ ] Valide CORS (requisições do navegador)

---

## 🐛 Troubleshooting Comum

### ❌ Erro CORS no navegador

```
Access to fetch blocked by CORS policy
```

**Solução:** Já está configurado no backend! Verifique se o frontend está enviando credenciais:

```javascript
// Axios
api.defaults.withCredentials = true;

// Fetch
fetch(url, { credentials: "include" });
```

---

### ❌ 401 Unauthorized

**Causas:**

1. Credenciais erradas no `.env`
2. Header Authorization não está sendo enviado
3. PasswordEncoder incompatível

**Teste com cURL:**

```bash
curl -u "admin@financeiro.com:password" \
  http://localhost:8080/api/categorias
```

---

### ❌ Variáveis de ambiente não carregam

**React:**

- Reinicie o servidor após alterar `.env`
- Nomes devem começar com `REACT_APP_`

**Vue/Vite:**

- Reinicie o dev server
- Nomes devem começar com `VITE_`

---

## 📚 Documentação Completa

- **[FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)** - Guia detalhado de configuração de ambientes
- **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** - Documentação completa da API
- **[README.md](./README.md)** - Documentação principal do projeto
- **[RAILWAY_DEPLOY_GUIDE.md](./RAILWAY_DEPLOY_GUIDE.md)** - Como fazer deploy no Railway

---

## 🎯 Próximos Passos

### Imediato

1. ✅ Configure o frontend seguindo `FRONTEND_ENVIRONMENT_SETUP.md`
2. ✅ Teste o ambiente DEV completo
3. ✅ Teste o ambiente PROD

### Curto Prazo

4. ⚠️ Implemente autenticação JWT (mais seguro que Basic Auth)
5. ⚠️ Configure CI/CD para o frontend
6. ⚠️ Hospede o frontend (Vercel/Netlify)

### Longo Prazo

7. ⚠️ Adicione testes E2E
8. ⚠️ Configure monitoramento e alertas
9. ⚠️ Implemente logging estruturado

---

**Última Atualização:** 04 de Outubro de 2025  
**Versão Backend:** 1.0.0  
**Ambiente PROD:** Railway  
**Ambiente DEV:** Local Docker
