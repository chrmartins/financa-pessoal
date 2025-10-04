# 🚀 Quick Start - Frontend Integration

## ⚡ Início Rápido (5 minutos)

Se você já tem um frontend rodando e quer conectar com a API **agora mesmo**, siga estes passos:

---

## 1️⃣ Configure as Variáveis de Ambiente

### Para Desenvolvimento (Backend Local)

Crie `.env.development` na raiz do seu projeto frontend:

```env
# React
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password

# Vue/Vite
VITE_API_URL=http://localhost:8080
VITE_API_USER=admin@financeiro.com
VITE_API_PASSWORD=password

# Angular - edite src/environments/environment.ts
```

### Para Produção (Backend Railway)

Crie `.env.production`:

```env
# React
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password

# Vue/Vite
VITE_API_URL=https://financa-pessoal-production.up.railway.app
VITE_API_USER=admin@financeiro.com
VITE_API_PASSWORD=password
```

---

## 2️⃣ Configure o Cliente HTTP

### Opção A: Axios (Recomendado)

```bash
npm install axios
```

**`src/services/api.js`**

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080",
});

// Adiciona autenticação automaticamente
api.interceptors.request.use((config) => {
  const username = process.env.REACT_APP_API_USER;
  const password = process.env.REACT_APP_API_PASSWORD;

  if (username && password) {
    const credentials = btoa(`${username}:${password}`);
    config.headers.Authorization = `Basic ${credentials}`;
  }

  return config;
});

export default api;
```

### Opção B: Fetch Nativo

**`src/services/api.js`**

```javascript
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";
const credentials = btoa(
  `${process.env.REACT_APP_API_USER}:${process.env.REACT_APP_API_PASSWORD}`
);

const headers = {
  Authorization: `Basic ${credentials}`,
  "Content-Type": "application/json",
};

export const fetchApi = async (endpoint, options = {}) => {
  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers: { ...headers, ...options.headers },
  });

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.status === 204 ? null : response.json();
};
```

---

## 3️⃣ Teste a Conexão

### Teste Simples (Console do Navegador)

```javascript
// Abra o DevTools (F12) e cole no console:

// Para desenvolvimento (localhost:8080)
fetch("http://localhost:8080/api/categorias", {
  headers: {
    Authorization: "Basic " + btoa("admin@financeiro.com:password"),
  },
})
  .then((r) => r.json())
  .then((data) => console.log(data));

// Para produção (Railway)
fetch("https://financa-pessoal-production.up.railway.app/api/categorias", {
  headers: {
    Authorization: "Basic " + btoa("admin@financeiro.com:password"),
  },
})
  .then((r) => r.json())
  .then((data) => console.log(data));
```

**Resultado esperado:** Array com 11 categorias

---

## 4️⃣ Implemente os Serviços

### Categorias

**`src/services/categoriaService.js`**

```javascript
import api from "./api";

export const categoriaService = {
  getAll: () => api.get("/api/categorias").then((r) => r.data),
  getByTipo: (tipo) =>
    api.get(`/api/categorias?tipo=${tipo}`).then((r) => r.data),
  create: (categoria) =>
    api.post("/api/categorias", categoria).then((r) => r.data),
};
```

### Transações

**`src/services/transacaoService.js`**

```javascript
import api from "./api";

export const transacaoService = {
  getAll: (filtros = {}) =>
    api.get("/api/transacoes", { params: filtros }).then((r) => r.data),

  create: (transacao) =>
    api.post("/api/transacoes", transacao).then((r) => r.data),

  getResumoMensal: (ano, mes) =>
    api
      .get(`/api/transacoes/resumo-mensal?ano=${ano}&mes=${mes}`)
      .then((r) => r.data),
};
```

---

## 5️⃣ Use nos Componentes

### Exemplo React

```javascript
import { useState, useEffect } from "react";
import { categoriaService } from "./services/categoriaService";

function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    categoriaService
      .getAll()
      .then((data) => {
        setCategorias(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Erro:", error);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Carregando...</div>;

  return (
    <ul>
      {categorias.map((cat) => (
        <li key={cat.id}>
          {cat.nome} - {cat.tipo}
        </li>
      ))}
    </ul>
  );
}
```

---

## 6️⃣ Inicie o Ambiente

### Backend Local (Terminal 1)

```bash
cd /Users/chrmartins/projetos/financa-pessoal
docker-compose up postgres  # PostgreSQL
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Frontend (Terminal 2)

```bash
cd seu-projeto-frontend
npm install  # Se ainda não fez
npm start    # React
# ou
npm run dev  # Vue/Vite
```

Acesse: **http://localhost:3000**

---

## ✅ Validações

### 1. Backend está rodando?

```bash
curl http://localhost:8080/actuator/health
# Deve retornar: {"status":"UP"}
```

### 2. Autenticação funciona?

```bash
curl -u "admin@financeiro.com:password" http://localhost:8080/api/categorias
# Deve retornar: array com categorias
```

### 3. Frontend está apontando para o backend certo?

```javascript
// No console do navegador:
console.log(process.env.REACT_APP_API_URL);
// DEV: http://localhost:8080
// PROD: https://financa-pessoal-production.up.railway.app
```

### 4. CORS está OK?

- Abra DevTools (F12) → Aba Network
- Faça uma requisição
- Se aparecer erro CORS, verifique o backend (já está configurado)

---

## 🎯 Próximos Passos

Agora que está funcionando:

1. ✅ Teste criar uma transação via frontend
2. ✅ Implemente o CRUD completo de categorias
3. ✅ Implemente o CRUD completo de transações
4. ✅ Adicione o relatório de resumo mensal
5. 📖 Leia a [documentação completa](./FRONTEND_INTEGRATION_GUIDE.md)

---

## 🐛 Problemas Comuns

### ❌ CORS Error

```
Access blocked by CORS policy
```

**Já está resolvido no backend!** Reinicie o backend se alterou recentemente.

---

### ❌ 401 Unauthorized

```
Request failed with status code 401
```

**Verifique:**

1. Credenciais no `.env` estão corretas
2. Header Authorization está sendo enviado
3. Teste com cURL (comando acima)

---

### ❌ Variáveis de ambiente não carregam

**React:** Reinicie o servidor após alterar `.env`
**Vue/Vite:** Reinicie o dev server
**Angular:** Rebuild do projeto

---

## 📚 Documentação Completa

- **[ENVIRONMENTS.md](./ENVIRONMENTS.md)** - Arquitetura completa dos ambientes
- **[FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md)** - Setup detalhado
- **[FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)** - API completa

---

## 🆘 Precisa de Ajuda?

1. Verifique os logs do backend
2. Abra o DevTools (F12) → Network tab
3. Teste com cURL primeiro
4. Leia a documentação completa

---

**Status:** ✅ Backend DEV e PROD rodando  
**Data:** 04 de Outubro de 2025
