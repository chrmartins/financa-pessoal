# Guia de Deploy Railway - Sistema Financeiro Pessoal

## ⚠️ PROBLEMA PRINCIPAL IDENTIFICADO

**Após 6+ horas de tentativas, os principais problemas eram:**

1. **Variáveis ENV no Dockerfile em ordem incorreta** ✅ CORRIGIDO
2. **Variáveis obrigatórias não configuradas no Railway** ⚠️ AÇÃO NECESSÁRIA
3. **Healthcheck timeout muito alto** ✅ CORRIGIDO
4. **Possível conflito nixpacks.toml vs Dockerfile** ✅ ESCLARECIDO

## 🚀 PASSO A PASSO PARA DEPLOY

### 1. Configurar PostgreSQL no Railway

```bash
# No painel Railway:
1. Adicionar serviço PostgreSQL
2. Anotar as variáveis geradas (DATABASE_URL, PGHOST, etc.)
```

### 2. Configurar Variáveis de Ambiente da Aplicação

**No serviço da aplicação, adicionar:**

```env
# OBRIGATÓRIAS
JWT_SECRET=seu_jwt_secret_super_seguro_aqui_min_32_chars
SPRING_PROFILES_ACTIVE=prod

# OPCIONAIS
CORS_ALLOWED_ORIGINS=https://seu-frontend.com,https://outro-dominio.com
SWAGGER_ENABLED=false
JWT_EXPIRATION=86400000
```

### 3. Vincular PostgreSQL à Aplicação

```
No Railway:
1. Vá ao serviço da aplicação
2. Variables tab
3. Reference PostgreSQL service variables:
   - DATABASE_URL (referência do Postgres)
   - Ou use PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD
```

### 4. Deploy

```bash
git add .
git commit -m "fix: correções para deploy Railway"
git push origin main
```

## 🔍 VARIÁVEIS NECESSÁRIAS

### Obrigatórias

- `JWT_SECRET` - Chave secreta para JWT (mín. 32 caracteres)
- `DATABASE_URL` - URL do PostgreSQL (gerada automaticamente pelo Railway)
- `SPRING_PROFILES_ACTIVE=prod`

### Opcionais com defaults

- `PORT=8080` (Railway define automaticamente)
- `CORS_ALLOWED_ORIGINS=http://localhost:3000`
- `SWAGGER_ENABLED=false`
- `JWT_EXPIRATION=86400000`

## 🐳 Arquivos de Deploy

### railway.json

- ✅ Configurado para usar Dockerfile
- ✅ Healthcheck em `/actuator/health`
- ✅ Timeout ajustado para 120s

### Dockerfile

- ✅ Ordem correta das variáveis ENV
- ✅ Java 21 + Spring Boot
- ✅ Gradle build otimizado

## 🔧 Debugging

### Se ainda falhar, verificar logs:

1. Build logs - erros de compilação
2. Deploy logs - erros de startup
3. Runtime logs - erros de conexão DB

### Logs importantes para procurar:

- "Variáveis de ambiente detectadas..." (do RailwayDataSourceConfig)
- "Started FinancasPessoalApplication"
- Erros de conexão com PostgreSQL

## 💡 PRÓXIMOS PASSOS

1. **Configure JWT_SECRET** (crítico)
2. **Vincule o PostgreSQL** ao serviço da app
3. **Faça o deploy**
4. **Monitore os logs**

Se ainda der erro, compartilhe os logs específicos do Railway para análise mais detalhada.
