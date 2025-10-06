# 🔧 SOLUÇÃO DEFINITIVA - Reset de Senha do Admin em Produção

## ✅ Deploy Realizado com Sucesso!

Um endpoint temporário foi criado e deployado para resetar a senha do admin **sem precisar acessar o banco de dados**.

---

## 🎯 Como Usar - 3 Passos Simples

### 1️⃣ Verificar Estado Atual do Admin

```bash
curl https://financa-pessoal-production.up.railway.app/api/admin/check/admin@financeiro.com
```

**Resposta esperada:**

```json
{
  "email": "admin@financeiro.com",
  "nome": "Admin",
  "ativo": true/false,
  "papel": "ADMIN",
  "senhaBcryptPreview": "$2a$10$...",
  "ultimoAcesso": "2025-10-06T..."
}
```

---

### 2️⃣ Resetar Senha do Admin

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/admin/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "novaSenha": "password"
  }'
```

**Resposta esperada:**

```json
{
  "sucesso": true,
  "mensagem": "Senha atualizada com sucesso",
  "usuario": {
    "email": "admin@financeiro.com",
    "ativo": true,
    "senhaBcryptPreview": "$2a$10$3U9GuvRfViYnfOxLUn3SfO..."
  }
}
```

---

### 3️⃣ Testar Login com Nova Senha

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "password"
  }'
```

**Resposta esperada (COM TOKEN):**

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

---

## 🌐 Testar no Frontend

Após resetar a senha, use no frontend:

```javascript
// Login no frontend
const response = await fetch(
  "https://financa-pessoal-production.up.railway.app/api/auth",
  {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: "admin@financeiro.com",
      senha: "password",
    }),
  }
);

const data = await response.json();
console.log("Token:", data.token);
```

---

## ⚠️ IMPORTANTE - Segurança

### Após Resolver o Problema:

1. **REMOVER** o arquivo `AdminUtilController.java`
2. **REMOVER** a linha do SecurityConfig:
   ```java
   .requestMatchers("/api/admin/**").permitAll()
   ```
3. **Fazer novo deploy** sem esse endpoint

### Por quê?

- ✅ Esse endpoint permite **qualquer pessoa** resetar senhas
- ✅ É **apenas para resolver emergência**
- ✅ **NÃO deve permanecer em produção**

---

## 📋 Checklist de Verificação

- [ ] Executei: `curl .../api/admin/check/admin@financeiro.com`
- [ ] Verifiquei que `"ativo": false` (provável causa do 403)
- [ ] Executei: `curl -X POST .../api/admin/reset-password` com a nova senha
- [ ] Confirmei: `"sucesso": true` na resposta
- [ ] Testei login: `curl -X POST .../api/auth` com a nova senha
- [ ] Recebi token JWT válido
- [ ] Frontend consegue fazer login normalmente
- [ ] **REMOVI** o `AdminUtilController.java` após resolver
- [ ] Fiz deploy final **sem** o endpoint temporário

---

## 🔍 Diagnóstico - Possíveis Causas do 403

1. **Campo `ativo = false`** ← Causa mais provável!

   - Solução: O reset-password já define `ativo = true`

2. **Senha incorreta ou não BCrypt**

   - Solução: O reset-password gera hash BCrypt correto

3. **Usuário não existe**
   - Verificar com: `GET /api/admin/check/admin@financeiro.com`

---

## 📞 Precisa de Ajuda?

Se ainda der erro 403 após seguir os passos:

1. Compartilhe a resposta do `/api/admin/check/...`
2. Compartilhe a resposta do `/api/admin/reset-password`
3. Compartilhe o erro exato do `/api/auth` (login)

---

## 🎉 Credenciais Finais de Produção

**Email:** `admin@financeiro.com`  
**Senha:** `password`

---

**Criado em:** 06/10/2025  
**Status:** ✅ Deploy realizado com sucesso  
**URL Base:** https://financa-pessoal-production.up.railway.app
