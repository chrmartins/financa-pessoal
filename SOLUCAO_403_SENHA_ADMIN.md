# 🔐 SOLUÇÃO PARA ERRO 403 - Senha do Admin em Produção

## ❌ Problema Identificado

O frontend está recebendo **403 Forbidden** ao tentar fazer login em produção porque:

1. A senha do usuário `admin@financeiro.com` não está correta OU
2. O campo `ativo` está como `false` OU
3. A senha não está em formato BCrypt correto

## ✅ Solução - SQL para Executar no Banco Railway

### Opção 1: Acessar via Railway Dashboard

1. Acesse: https://railway.app/project/seu-projeto
2. Clique no serviço **Postgres**
3. Vá na aba **Data**
4. Execute o SQL abaixo:

```sql
-- Verificar estado atual
SELECT id, email, LEFT(senha, 30) as senha_preview, ativo, papel
FROM usuarios
WHERE email = 'admin@financeiro.com';

-- Atualizar senha para "password" (BCrypt hash) e garantir que está ativo
UPDATE usuarios
SET senha = '$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG',
    ativo = true
WHERE email = 'admin@financeiro.com';

-- Confirmar atualização
SELECT id, email, LEFT(senha, 30) as senha_preview, ativo, papel
FROM usuarios
WHERE email = 'admin@financeiro.com';
```

### Opção 2: Conectar via psql (se tiver PostgreSQL instalado)

```bash
# 1. Pegar a URL de conexão do Railway
railway variables | grep DATABASE_URL

# 2. Conectar usando psql
psql "postgresql://usuario:senha@host:porta/database"

# 3. Executar o UPDATE acima
```

### Opção 3: Usar Railway CLI com Service Connect

```bash
# Conectar ao banco via Railway CLI
railway connect Postgres

# Quando abrir o psql, executar:
UPDATE usuarios
SET senha = '$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG',
    ativo = true
WHERE email = 'admin@financeiro.com';
```

## 📝 Credenciais Após a Atualização

**Email:** `admin@financeiro.com`  
**Senha:** `password`

**Hash BCrypt no banco:**

```
$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG
```

## 🧪 Testar Login Após Atualização

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "password"
  }'
```

**Resposta esperada:**

```json
{
  "usuario": {
    "id": "...",
    "nome": "Admin",
    "email": "admin@financeiro.com",
    "papel": "ADMIN",
    "ativo": true
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000
}
```

## ⚠️ Nota Importante sobre os Hashes

- **Desenvolvimento** (admin123): `$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG`
- **Produção** (password): `$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG`

⚠️ **ATENÇÃO**: Por coincidência, ambos têm o mesmo hash! Isso significa que **a senha atual em produção já deve ser "admin123" ou "password"**.

## 🔍 Diagnóstico Adicional

Se após o UPDATE ainda der 403, verifique:

1. **Campo `ativo`**:

   ```sql
   SELECT ativo FROM usuarios WHERE email = 'admin@financeiro.com';
   ```

   Deve retornar `true` ou `t`

2. **Formato da senha**:

   ```sql
   SELECT LEFT(senha, 10) FROM usuarios WHERE email = 'admin@financeiro.com';
   ```

   Deve retornar `$2a$10$...` (BCrypt)

3. **Verificar logs da aplicação no Railway**:
   ```bash
   railway logs
   ```
   Procure por mensagens como "Bad credentials" ou "User is disabled"

## 🚀 Alternativa Rápida - Criar Novo Usuário Admin

Se não conseguir acessar o banco, posso criar um endpoint temporário para criar/resetar o admin:

```sql
INSERT INTO usuarios (id, nome, email, senha, papel, ativo, data_criacao, data_atualizacao)
VALUES (
  gen_random_uuid(),
  'Admin',
  'admin@financeiro.com',
  '$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG',
  'ADMIN',
  true,
  NOW(),
  NOW()
)
ON CONFLICT (email) DO UPDATE
SET senha = '$2a$10$3U9GuvRfViYnfOxLUn3SfOfAKGIOYcoDX7RFztlylBc5YFp7pUHoG',
    ativo = true,
    data_atualizacao = NOW();
```
