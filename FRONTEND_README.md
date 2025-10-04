# 🎨 README - Integração Frontend

> **Para o Time de Frontend:** Este é o seu ponto de partida!

---

## 🎯 TL;DR (Muito Ocupado para Ler)

```bash
# 1. Crie .env.development
echo "REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password" > .env.development

# 2. Instale Axios
npm install axios

# 3. Configure API (veja FRONTEND_QUICKSTART.md)

# 4. Rode
npm start
```

**Backend já está rodando em:** `https://financa-pessoal-production.up.railway.app`

---

## 📚 Documentação Completa

### 🚀 Começar Agora

- **[EXECUTIVE_SUMMARY.md](./EXECUTIVE_SUMMARY.md)** ← Comece aqui! Visão geral do projeto
- **[FRONTEND_QUICKSTART.md](./FRONTEND_QUICKSTART.md)** ← Setup em 5 minutos

### 📖 Guias Detalhados

- **[FRONTEND_DOCS_INDEX.md](./FRONTEND_DOCS_INDEX.md)** - Índice de toda documentação
- **[FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)** - Configuração completa
- **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** - Referência da API
- **[FRONTEND_CHECKLIST.md](./FRONTEND_CHECKLIST.md)** - Checklist passo a passo

### 🏗️ Arquitetura

- **[ENVIRONMENTS.md](./ENVIRONMENTS.md)** - Ambientes DEV e PROD
- **[ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md)** - Diagramas do sistema

### 💻 Exemplos Práticos

- **[REACT_EXAMPLE.md](./REACT_EXAMPLE.md)** - Código completo React

---

## 🌍 Ambientes Disponíveis

### ✅ Desenvolvimento (Local)

```
Backend:  http://localhost:8080
Swagger:  http://localhost:8080/swagger-ui.html
Database: PostgreSQL Docker (localhost:5432)
```

**Como iniciar:**

```bash
# Terminal 1: PostgreSQL
cd backend-repo
docker-compose up postgres

# Terminal 2: Backend
./gradlew bootRun --args='--spring.profiles.active=dev'

# Terminal 3: Frontend (seu projeto)
npm start
```

---

### ✅ Produção (Railway)

```
Backend:  https://financa-pessoal-production.up.railway.app
Swagger:  https://financa-pessoal-production.up.railway.app/swagger-ui.html
Database: PostgreSQL Railway (gerenciado)
```

**Já está rodando!** Apenas aponte seu frontend para a URL acima.

---

## 🔐 Credenciais

**Ambos ambientes (DEV e PROD):**

```
Email: admin@financeiro.com
Senha: password
```

Outros usuários disponíveis:

- `joao.silva@email.com` / `password`
- `maria.santos@email.com` / `password`

---

## 📡 API Endpoints Principais

### Categorias

```
GET    /api/categorias              Listar todas
GET    /api/categorias?tipo=RECEITA Filtrar por tipo
POST   /api/categorias              Criar nova
PUT    /api/categorias/{id}         Atualizar
DELETE /api/categorias/{id}         Deletar
```

### Transações

```
GET    /api/transacoes                      Listar todas
GET    /api/transacoes?tipo=RECEITA         Por tipo
GET    /api/transacoes?dataInicio=...       Por período
POST   /api/transacoes                      Criar nova
PUT    /api/transacoes/{id}                 Atualizar
DELETE /api/transacoes/{id}                 Deletar
```

### Relatórios

```
GET    /api/transacoes/resumo-mensal?ano=2025&mes=8
```

**📖 Documentação completa:** [FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)

---

## 🧪 Teste Rápido

### No Navegador (Console)

**DEV:**

```javascript
fetch("http://localhost:8080/api/categorias", {
  headers: {
    Authorization: "Basic " + btoa("admin@financeiro.com:password"),
  },
})
  .then((r) => r.json())
  .then(console.log);
```

**PROD:**

```javascript
fetch("https://financa-pessoal-production.up.railway.app/api/categorias", {
  headers: {
    Authorization: "Basic " + btoa("admin@financeiro.com:password"),
  },
})
  .then((r) => r.json())
  .then(console.log);
```

**Resultado esperado:** Array com 11 categorias ✅

---

## 💻 Exemplo de Código

### Configuração Básica (Axios)

```javascript
// src/config/api.js
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

### Uso

```javascript
// src/services/categoriaService.js
import api from "../config/api";

export const categoriaService = {
  getAll: () => api.get("/api/categorias").then((r) => r.data),
  create: (cat) => api.post("/api/categorias", cat).then((r) => r.data),
};
```

**📖 Exemplo completo:** [REACT_EXAMPLE.md](./REACT_EXAMPLE.md)

---

## 🎯 Próximos Passos

### 1. Configure o Ambiente (15 min)

- [ ] Crie `.env.development` e `.env.production`
- [ ] Configure cliente HTTP (Axios ou Fetch)
- [ ] Teste conexão

### 2. Implemente CRUD (1-2 horas)

- [ ] Listagem de categorias
- [ ] Listagem de transações
- [ ] Formulários de criação/edição
- [ ] Relatório mensal

### 3. Teste (30 min)

- [ ] Teste em DEV
- [ ] Teste em PROD
- [ ] Valide todos os endpoints

### 4. Deploy (Futuro)

- [ ] Hospedar frontend (Vercel/Netlify)
- [ ] Configurar CI/CD
- [ ] Produção completa

---

## 📊 Status do Projeto

| Componente    | Status        | URL                                               |
| ------------- | ------------- | ------------------------------------------------- |
| Backend DEV   | ✅ Rodando    | http://localhost:8080                             |
| Backend PROD  | ✅ Rodando    | https://financa-pessoal-production.up.railway.app |
| Database DEV  | ✅ Rodando    | PostgreSQL Docker                                 |
| Database PROD | ✅ Rodando    | PostgreSQL Railway                                |
| Frontend DEV  | 🔧 Configurar | localhost:3000                                    |
| Frontend PROD | 🔧 Configurar | A hospedar                                        |

---

## 🆘 Problemas Comuns

### ❌ CORS Error

**Já está resolvido!** O backend aceita requisições do frontend.

### ❌ 401 Unauthorized

Verifique credenciais no `.env` e teste com cURL primeiro.

### ❌ Variáveis de ambiente não carregam

Reinicie o servidor após alterar `.env` (React/Vue/Angular).

**📖 Troubleshooting completo:** Cada guia tem seção de troubleshooting

---

## 📞 Links Úteis

- **Swagger DEV:** http://localhost:8080/swagger-ui.html
- **Swagger PROD:** https://financa-pessoal-production.up.railway.app/swagger-ui.html
- **Health DEV:** http://localhost:8080/actuator/health
- **Health PROD:** https://financa-pessoal-production.up.railway.app/actuator/health

---

## 📂 Estrutura Recomendada

```
seu-frontend/
├── .env.development        ← URLs e credenciais DEV
├── .env.production         ← URLs e credenciais PROD
├── src/
│   ├── config/
│   │   └── api.js         ← Configuração Axios
│   ├── services/
│   │   ├── categoriaService.js
│   │   └── transacaoService.js
│   ├── components/
│   │   ├── Categorias/
│   │   └── Transacoes/
│   └── App.jsx
└── package.json
```

---

## 🎉 Tudo Pronto!

**O backend está 100% funcional e aguardando a integração do frontend!**

**Próximo passo:** Escolha um guia e comece:

| Se você é...        | Leia...                                                          |
| ------------------- | ---------------------------------------------------------------- |
| **Experiente**      | [FRONTEND_QUICKSTART.md](./FRONTEND_QUICKSTART.md)               |
| **Iniciante**       | [FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md) |
| **Líder técnico**   | [EXECUTIVE_SUMMARY.md](./EXECUTIVE_SUMMARY.md)                   |
| **Novo no projeto** | [ENVIRONMENTS.md](./ENVIRONMENTS.md)                             |

---

**Última Atualização:** 04 de Outubro de 2025  
**Versão Backend:** 1.0.0  
**Status:** ✅ **Pronto para Integração**

---

**Dúvidas?** Consulte o [FRONTEND_DOCS_INDEX.md](./FRONTEND_DOCS_INDEX.md) para encontrar o guia certo!
