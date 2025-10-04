# Guia de Integração Frontend - API Finanças Pessoal

## 📋 Informações da API em Produção

### URL Base

```
https://financa-pessoal-production.up.railway.app
```

### Autenticação

A API utiliza **HTTP Basic Authentication** para todas as rotas protegidas.

#### Credenciais de Teste

```
Usuário: admin@financeiro.com
Senha: password
```

Outros usuários disponíveis:

- `joao.silva@email.com` / `password`
- `maria.santos@email.com` / `password`

---

## 🔐 Autenticação

### Método 1: Basic Auth com Headers

Para cada requisição protegida, inclua o header:

```javascript
const headers = {
  Authorization: "Basic " + btoa("admin@financeiro.com:password"),
  "Content-Type": "application/json",
};
```

### Método 2: Endpoint de Login Dedicado

**POST** `/api/auth`

```javascript
const loginResponse = await fetch(
  "https://financa-pessoal-production.up.railway.app/api/auth",
  {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email: "admin@financeiro.com",
      senha: "password",
    }),
  }
);

const data = await loginResponse.json();
// Retorna: { usuario: {...}, token: null, expiresIn: 86400000 }
```

**Nota:** Mesmo usando o endpoint `/api/auth`, você ainda precisa enviar o Basic Auth header nas requisições subsequentes.

---

## 📚 Endpoints Disponíveis

### 1. Usuários

#### Listar Todos os Usuários

```http
GET /api/usuarios
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=
```

**Response:**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "nome": "Admin Sistema",
    "email": "admin@financeiro.com",
    "papel": "ADMIN",
    "ativo": true,
    "dataCriacao": "2025-10-02T01:43:13.165",
    "ultimoAcesso": "2025-10-03T04:00:12.236"
  }
]
```

#### Buscar Usuário por ID

```http
GET /api/usuarios/{id}
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=
```

#### Criar Novo Usuário

```http
POST /api/usuarios
Content-Type: application/json
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=

{
  "nome": "Novo Usuário",
  "email": "novo@email.com",
  "senha": "senha123",
  "papel": "USER"
}
```

---

### 2. Categorias

#### Listar Todas as Categorias

```http
GET /api/categorias
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=
```

**Response:**

```json
[
  {
    "id": "650e8400-e29b-41d4-a716-446655440001",
    "nome": "Salário",
    "descricao": "Renda do trabalho principal",
    "tipo": "RECEITA",
    "ativa": true,
    "dataCriacao": "2025-10-01T01:41:21.238"
  }
]
```

#### Filtrar por Tipo

```http
GET /api/categorias?tipo=RECEITA
GET /api/categorias?tipo=DESPESA
```

#### Buscar Categoria por ID

```http
GET /api/categorias/{id}
```

#### Criar Nova Categoria

```http
POST /api/categorias
Content-Type: application/json
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=

{
  "nome": "Nova Categoria",
  "descricao": "Descrição da categoria",
  "tipo": "RECEITA"
}
```

**Tipos válidos:** `RECEITA` ou `DESPESA`

#### Atualizar Categoria

```http
PUT /api/categorias/{id}
Content-Type: application/json

{
  "nome": "Categoria Atualizada",
  "descricao": "Nova descrição",
  "tipo": "RECEITA"
}
```

#### Desativar Categoria

```http
PATCH /api/categorias/{id}/desativar
```

#### Ativar Categoria

```http
PATCH /api/categorias/{id}/ativar
```

#### Deletar Categoria

```http
DELETE /api/categorias/{id}
```

---

### 3. Transações

#### Listar Todas as Transações

```http
GET /api/transacoes
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=
```

**Response:**

```json
[
  {
    "id": "750e8400-e29b-41d4-a716-446655440001",
    "descricao": "Salário Agosto",
    "valor": 5500.0,
    "tipo": "RECEITA",
    "dataTransacao": "2025-08-01",
    "categoria": {
      "id": "650e8400-e29b-41d4-a716-446655440001",
      "nome": "Salário"
    },
    "usuario": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "nome": "João Silva"
    },
    "dataCriacao": "2025-10-01T01:41:21.238"
  }
]
```

#### Filtrar Transações

**Por tipo:**

```http
GET /api/transacoes?tipo=RECEITA
GET /api/transacoes?tipo=DESPESA
```

**Por período:**

```http
GET /api/transacoes?dataInicio=2025-08-01&dataFim=2025-08-31
```

**Por categoria:**

```http
GET /api/transacoes?categoriaId=650e8400-e29b-41d4-a716-446655440001
```

**Combinando filtros:**

```http
GET /api/transacoes?tipo=RECEITA&dataInicio=2025-08-01&dataFim=2025-08-31
```

#### Buscar Transação por ID

```http
GET /api/transacoes/{id}
```

#### Criar Nova Transação

```http
POST /api/transacoes
Content-Type: application/json
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=

{
  "descricao": "Freelance Projeto X",
  "valor": 1500.00,
  "tipo": "RECEITA",
  "dataTransacao": "2025-10-03",
  "categoriaId": "650e8400-e29b-41d4-a716-446655440002"
}
```

**Validações:**

- `descricao`: obrigatório, mínimo 3 caracteres
- `valor`: obrigatório, deve ser positivo
- `tipo`: obrigatório, valores válidos: `RECEITA` ou `DESPESA`
- `dataTransacao`: obrigatório, formato: `YYYY-MM-DD`
- `categoriaId`: obrigatório, UUID válido de categoria existente

#### Atualizar Transação

```http
PUT /api/transacoes/{id}
Content-Type: application/json

{
  "descricao": "Freelance Projeto Y",
  "valor": 2000.00,
  "tipo": "RECEITA",
  "dataTransacao": "2025-10-03",
  "categoriaId": "650e8400-e29b-41d4-a716-446655440002"
}
```

#### Deletar Transação

```http
DELETE /api/transacoes/{id}
```

---

### 4. Relatórios

#### Resumo Mensal

```http
GET /api/transacoes/resumo-mensal?ano=2025&mes=8
Authorization: Basic YWRtaW5AZmluYW5jZWlyby5jb206cGFzc3dvcmQ=
```

**Response:**

```json
{
  "mes": 8,
  "ano": 2025,
  "totalReceitas": 7050.0,
  "totalDespesas": 3600.0,
  "saldo": 3450.0,
  "quantidadeReceitas": 3,
  "quantidadeDespesas": 7
}
```

---

## 💻 Exemplos de Código

### JavaScript/Fetch

```javascript
// Configuração base
const API_URL = "https://financa-pessoal-production.up.railway.app";
const credentials = btoa("admin@financeiro.com:password");

const headers = {
  Authorization: `Basic ${credentials}`,
  "Content-Type": "application/json",
};

// Listar transações
async function getTransacoes() {
  const response = await fetch(`${API_URL}/api/transacoes`, { headers });
  return await response.json();
}

// Criar transação
async function createTransacao(transacao) {
  const response = await fetch(`${API_URL}/api/transacoes`, {
    method: "POST",
    headers,
    body: JSON.stringify(transacao),
  });
  return await response.json();
}

// Resumo mensal
async function getResumoMensal(ano, mes) {
  const response = await fetch(
    `${API_URL}/api/transacoes/resumo-mensal?ano=${ano}&mes=${mes}`,
    { headers }
  );
  return await response.json();
}
```

### React com Axios

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "https://financa-pessoal-production.up.railway.app/api",
  auth: {
    username: "admin@financeiro.com",
    password: "password",
  },
});

// Listar categorias
export const getCategorias = async (tipo) => {
  const params = tipo ? { tipo } : {};
  const { data } = await api.get("/categorias", { params });
  return data;
};

// Criar transação
export const createTransacao = async (transacao) => {
  const { data } = await api.post("/transacoes", transacao);
  return data;
};

// Atualizar transação
export const updateTransacao = async (id, transacao) => {
  const { data } = await api.put(`/transacoes/${id}`, transacao);
  return data;
};

// Deletar transação
export const deleteTransacao = async (id) => {
  await api.delete(`/transacoes/${id}`);
};
```

### React com Custom Hook

```javascript
import { useState, useEffect } from "react";
import axios from "axios";

const api = axios.create({
  baseURL: "https://financa-pessoal-production.up.railway.app/api",
  auth: {
    username: "admin@financeiro.com",
    password: "password",
  },
});

export const useTransacoes = (filtros = {}) => {
  const [transacoes, setTransacoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTransacoes = async () => {
      try {
        setLoading(true);
        const { data } = await api.get("/transacoes", { params: filtros });
        setTransacoes(data);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchTransacoes();
  }, [JSON.stringify(filtros)]);

  return { transacoes, loading, error };
};

// Uso no componente
function TransacoesList() {
  const { transacoes, loading, error } = useTransacoes({ tipo: "RECEITA" });

  if (loading) return <div>Carregando...</div>;
  if (error) return <div>Erro: {error}</div>;

  return (
    <ul>
      {transacoes.map((t) => (
        <li key={t.id}>
          {t.descricao} - R$ {t.valor}
        </li>
      ))}
    </ul>
  );
}
```

### Angular Service

```typescript
import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class FinanceiroService {
  private apiUrl = "https://financa-pessoal-production.up.railway.app/api";
  private credentials = btoa("admin@financeiro.com:password");

  private headers = new HttpHeaders({
    Authorization: `Basic ${this.credentials}`,
    "Content-Type": "application/json",
  });

  constructor(private http: HttpClient) {}

  getCategorias(tipo?: string): Observable<any[]> {
    const params = tipo ? { tipo } : {};
    return this.http.get<any[]>(`${this.apiUrl}/categorias`, {
      headers: this.headers,
      params,
    });
  }

  getTransacoes(filtros?: any): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/transacoes`, {
      headers: this.headers,
      params: filtros,
    });
  }

  createTransacao(transacao: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/transacoes`, transacao, {
      headers: this.headers,
    });
  }

  getResumoMensal(ano: number, mes: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/transacoes/resumo-mensal`, {
      headers: this.headers,
      params: { ano: ano.toString(), mes: mes.toString() },
    });
  }
}
```

---

## 🧪 Testando a API

### Com cURL

```bash
# Listar usuários
curl -u "admin@financeiro.com:password" \
  https://financa-pessoal-production.up.railway.app/api/usuarios

# Listar categorias
curl -u "admin@financeiro.com:password" \
  https://financa-pessoal-production.up.railway.app/api/categorias

# Criar transação
curl -u "admin@financeiro.com:password" \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "descricao": "Teste Frontend",
    "valor": 100.00,
    "tipo": "RECEITA",
    "dataTransacao": "2025-10-03",
    "categoriaId": "650e8400-e29b-41d4-a716-446655440001"
  }' \
  https://financa-pessoal-production.up.railway.app/api/transacoes

# Resumo mensal
curl -u "admin@financeiro.com:password" \
  "https://financa-pessoal-production.up.railway.app/api/transacoes/resumo-mensal?ano=2025&mes=8"
```

### Com Postman/Insomnia

1. **Base URL:** `https://financa-pessoal-production.up.railway.app`
2. **Authorization:** Basic Auth
   - Username: `admin@financeiro.com`
   - Password: `password`
3. **Headers:**
   - `Content-Type: application/json`

---

## ⚠️ Tratamento de Erros

### Códigos HTTP Comuns

| Código | Descrição             | Quando Ocorre                         |
| ------ | --------------------- | ------------------------------------- |
| 200    | OK                    | Sucesso na requisição GET/PUT         |
| 201    | Created               | Recurso criado com sucesso (POST)     |
| 204    | No Content            | Recurso deletado com sucesso (DELETE) |
| 400    | Bad Request           | Dados de entrada inválidos            |
| 401    | Unauthorized          | Credenciais ausentes ou inválidas     |
| 403    | Forbidden             | Usuário sem permissão                 |
| 404    | Not Found             | Recurso não encontrado                |
| 500    | Internal Server Error | Erro no servidor                      |

### Exemplo de Resposta de Erro

```json
{
  "timestamp": "2025-10-03T04:15:30.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/transacoes"
}
```

### Tratamento no Frontend

```javascript
async function createTransacao(transacao) {
  try {
    const response = await fetch(`${API_URL}/api/transacoes`, {
      method: "POST",
      headers,
      body: JSON.stringify(transacao),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Erro ao criar transação");
    }

    return await response.json();
  } catch (error) {
    console.error("Erro:", error.message);
    // Mostrar mensagem para o usuário
    alert("Erro ao criar transação: " + error.message);
  }
}
```

---

## 🔍 Dados de Exemplo Disponíveis

A API já possui dados de teste carregados:

### Categorias (11 total)

**Receitas:**

- Salário
- Freelance
- Investimentos
- Vendas

**Despesas:**

- Alimentação
- Transporte
- Moradia
- Saúde
- Lazer
- Educação
- Utilities (Energia, Água, Internet)

### Transações (30 total)

- **Agosto/2025:** 10 transações (3 receitas, 7 despesas)
- **Setembro/2025:** 20 transações (incluindo vendas e projetos freelance)

### Usuários (3 total)

- Admin Sistema (ADMIN)
- João Silva (USER)
- Maria Santos (USER)

---

## 📖 Documentação Swagger

Acesse a documentação interativa da API:

```
https://financa-pessoal-production.up.railway.app/swagger-ui.html
```

**Nota:** Para testar endpoints protegidos no Swagger, clique em "Authorize" e insira:

- Username: `admin@financeiro.com`
- Password: `password`

---

## 🚀 Próximos Passos

1. ✅ Configure o cliente HTTP (Axios/Fetch) com as credenciais
2. ✅ Teste os endpoints principais no Postman/cURL
3. ✅ Implemente o serviço de autenticação no frontend
4. ✅ Crie os serviços para cada recurso (usuários, categorias, transações)
5. ✅ Implemente o gerenciamento de estado (Context API/Redux/Zustand)
6. ✅ Adicione tratamento de erros e loading states
7. ✅ Implemente validações de formulário matching as regras da API

---

## 💡 Dicas e Boas Práticas

### 1. Armazene Credenciais com Segurança

```javascript
// ❌ Não faça isso
const password = "password";

// ✅ Use variáveis de ambiente
const API_USER = process.env.REACT_APP_API_USER;
const API_PASSWORD = process.env.REACT_APP_API_PASSWORD;
```

### 2. Interceptors para Autenticação

```javascript
// Axios
api.interceptors.request.use((config) => {
  const credentials = btoa(`${API_USER}:${API_PASSWORD}`);
  config.headers.Authorization = `Basic ${credentials}`;
  return config;
});
```

### 3. Cache de Dados

```javascript
// React Query
import { useQuery } from "@tanstack/react-query";

export const useCategorias = () => {
  return useQuery({
    queryKey: ["categorias"],
    queryFn: getCategorias,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
};
```

### 4. Formatação de Datas

```javascript
// Enviar para API: YYYY-MM-DD
const dataFormatada = new Date().toISOString().split("T")[0];

// Exibir no frontend: DD/MM/YYYY
const dataExibicao = new Date("2025-08-01").toLocaleDateString("pt-BR");
```

### 5. Formatação de Valores

```javascript
const formatarMoeda = (valor) => {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(valor);
};

// R$ 5.500,00
formatarMoeda(5500);
```

---

## 🆘 Suporte

Se encontrar problemas ou tiver dúvidas:

1. Verifique os logs no Railway Dashboard
2. Teste o endpoint diretamente com cURL
3. Valide o formato dos dados enviados
4. Confirme que as credenciais estão corretas
5. Verifique se o usuário tem permissão para a operação

---

## 📝 Changelog da API

### Versão 1.0.0 (Atual)

- ✅ Autenticação Basic Auth
- ✅ CRUD completo de Usuários, Categorias e Transações
- ✅ Filtros avançados de transações
- ✅ Relatório de resumo mensal
- ✅ Validações de entrada
- ✅ Documentação Swagger
- ✅ CORS habilitado para desenvolvimento
- ✅ Dados de exemplo pré-carregados

---

**Data de Última Atualização:** 03 de Outubro de 2025  
**Ambiente:** Produção (Railway)  
**Versão da API:** 1.0.0
