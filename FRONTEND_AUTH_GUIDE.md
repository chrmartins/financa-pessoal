# 📋 API de Autenticação - Documentação para Frontend

## 🔐 Sistema de Autenticação

**Tipo**: HTTP Basic Authentication  
**Base URL**: `http://localhost:8080`  
**Content-Type**: `application/json`

---

## 👥 **Usuários Disponíveis para Teste**

| Email                    | Senha      | Papel | Status   |
| ------------------------ | ---------- | ----- | -------- |
| `admin@financeiro.com`   | `admin123` | ADMIN | ✅ Ativo |
| `joao.silva@email.com`   | `123456`   | USER  | ✅ Ativo |
| `maria.santos@email.com` | `123456`   | USER  | ✅ Ativo |

---

## 🛡️ **Endpoints de Autenticação**

### 1. **Listar Usuários (Público)**

```http
GET /api/usuarios
```

**Descrição**: Retorna lista de todos os usuários (endpoint público para consulta)

**Resposta**:

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "nome": "Admin Sistema",
    "email": "admin@financeiro.com",
    "papel": "ADMIN",
    "ativo": true,
    "dataCriacao": "2025-09-29T20:48:17.01524",
    "dataAtualizacao": null,
    "ultimoAcesso": null
  }
]
```

### 2. **Obter Usuário Atual (Autenticado)**

```http
GET /api/me/info
Authorization: Basic <base64(email:senha)>
```

**Descrição**: Retorna informações do usuário autenticado

**Headers**:

```
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206YWRtaW4xMjM=
```

**Resposta**:

```json
"Usuário logado: admin@financeiro.com"
```

---

## 📊 **Endpoints Protegidos (Requerem Autenticação)**

### 3. **Listar Categorias**

```http
GET /api/categorias
Authorization: Basic <base64(email:senha)>
```

**Descrição**: Retorna todas as categorias financeiras

**Resposta**:

```json
[
  {
    "id": "650e8400-e29b-41d4-a716-446655440001",
    "nome": "Salário",
    "descricao": "Receita de salário mensal",
    "tipo": "RECEITA",
    "ativa": true,
    "dataCriacao": "2025-09-29T20:48:17.017313",
    "dataAtualizacao": null
  }
]
```

---

## 🔧 **Como Implementar no Frontend**

### **JavaScript/Fetch**

```javascript
// 1. Login com credenciais
const email = "admin@financeiro.com";
const senha = "admin123";
const credentials = btoa(`${email}:${senha}`); // Base64 encode

// 2. Fazer requisição autenticada
const response = await fetch("http://localhost:8080/api/categorias", {
  method: "GET",
  headers: {
    Authorization: `Basic ${credentials}`,
    "Content-Type": "application/json",
  },
});

if (response.ok) {
  const data = await response.json();
  console.log("Login sucesso:", data);
} else if (response.status === 401) {
  console.log("Credenciais inválidas");
}
```

### **Axios (React/Vue)**

```javascript
import axios from "axios";

// Configurar instância do axios
const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

// Função de login
const login = async (email, senha) => {
  try {
    const response = await api.get("/api/categorias", {
      auth: {
        username: email,
        password: senha,
      },
    });

    // Login bem-sucedido
    return { success: true, data: response.data };
  } catch (error) {
    if (error.response?.status === 401) {
      return { success: false, message: "Email ou senha incorretos" };
    }
    return { success: false, message: "Erro no servidor" };
  }
};

// Uso
const resultado = await login("admin@financeiro.com", "admin123");
```

### **Angular HttpClient**

```typescript
import { HttpClient, HttpHeaders } from '@angular/common/http';

constructor(private http: HttpClient) {}

login(email: string, senha: string) {
  const credentials = btoa(`${email}:${senha}`);
  const headers = new HttpHeaders({
    'Authorization': `Basic ${credentials}`,
    'Content-Type': 'application/json'
  });

  return this.http.get('http://localhost:8080/api/categorias', { headers });
}
```

---

## ⚠️ **Tratamento de Erros**

| Status Code | Significado       | Ação                     |
| ----------- | ----------------- | ------------------------ |
| `200`       | ✅ Sucesso        | Login válido, prosseguir |
| `401`       | ❌ Não autorizado | Credenciais inválidas    |
| `403`       | ❌ Proibido       | Usuário sem permissão    |
| `404`       | ❌ Não encontrado | Endpoint não existe      |
| `500`       | ❌ Erro servidor  | Problema interno         |

---

## 🔄 **Fluxo de Login Recomendado**

1. **Capturar credenciais** do formulário
2. **Tentar autenticação** em qualquer endpoint protegido (ex: `/api/categorias`)
3. **Se 200**: Login válido → Salvar credenciais/token localmente
4. **Se 401**: Mostrar "Email ou senha incorretos"
5. **Usar credenciais** em todas as requisições subsequentes

---

## 🚀 **Teste Rápido via cURL**

```bash
# Teste de login válido
curl -u "admin@financeiro.com:admin123" http://localhost:8080/api/categorias

# Teste de login inválido
curl -u "admin@financeiro.com:senha_errada" http://localhost:8080/api/categorias
```

---

## 📝 **Notas Importantes**

- ✅ **CORS habilitado** para desenvolvimento
- ✅ **3 usuários** disponíveis para teste
- ✅ **12 categorias** retornadas após login
- ⚠️ **Senhas em texto plano** (apenas para desenvolvimento)
- 🔐 **Usar HTTPS** em produção
- 💾 **Salvar credenciais** no localStorage/sessionStorage conforme necessário
