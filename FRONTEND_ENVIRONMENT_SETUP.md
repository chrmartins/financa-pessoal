# Guia de Configuração de Ambientes - Frontend

## 🎯 Objetivo

Configurar o frontend para trabalhar em **dois ambientes distintos**:

- **DEV (Desenvolvimento)**: Backend local + Frontend local
- **PROD (Produção)**: Backend Railway + Frontend (local ou hospedado)

---

## 🏗️ Arquitetura dos Ambientes

### Ambiente de Desenvolvimento (DEV)

```
┌─────────────────┐         ┌──────────────────────┐
│  Frontend Local │────────▶│  Backend Local       │
│  localhost:3000 │         │  localhost:8080      │
└─────────────────┘         └──────────────────────┘
                                      │
                                      ▼
                            ┌──────────────────────┐
                            │  PostgreSQL Local    │
                            │  localhost:5432      │
                            └──────────────────────┘
```

### Ambiente de Produção (PROD)

```
┌─────────────────┐         ┌──────────────────────────────────────┐
│  Frontend       │────────▶│  Backend Railway                     │
│  (hospedado)    │         │  financa-pessoal-production.up...    │
└─────────────────┘         └──────────────────────────────────────┘
                                      │
                                      ▼
                            ┌──────────────────────────────────────┐
                            │  PostgreSQL Railway                  │
                            └──────────────────────────────────────┘
```

---

## ⚙️ Configuração do Frontend

### 1. Variáveis de Ambiente

Crie os arquivos de configuração no **projeto do frontend**:

#### `.env.development` (Ambiente DEV)

```env
# Backend Local
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=development

# Opcional: Se usar Vue/Angular ajuste o prefixo
# VUE_APP_API_URL=http://localhost:8080
# VITE_API_URL=http://localhost:8080
# NG_APP_API_URL=http://localhost:8080
```

#### `.env.production` (Ambiente PROD)

```env
# Backend Railway
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=production
```

#### `.env.local` (Opcional - Sobrescreve dev localmente)

```env
# Use este arquivo para configurações pessoais de dev
# Este arquivo NÃO deve ir para o git (adicione ao .gitignore)
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=seu.email@teste.com
REACT_APP_API_PASSWORD=sua-senha-local
```

---

### 2. Configuração do Cliente HTTP

#### Opção A: React com Axios

**`src/services/api.js`**

```javascript
import axios from "axios";

// Configuração base que lê das variáveis de ambiente
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080",
});

// Interceptor para adicionar autenticação em todas as requisições
api.interceptors.request.use((config) => {
  const username = process.env.REACT_APP_API_USER;
  const password = process.env.REACT_APP_API_PASSWORD;

  if (username && password) {
    const credentials = btoa(`${username}:${password}`);
    config.headers.Authorization = `Basic ${credentials}`;
  }

  return config;
});

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.error("Não autorizado - verifique as credenciais");
      // Redirecionar para login ou mostrar mensagem
    }
    return Promise.reject(error);
  }
);

export default api;
```

**`src/services/categoriaService.js`**

```javascript
import api from "./api";

export const categoriaService = {
  // Listar todas
  getAll: async (tipo = null) => {
    const params = tipo ? { tipo } : {};
    const { data } = await api.get("/api/categorias", { params });
    return data;
  },

  // Buscar por ID
  getById: async (id) => {
    const { data } = await api.get(`/api/categorias/${id}`);
    return data;
  },

  // Criar
  create: async (categoria) => {
    const { data } = await api.post("/api/categorias", categoria);
    return data;
  },

  // Atualizar
  update: async (id, categoria) => {
    const { data } = await api.put(`/api/categorias/${id}`, categoria);
    return data;
  },

  // Ativar/Desativar
  ativar: async (id) => {
    const { data } = await api.patch(`/api/categorias/${id}/ativar`);
    return data;
  },

  desativar: async (id) => {
    const { data } = await api.patch(`/api/categorias/${id}/desativar`);
    return data;
  },

  // Deletar
  delete: async (id) => {
    await api.delete(`/api/categorias/${id}`);
  },
};
```

**`src/services/transacaoService.js`**

```javascript
import api from "./api";

export const transacaoService = {
  // Listar com filtros
  getAll: async (filtros = {}) => {
    const { data } = await api.get("/api/transacoes", { params: filtros });
    return data;
  },

  // Buscar por ID
  getById: async (id) => {
    const { data } = await api.get(`/api/transacoes/${id}`);
    return data;
  },

  // Criar
  create: async (transacao) => {
    const { data } = await api.post("/api/transacoes", transacao);
    return data;
  },

  // Atualizar
  update: async (id, transacao) => {
    const { data } = await api.put(`/api/transacoes/${id}`, transacao);
    return data;
  },

  // Deletar
  delete: async (id) => {
    await api.delete(`/api/transacoes/${id}`);
  },

  // Resumo mensal
  getResumoMensal: async (ano, mes) => {
    const { data } = await api.get("/api/transacoes/resumo-mensal", {
      params: { ano, mes },
    });
    return data;
  },
};
```

---

#### Opção B: React com Fetch (sem biblioteca)

**`src/config/api.js`**

```javascript
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";
const API_USER = process.env.REACT_APP_API_USER;
const API_PASSWORD = process.env.REACT_APP_API_PASSWORD;

const getAuthHeaders = () => {
  const credentials = btoa(`${API_USER}:${API_PASSWORD}`);
  return {
    Authorization: `Basic ${credentials}`,
    "Content-Type": "application/json",
  };
};

export const fetchApi = async (endpoint, options = {}) => {
  const url = `${API_URL}${endpoint}`;
  const config = {
    ...options,
    headers: {
      ...getAuthHeaders(),
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, config);

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro na requisição");
    }

    // DELETE retorna 204 sem body
    if (response.status === 204) {
      return null;
    }

    return await response.json();
  } catch (error) {
    console.error("Erro na API:", error);
    throw error;
  }
};

export { API_URL };
```

**`src/services/transacaoService.js`**

```javascript
import { fetchApi } from "../config/api";

export const transacaoService = {
  getAll: (filtros) => {
    const params = new URLSearchParams(filtros).toString();
    const endpoint = `/api/transacoes${params ? `?${params}` : ""}`;
    return fetchApi(endpoint);
  },

  getById: (id) => fetchApi(`/api/transacoes/${id}`),

  create: (transacao) =>
    fetchApi("/api/transacoes", {
      method: "POST",
      body: JSON.stringify(transacao),
    }),

  update: (id, transacao) =>
    fetchApi(`/api/transacoes/${id}`, {
      method: "PUT",
      body: JSON.stringify(transacao),
    }),

  delete: (id) =>
    fetchApi(`/api/transacoes/${id}`, {
      method: "DELETE",
    }),

  getResumoMensal: (ano, mes) =>
    fetchApi(`/api/transacoes/resumo-mensal?ano=${ano}&mes=${mes}`),
};
```

---

#### Opção C: Vue 3 com Axios

**`src/services/api.js`**

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const username = import.meta.env.VITE_API_USER;
  const password = import.meta.env.VITE_API_PASSWORD;

  if (username && password) {
    const credentials = btoa(`${username}:${password}`);
    config.headers.Authorization = `Basic ${credentials}`;
  }

  return config;
});

export default api;
```

**Arquivo de ambiente Vue:**

- `.env.development` → `VITE_API_URL=http://localhost:8080`
- `.env.production` → `VITE_API_URL=https://financa-pessoal-production.up.railway.app`

---

#### Opção D: Angular com HttpClient

**`src/environments/environment.ts` (DEV)**

```typescript
export const environment = {
  production: false,
  apiUrl: "http://localhost:8080",
  apiUser: "admin@financeiro.com",
  apiPassword: "password",
};
```

**`src/environments/environment.prod.ts` (PROD)**

```typescript
export const environment = {
  production: true,
  apiUrl: "https://financa-pessoal-production.up.railway.app",
  apiUser: "admin@financeiro.com",
  apiPassword: "password",
};
```

**`src/app/services/api.service.ts`**

```typescript
import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  private getHeaders(): HttpHeaders {
    const credentials = btoa(
      `${environment.apiUser}:${environment.apiPassword}`
    );
    return new HttpHeaders({
      Authorization: `Basic ${credentials}`,
      "Content-Type": "application/json",
    });
  }

  constructor(private http: HttpClient) {}

  get<T>(endpoint: string, params?: any) {
    return this.http.get<T>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders(),
      params,
    });
  }

  post<T>(endpoint: string, body: any) {
    return this.http.post<T>(`${this.apiUrl}${endpoint}`, body, {
      headers: this.getHeaders(),
    });
  }

  put<T>(endpoint: string, body: any) {
    return this.http.put<T>(`${this.apiUrl}${endpoint}`, body, {
      headers: this.getHeaders(),
    });
  }

  delete<T>(endpoint: string) {
    return this.http.delete<T>(`${this.apiUrl}${endpoint}`, {
      headers: this.getHeaders(),
    });
  }
}
```

---

### 3. Indicador Visual de Ambiente

Útil para evitar confusões durante desenvolvimento:

**`src/components/EnvironmentBadge.jsx`**

```javascript
import React from "react";

const EnvironmentBadge = () => {
  const env = process.env.REACT_APP_ENVIRONMENT || "development";
  const apiUrl = process.env.REACT_APP_API_URL;

  if (env === "production") {
    return null; // Não mostrar em produção
  }

  return (
    <div
      style={{
        position: "fixed",
        bottom: 10,
        right: 10,
        padding: "8px 16px",
        backgroundColor: env === "development" ? "#4CAF50" : "#FF9800",
        color: "white",
        borderRadius: 4,
        fontSize: 12,
        fontWeight: "bold",
        zIndex: 9999,
      }}
    >
      <div>🔧 {env.toUpperCase()}</div>
      <div style={{ fontSize: 10, marginTop: 4 }}>{apiUrl}</div>
    </div>
  );
};

export default EnvironmentBadge;
```

**Uso no `App.js`:**

```javascript
import EnvironmentBadge from "./components/EnvironmentBadge";

function App() {
  return (
    <div className="App">
      <EnvironmentBadge />
      {/* resto da aplicação */}
    </div>
  );
}
```

---

## 🚀 Scripts de Execução

### React (package.json)

```json
{
  "scripts": {
    "start": "react-scripts start",
    "start:dev": "REACT_APP_ENV=development react-scripts start",
    "start:prod": "REACT_APP_ENV=production react-scripts start",
    "build": "react-scripts build",
    "build:dev": "REACT_APP_ENV=development react-scripts build",
    "build:prod": "REACT_APP_ENV=production react-scripts build"
  }
}
```

### Vue (package.json)

```json
{
  "scripts": {
    "dev": "vite --mode development",
    "dev:prod": "vite --mode production",
    "build": "vite build",
    "build:dev": "vite build --mode development",
    "build:prod": "vite build --mode production"
  }
}
```

### Angular (package.json)

```json
{
  "scripts": {
    "start": "ng serve",
    "start:prod": "ng serve --configuration production",
    "build": "ng build",
    "build:prod": "ng build --configuration production"
  }
}
```

---

## 📋 Checklist de Configuração

### Backend (já configurado ✅)

- [x] Backend local rodando em `http://localhost:8080`
- [x] Backend produção hospedado no Railway
- [x] PostgreSQL local com dados de desenvolvimento
- [x] PostgreSQL Railway com dados de produção
- [x] CORS configurado para aceitar requisições do frontend

### Frontend (configure agora)

- [ ] Criar arquivo `.env.development` com URL do backend local
- [ ] Criar arquivo `.env.production` com URL do Railway
- [ ] Adicionar `.env.local` ao `.gitignore`
- [ ] Configurar cliente HTTP (Axios ou Fetch)
- [ ] Criar serviços para cada recurso (categorias, transações, usuários)
- [ ] Adicionar interceptors de autenticação
- [ ] Implementar tratamento de erros
- [ ] Adicionar badge de ambiente (opcional)
- [ ] Testar em ambiente DEV
- [ ] Testar em ambiente PROD

---

## 🧪 Testando os Ambientes

### 1. Teste do Ambiente DEV

**Pré-requisitos:**

```bash
# Terminal 1: Backend local rodando
cd /Users/chrmartins/projetos/financa-pessoal
./gradlew bootRun --args='--spring.profiles.active=dev'

# Terminal 2: PostgreSQL local rodando
docker-compose up postgres

# Terminal 3: Frontend rodando
cd seu-projeto-frontend
npm run start  # ou yarn dev
```

**Validação:**

1. Abra o navegador em `http://localhost:3000`
2. Verifique o badge mostrando "DEVELOPMENT"
3. Faça login com `admin@financeiro.com` / `password`
4. Crie uma nova transação
5. Verifique no DevTools que a requisição foi para `http://localhost:8080`

---

### 2. Teste do Ambiente PROD

**Configuração:**

```bash
# Apenas o frontend rodando localmente apontando para produção
cd seu-projeto-frontend
npm run start:prod  # ou configure manualmente
```

**Validação:**

1. Abra o navegador em `http://localhost:3000`
2. Verifique no DevTools que as requisições vão para Railway
3. Faça login com `admin@financeiro.com` / `password`
4. Teste CRUD de transações
5. Verifique no Railway Logs que as requisições estão chegando

---

## 🔒 Segurança em Produção

### ⚠️ IMPORTANTE: Credenciais Hardcoded

Atualmente, as credenciais estão **hardcoded** nos arquivos `.env`. Para produção real:

#### Opção 1: Backend com JWT (Recomendado)

```javascript
// Fluxo recomendado:
// 1. Login: POST /api/auth → retorna JWT token
// 2. Salvar token no localStorage
// 3. Enviar token em todas as requisições

const login = async (email, senha) => {
  const { data } = await api.post("/api/auth", { email, senha });
  localStorage.setItem("token", data.token);
  return data;
};

// Interceptor atualizado
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

#### Opção 2: Variáveis de Ambiente no Build

```bash
# Durante deploy do frontend
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app npm run build
```

#### Opção 3: Configuração Dinâmica

```javascript
// public/config.js
window.ENV = {
  API_URL: "https://financa-pessoal-production.up.railway.app",
};

// src/config/api.js
const API_URL = window.ENV?.API_URL || process.env.REACT_APP_API_URL;
```

---

## 📊 Comparação dos Ambientes

| Aspecto         | DEV                       | PROD                                              |
| --------------- | ------------------------- | ------------------------------------------------- |
| **Backend URL** | http://localhost:8080     | https://financa-pessoal-production.up.railway.app |
| **Database**    | PostgreSQL Local (Docker) | PostgreSQL Railway                                |
| **Dados**       | Dados de teste locais     | Dados reais de produção                           |
| **CORS**        | Liberado para localhost   | Configurado para domínio do frontend              |
| **Logs**        | Console local             | Railway Dashboard                                 |
| **Deploy**      | Manual (gradle bootRun)   | Automático (push to main)                         |
| **Hot Reload**  | Sim (DevTools)            | Não                                               |
| **Debugging**   | Sim (attach debugger)     | Logs apenas                                       |

---

## 🐛 Troubleshooting

### Problema: CORS Error no navegador

```
Access to fetch at 'https://...' from origin 'http://localhost:3000'
has been blocked by CORS policy
```

**Solução:**
Verifique no backend se o CORS está configurado para aceitar o domínio do frontend:

```java
// SecurityConfig.java deve ter:
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:3000",  // DEV
        "https://seu-frontend.vercel.app"  // PROD
    ));
    // ...
}))
```

---

### Problema: 401 Unauthorized

```
Request failed with status code 401
```

**Solução:**

1. Verifique as credenciais no `.env`
2. Confirme que o interceptor está adicionando o header Authorization
3. Teste com cURL para validar as credenciais:

```bash
curl -u "admin@financeiro.com:password" \
  http://localhost:8080/api/categorias
```

---

### Problema: Variáveis de ambiente não carregam

**React:**

- Reinicie o servidor após alterar `.env`
- Variáveis devem começar com `REACT_APP_`
- Não use `.env` em produção, use build-time variables

**Vue/Vite:**

- Variáveis devem começar com `VITE_`
- Reinicie o dev server

---

## 📖 Próximos Passos

1. ✅ Configure os arquivos `.env` no frontend
2. ✅ Implemente o cliente HTTP com interceptors
3. ✅ Crie os serviços para cada recurso
4. ✅ Teste o ambiente DEV completo
5. ✅ Teste o ambiente PROD
6. ⚠️ (Futuro) Implemente autenticação JWT no backend
7. ⚠️ (Futuro) Configure CI/CD para o frontend
8. ⚠️ (Futuro) Hospede o frontend (Vercel/Netlify)

---

## 📞 Suporte

**Backend (API):**

- Logs DEV: Terminal local
- Logs PROD: Railway Dashboard → Service → Deployments → View Logs
- Healthcheck: `/actuator/health`

**Frontend:**

- Console do navegador (DevTools)
- Network tab para debug de requisições
- React DevTools / Vue DevTools

---

**Última Atualização:** 04 de Outubro de 2025  
**Versão:** 1.0.0
