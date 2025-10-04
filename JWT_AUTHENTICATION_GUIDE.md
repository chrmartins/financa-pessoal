# 🔐 Guia de Autenticação JWT - API Finanças Pessoal

## ✅ Implementação Completa

A API agora utiliza **JWT (JSON Web Tokens)** para autenticação profissional e stateless!

---

## 📋 Como Funciona

1. **Login**: Frontend envia email/senha → Backend retorna JWT token
2. **Requisições**: Frontend envia `Authorization: Bearer {token}` em todas as requests
3. **Refresh**: Quando token expira, use o refresh token para obter novo token

---

## 🎯 Endpoints de Autenticação

### 1. Login (Obter Token)

**POST** `/api/auth`

**Request:**

```json
{
  "email": "admin@financeiro.com",
  "senha": "admin123"
}
```

**Response 200 OK:**

```json
{
  "usuario": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "nome": "Admin Sistema",
    "email": "admin@financeiro.com",
    "papel": "ADMIN",
    "ativo": true
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

---

### 2. Refresh Token (Renovar Token)

**POST** `/api/auth/refresh`

**Request:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response 200 OK:**

```json
{
  "usuario": { ... },
  "token": "novo_token_jwt",
  "refreshToken": "novo_refresh_token",
  "expiresIn": 86400000
}
```

---

## 💻 Implementação no Frontend

### JavaScript Vanilla

```javascript
// 1. Login e armazenar token
async function login(email, senha) {
  const response = await fetch("http://localhost:8080/api/auth", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, senha }),
  });

  if (!response.ok) {
    throw new Error("Login falhou");
  }

  const data = await response.json();

  // Armazena tokens no localStorage
  localStorage.setItem("token", data.token);
  localStorage.setItem("refreshToken", data.refreshToken);
  localStorage.setItem("usuario", JSON.stringify(data.usuario));

  return data;
}

// 2. Fazer requisição autenticada
async function buscarCategorias() {
  const token = localStorage.getItem("token");

  const response = await fetch("http://localhost:8080/api/categorias", {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (response.status === 401) {
    // Token expirado, tenta renovar
    await refreshToken();
    return buscarCategorias(); // Retry
  }

  return response.json();
}

// 3. Renovar token
async function refreshToken() {
  const refreshToken = localStorage.getItem("refreshToken");

  const response = await fetch("http://localhost:8080/api/auth/refresh", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ refreshToken }),
  });

  if (!response.ok) {
    // Refresh token inválido, redireciona para login
    localStorage.clear();
    window.location.href = "/login";
    throw new Error("Sessão expirada");
  }

  const data = await response.json();
  localStorage.setItem("token", data.token);
  localStorage.setItem("refreshToken", data.refreshToken);
}

// 4. Logout
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("usuario");
  window.location.href = "/login";
}
```

---

### React com Axios

```javascript
// src/services/api.js
import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080",
});

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para renovar token quando expirar
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Se recebeu 401 e ainda não tentou renovar
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        const { data } = await axios.post(
          `${api.defaults.baseURL}/api/auth/refresh`,
          { refreshToken }
        );

        localStorage.setItem("token", data.token);
        localStorage.setItem("refreshToken", data.refreshToken);

        // Refaz a requisição original com novo token
        originalRequest.headers.Authorization = `Bearer ${data.token}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token inválido, desloga
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

```javascript
// src/services/authService.js
import api from "./api";

export const authService = {
  async login(email, senha) {
    const { data } = await api.post("/api/auth", { email, senha });
    localStorage.setItem("token", data.token);
    localStorage.setItem("refreshToken", data.refreshToken);
    localStorage.setItem("usuario", JSON.stringify(data.usuario));
    return data;
  },

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("usuario");
  },

  getUsuario() {
    const usuario = localStorage.getItem("usuario");
    return usuario ? JSON.parse(usuario) : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem("token");
  },
};
```

```javascript
// src/pages/Login.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authService } from "../services/authService";

function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro("");

    try {
      await authService.login(email, senha);
      navigate("/dashboard");
    } catch (error) {
      setErro("Credenciais inválidas");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      <input
        type="password"
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        placeholder="Senha"
        required
      />
      {erro && <div className="erro">{erro}</div>}
      <button type="submit">Entrar</button>
    </form>
  );
}
```

---

## 🔒 Credenciais de Teste

**DEV (localhost:8080):**

```
Email: admin@financeiro.com
Senha: admin123
```

**PROD (Railway):**

```
Email: admin@financeiro.com
Senha: password
```

---

## ⚙️ Configuração de Ambiente

### .env.development

```env
REACT_APP_API_URL=http://localhost:8080
```

### .env.production

```env
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
```

---

## 📊 Fluxo Completo

```
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │
       │ 1. POST /api/auth
       │    { email, senha }
       ↓
┌─────────────┐
│   Backend   │
└──────┬──────┘
       │
       │ 2. Valida credenciais
       │    Gera JWT token
       ↓
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │
       │ 3. Armazena token
       │    localStorage.setItem('token', ...)
       ↓
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │
       │ 4. GET /api/categorias
       │    Authorization: Bearer {token}
       ↓
┌─────────────┐
│   Backend   │
└──────┬──────┘
       │
       │ 5. Valida JWT
       │    Retorna dados
       ↓
┌─────────────┐
│  Frontend   │
└─────────────┘
```

---

## ⏰ Expiração de Tokens

- **Access Token**: 24 horas (86400000 ms)
- **Refresh Token**: 7 dias (604800000 ms)

Quando o access token expirar (401), use o refresh token para obter um novo.

---

## 🛡️ Segurança

✅ **Implementado:**

- Tokens JWT assinados com HS256
- Chave secreta segura (256 bits)
- Refresh tokens para renovação
- Session stateless (sem sessões no servidor)
- CORS configurado

⚠️ **Boas Práticas:**

- Nunca exponha a `secretKey` no frontend
- Use HTTPS em produção
- Considere HttpOnly cookies para produção (mais seguro que localStorage)
- Implemente rate limiting no backend

---

## 🧪 Testando

Execute o script de teste:

```bash
# 1. Inicie a aplicação
./gradlew bootRun

# 2. Em outro terminal, execute:
./test-jwt.sh
```

**Teste manual com cURL:**

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@financeiro.com","senha":"admin123"}' \
  | jq -r '.token')

# 2. Usar token
curl -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📚 Referências

- [JWT.io](https://jwt.io/) - Decodificar e debugar tokens
- [JJWT Documentation](https://github.com/jwtk/jjwt) - Biblioteca JWT usada
- [Spring Security JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

---

## ✅ Checklist de Integração

- [ ] Implementar serviço de autenticação
- [ ] Configurar interceptors do Axios
- [ ] Criar tela de login
- [ ] Armazenar tokens no localStorage
- [ ] Implementar logout
- [ ] Adicionar renovação automática de tokens
- [ ] Proteger rotas que exigem autenticação
- [ ] Testar fluxo completo (login → requests → refresh → logout)
- [ ] Configurar variáveis de ambiente (DEV/PROD)
- [ ] Deploy frontend

---

**🎉 Parabéns! Sua API agora tem autenticação JWT profissional!**
