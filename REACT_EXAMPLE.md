# 💻 Exemplo Completo - React Integration

Este é um exemplo prático e completo de como integrar o frontend React com a API.

---

## 📁 Estrutura de Arquivos

```
meu-app-financeiro/
├── .env.development
├── .env.production
├── .gitignore
├── package.json
├── public/
└── src/
    ├── config/
    │   └── api.js
    ├── services/
    │   ├── categoriaService.js
    │   └── transacaoService.js
    ├── components/
    │   ├── Categorias/
    │   │   ├── CategoriaList.jsx
    │   │   └── CategoriaForm.jsx
    │   ├── Transacoes/
    │   │   ├── TransacaoList.jsx
    │   │   ├── TransacaoForm.jsx
    │   │   └── ResumoMensal.jsx
    │   └── common/
    │       └── EnvironmentBadge.jsx
    ├── App.jsx
    └── index.js
```

---

## 1️⃣ Arquivos de Configuração

### `.env.development`

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=development
```

### `.env.production`

```env
REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app
REACT_APP_API_USER=admin@financeiro.com
REACT_APP_API_PASSWORD=password
REACT_APP_ENVIRONMENT=production
```

### `.gitignore`

```
node_modules/
build/
.env.local
.DS_Store
```

### `package.json`

```json
{
  "name": "meu-app-financeiro",
  "version": "1.0.0",
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "axios": "^1.6.0"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test"
  }
}
```

---

## 2️⃣ Configuração da API

### `src/config/api.js`

```javascript
import axios from "axios";

// Cria instância do Axios com URL base das variáveis de ambiente
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080",
});

// Interceptor para adicionar autenticação em todas as requisições
api.interceptors.request.use(
  (config) => {
    const username = process.env.REACT_APP_API_USER;
    const password = process.env.REACT_APP_API_PASSWORD;

    if (username && password) {
      // Codifica credenciais em Base64 para Basic Auth
      const credentials = btoa(`${username}:${password}`);
      config.headers.Authorization = `Basic ${credentials}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Erro com resposta do servidor
      switch (error.response.status) {
        case 401:
          console.error("Não autorizado - verifique as credenciais");
          break;
        case 404:
          console.error("Recurso não encontrado");
          break;
        case 500:
          console.error("Erro interno do servidor");
          break;
        default:
          console.error("Erro na requisição:", error.response.data);
      }
    } else if (error.request) {
      // Requisição foi feita mas não houve resposta
      console.error("Sem resposta do servidor");
    } else {
      // Erro ao configurar a requisição
      console.error("Erro:", error.message);
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 3️⃣ Serviços

### `src/services/categoriaService.js`

```javascript
import api from "../config/api";

export const categoriaService = {
  // Listar todas as categorias
  getAll: async () => {
    try {
      const { data } = await api.get("/api/categorias");
      return data;
    } catch (error) {
      throw new Error("Erro ao listar categorias");
    }
  },

  // Listar por tipo (RECEITA ou DESPESA)
  getByTipo: async (tipo) => {
    try {
      const { data } = await api.get("/api/categorias", {
        params: { tipo },
      });
      return data;
    } catch (error) {
      throw new Error(`Erro ao listar categorias do tipo ${tipo}`);
    }
  },

  // Buscar categoria por ID
  getById: async (id) => {
    try {
      const { data } = await api.get(`/api/categorias/${id}`);
      return data;
    } catch (error) {
      throw new Error("Erro ao buscar categoria");
    }
  },

  // Criar nova categoria
  create: async (categoria) => {
    try {
      const { data } = await api.post("/api/categorias", categoria);
      return data;
    } catch (error) {
      throw new Error("Erro ao criar categoria");
    }
  },

  // Atualizar categoria
  update: async (id, categoria) => {
    try {
      const { data } = await api.put(`/api/categorias/${id}`, categoria);
      return data;
    } catch (error) {
      throw new Error("Erro ao atualizar categoria");
    }
  },

  // Ativar categoria
  ativar: async (id) => {
    try {
      const { data } = await api.patch(`/api/categorias/${id}/ativar`);
      return data;
    } catch (error) {
      throw new Error("Erro ao ativar categoria");
    }
  },

  // Desativar categoria
  desativar: async (id) => {
    try {
      const { data } = await api.patch(`/api/categorias/${id}/desativar`);
      return data;
    } catch (error) {
      throw new Error("Erro ao desativar categoria");
    }
  },

  // Deletar categoria
  delete: async (id) => {
    try {
      await api.delete(`/api/categorias/${id}`);
    } catch (error) {
      throw new Error("Erro ao deletar categoria");
    }
  },
};
```

### `src/services/transacaoService.js`

```javascript
import api from "../config/api";

export const transacaoService = {
  // Listar todas as transações com filtros opcionais
  getAll: async (filtros = {}) => {
    try {
      const { data } = await api.get("/api/transacoes", {
        params: filtros,
      });
      return data;
    } catch (error) {
      throw new Error("Erro ao listar transações");
    }
  },

  // Buscar transação por ID
  getById: async (id) => {
    try {
      const { data } = await api.get(`/api/transacoes/${id}`);
      return data;
    } catch (error) {
      throw new Error("Erro ao buscar transação");
    }
  },

  // Criar nova transação
  create: async (transacao) => {
    try {
      const { data } = await api.post("/api/transacoes", transacao);
      return data;
    } catch (error) {
      throw new Error("Erro ao criar transação");
    }
  },

  // Atualizar transação
  update: async (id, transacao) => {
    try {
      const { data } = await api.put(`/api/transacoes/${id}`, transacao);
      return data;
    } catch (error) {
      throw new Error("Erro ao atualizar transação");
    }
  },

  // Deletar transação
  delete: async (id) => {
    try {
      await api.delete(`/api/transacoes/${id}`);
    } catch (error) {
      throw new Error("Erro ao deletar transação");
    }
  },

  // Buscar resumo mensal
  getResumoMensal: async (ano, mes) => {
    try {
      const { data } = await api.get("/api/transacoes/resumo-mensal", {
        params: { ano, mes },
      });
      return data;
    } catch (error) {
      throw new Error("Erro ao buscar resumo mensal");
    }
  },
};
```

---

## 4️⃣ Componentes

### `src/components/Categorias/CategoriaList.jsx`

```javascript
import React, { useState, useEffect } from "react";
import { categoriaService } from "../../services/categoriaService";

const CategoriaList = () => {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtroTipo, setFiltroTipo] = useState("");

  useEffect(() => {
    loadCategorias();
  }, [filtroTipo]);

  const loadCategorias = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = filtroTipo
        ? await categoriaService.getByTipo(filtroTipo)
        : await categoriaService.getAll();

      setCategorias(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleAtivo = async (id, ativa) => {
    try {
      if (ativa) {
        await categoriaService.desativar(id);
      } else {
        await categoriaService.ativar(id);
      }
      loadCategorias(); // Recarrega lista
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) {
    return <div>Carregando categorias...</div>;
  }

  if (error) {
    return <div style={{ color: "red" }}>Erro: {error}</div>;
  }

  return (
    <div>
      <h2>Categorias</h2>

      {/* Filtro por tipo */}
      <div style={{ marginBottom: "20px" }}>
        <label>Filtrar por tipo: </label>
        <select
          value={filtroTipo}
          onChange={(e) => setFiltroTipo(e.target.value)}
        >
          <option value="">Todas</option>
          <option value="RECEITA">Receitas</option>
          <option value="DESPESA">Despesas</option>
        </select>
      </div>

      {/* Lista */}
      <table border="1" cellPadding="10">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Tipo</th>
            <th>Descrição</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {categorias.map((cat) => (
            <tr key={cat.id}>
              <td>{cat.nome}</td>
              <td>
                <span
                  style={{
                    color: cat.tipo === "RECEITA" ? "green" : "red",
                  }}
                >
                  {cat.tipo}
                </span>
              </td>
              <td>{cat.descricao}</td>
              <td>{cat.ativa ? "✅ Ativa" : "❌ Inativa"}</td>
              <td>
                <button onClick={() => handleToggleAtivo(cat.id, cat.ativa)}>
                  {cat.ativa ? "Desativar" : "Ativar"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <p>Total: {categorias.length} categoria(s)</p>
    </div>
  );
};

export default CategoriaList;
```

### `src/components/Transacoes/TransacaoList.jsx`

```javascript
import React, { useState, useEffect } from "react";
import { transacaoService } from "../../services/transacaoService";

const TransacaoList = () => {
  const [transacoes, setTransacoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filtros, setFiltros] = useState({
    tipo: "",
    dataInicio: "",
    dataFim: "",
  });

  useEffect(() => {
    loadTransacoes();
  }, []);

  const loadTransacoes = async () => {
    try {
      setLoading(true);
      setError(null);

      // Remove filtros vazios
      const filtrosAtivos = Object.fromEntries(
        Object.entries(filtros).filter(([_, value]) => value !== "")
      );

      const data = await transacaoService.getAll(filtrosAtivos);
      setTransacoes(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleFiltroChange = (campo, valor) => {
    setFiltros({ ...filtros, [campo]: valor });
  };

  const handleDelete = async (id) => {
    if (window.confirm("Deseja realmente deletar esta transação?")) {
      try {
        await transacaoService.delete(id);
        loadTransacoes(); // Recarrega lista
      } catch (err) {
        alert(err.message);
      }
    }
  };

  const formatarMoeda = (valor) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  const formatarData = (data) => {
    return new Date(data).toLocaleDateString("pt-BR");
  };

  if (loading) {
    return <div>Carregando transações...</div>;
  }

  if (error) {
    return <div style={{ color: "red" }}>Erro: {error}</div>;
  }

  return (
    <div>
      <h2>Transações</h2>

      {/* Filtros */}
      <div
        style={{
          marginBottom: "20px",
          padding: "10px",
          border: "1px solid #ccc",
        }}
      >
        <h3>Filtros</h3>
        <div>
          <label>Tipo: </label>
          <select
            value={filtros.tipo}
            onChange={(e) => handleFiltroChange("tipo", e.target.value)}
          >
            <option value="">Todos</option>
            <option value="RECEITA">Receitas</option>
            <option value="DESPESA">Despesas</option>
          </select>
        </div>
        <div>
          <label>Data Início: </label>
          <input
            type="date"
            value={filtros.dataInicio}
            onChange={(e) => handleFiltroChange("dataInicio", e.target.value)}
          />
        </div>
        <div>
          <label>Data Fim: </label>
          <input
            type="date"
            value={filtros.dataFim}
            onChange={(e) => handleFiltroChange("dataFim", e.target.value)}
          />
        </div>
        <button onClick={loadTransacoes}>Aplicar Filtros</button>
      </div>

      {/* Lista */}
      <table border="1" cellPadding="10">
        <thead>
          <tr>
            <th>Data</th>
            <th>Descrição</th>
            <th>Categoria</th>
            <th>Tipo</th>
            <th>Valor</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {transacoes.map((trans) => (
            <tr key={trans.id}>
              <td>{formatarData(trans.dataTransacao)}</td>
              <td>{trans.descricao}</td>
              <td>{trans.categoria?.nome || "N/A"}</td>
              <td>
                <span
                  style={{
                    color: trans.tipo === "RECEITA" ? "green" : "red",
                    fontWeight: "bold",
                  }}
                >
                  {trans.tipo}
                </span>
              </td>
              <td
                style={{
                  color: trans.tipo === "RECEITA" ? "green" : "red",
                  fontWeight: "bold",
                }}
              >
                {formatarMoeda(trans.valor)}
              </td>
              <td>
                <button onClick={() => handleDelete(trans.id)}>Deletar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <p>Total: {transacoes.length} transação(ões)</p>
    </div>
  );
};

export default TransacaoList;
```

### `src/components/Transacoes/ResumoMensal.jsx`

```javascript
import React, { useState } from "react";
import { transacaoService } from "../../services/transacaoService";

const ResumoMensal = () => {
  const [resumo, setResumo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [ano, setAno] = useState(new Date().getFullYear());
  const [mes, setMes] = useState(new Date().getMonth() + 1);

  const loadResumo = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await transacaoService.getResumoMensal(ano, mes);
      setResumo(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatarMoeda = (valor) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor);
  };

  const meses = [
    "Janeiro",
    "Fevereiro",
    "Março",
    "Abril",
    "Maio",
    "Junho",
    "Julho",
    "Agosto",
    "Setembro",
    "Outubro",
    "Novembro",
    "Dezembro",
  ];

  return (
    <div>
      <h2>Resumo Mensal</h2>

      {/* Seleção de período */}
      <div style={{ marginBottom: "20px" }}>
        <label>Mês: </label>
        <select value={mes} onChange={(e) => setMes(Number(e.target.value))}>
          {meses.map((nome, index) => (
            <option key={index} value={index + 1}>
              {nome}
            </option>
          ))}
        </select> <label>Ano: </label>
        <input
          type="number"
          value={ano}
          onChange={(e) => setAno(Number(e.target.value))}
          min="2000"
          max="2100"
        />{" "}
        <button onClick={loadResumo}>Buscar</button>
      </div>

      {/* Loading/Error */}
      {loading && <div>Carregando resumo...</div>}
      {error && <div style={{ color: "red" }}>Erro: {error}</div>}

      {/* Resumo */}
      {resumo && (
        <div
          style={{
            border: "1px solid #ccc",
            padding: "20px",
            borderRadius: "8px",
          }}
        >
          <h3>
            {meses[mes - 1]} de {ano}
          </h3>

          <div style={{ marginTop: "20px" }}>
            <div style={{ marginBottom: "10px" }}>
              <strong>Total de Receitas:</strong>{" "}
              <span style={{ color: "green", fontSize: "1.2em" }}>
                {formatarMoeda(resumo.totalReceitas)}
              </span>{" "}
              ({resumo.quantidadeReceitas} transação(ões))
            </div>

            <div style={{ marginBottom: "10px" }}>
              <strong>Total de Despesas:</strong>{" "}
              <span style={{ color: "red", fontSize: "1.2em" }}>
                {formatarMoeda(resumo.totalDespesas)}
              </span>{" "}
              ({resumo.quantidadeDespesas} transação(ões))
            </div>

            <hr />

            <div style={{ marginTop: "10px" }}>
              <strong>Saldo do Período:</strong>{" "}
              <span
                style={{
                  color: resumo.saldo >= 0 ? "green" : "red",
                  fontSize: "1.5em",
                  fontWeight: "bold",
                }}
              >
                {formatarMoeda(resumo.saldo)}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResumoMensal;
```

### `src/components/common/EnvironmentBadge.jsx`

```javascript
import React from "react";

const EnvironmentBadge = () => {
  const env = process.env.REACT_APP_ENVIRONMENT || "development";
  const apiUrl = process.env.REACT_APP_API_URL;

  // Não mostrar em produção
  if (env === "production") {
    return null;
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
        boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
      }}
    >
      <div>🔧 {env.toUpperCase()}</div>
      <div style={{ fontSize: 10, marginTop: 4 }}>{apiUrl}</div>
    </div>
  );
};

export default EnvironmentBadge;
```

---

## 5️⃣ App Principal

### `src/App.jsx`

```javascript
import React from "react";
import CategoriaList from "./components/Categorias/CategoriaList";
import TransacaoList from "./components/Transacoes/TransacaoList";
import ResumoMensal from "./components/Transacoes/ResumoMensal";
import EnvironmentBadge from "./components/common/EnvironmentBadge";

function App() {
  return (
    <div style={{ padding: "20px" }}>
      <h1>💰 Sistema Financeiro Pessoal</h1>

      <EnvironmentBadge />

      <hr />
      <CategoriaList />

      <hr />
      <TransacaoList />

      <hr />
      <ResumoMensal />
    </div>
  );
}

export default App;
```

---

## 🚀 Como Executar

### 1. Instalar Dependências

```bash
npm install
```

### 2. Iniciar Backend (Terminal separado)

```bash
cd /Users/chrmartins/projetos/financa-pessoal
docker-compose up postgres
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 3. Iniciar Frontend

```bash
# Desenvolvimento (backend local)
npm start

# Produção (backend Railway)
REACT_APP_ENV=production npm start
```

---

## ✅ O Que Este Exemplo Demonstra

- ✅ Configuração completa de Axios com interceptors
- ✅ Separação de serviços por recurso
- ✅ Componentes React com hooks (useState, useEffect)
- ✅ Listagem com filtros
- ✅ CRUD de categorias e transações
- ✅ Relatório de resumo mensal
- ✅ Formatação de valores monetários
- ✅ Formatação de datas
- ✅ Tratamento de erros
- ✅ Loading states
- ✅ Badge de ambiente (DEV/PROD)

---

## 📝 Próximos Passos

Após testar este exemplo:

1. ✅ Adicione formulários para criar/editar transações
2. ✅ Implemente paginação nas listagens
3. ✅ Adicione gráficos de visualização
4. ✅ Implemente autenticação de verdade (login form)
5. ✅ Adicione validações de formulário
6. ✅ Melhore o design com CSS/styled-components

---

**Status:** ✅ Exemplo completo e funcional  
**Data:** 04 de Outubro de 2025
