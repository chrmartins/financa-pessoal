# 👥 API de Gerenciamento de Usuários

## 🔐 Autenticação e Autorização

- ✅ **Endpoints de leitura (GET):** Qualquer usuário autenticado
- 🔒 **Endpoints de escrita (POST/PUT/DELETE):** Apenas **ADMIN**

---

## 📋 Endpoints Disponíveis

### 1️⃣ Listar Todos os Usuários Ativos

```bash
GET /api/usuarios
```

**Headers:**

```
Authorization: Bearer {token}
```

**Resposta 200 OK:**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "nome": "Admin Sistema",
    "email": "admin@financeiro.com",
    "papel": "ADMIN",
    "ativo": true,
    "dataCriacao": "2025-10-02T01:43:13.165",
    "dataAtualizacao": "2025-10-06T21:18:09.003",
    "ultimoAcesso": "2025-10-06T21:18:09.001"
  }
]
```

---

### 2️⃣ Obter Usuário Atual (Logado)

```bash
GET /api/usuarios/atual
```

**Headers:**

```
Authorization: Bearer {token}
```

**Resposta 200 OK:**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "nome": "Admin Sistema",
  "email": "admin@financeiro.com",
  "papel": "ADMIN",
  "ativo": true,
  "dataCriacao": "2025-10-02T01:43:13.165",
  "dataAtualizacao": "2025-10-06T21:18:09.003",
  "ultimoAcesso": "2025-10-06T21:18:09.001"
}
```

---

### 3️⃣ Buscar Usuário por ID

```bash
GET /api/usuarios/{id}
```

**Headers:**

```
Authorization: Bearer {token}
```

**Exemplo:**

```bash
curl https://financa-pessoal-production.up.railway.app/api/usuarios/550e8400-e29b-41d4-a716-446655440003 \
  -H "Authorization: Bearer {token}"
```

---

### 4️⃣ Buscar Usuário por Email

```bash
GET /api/usuarios/email/{email}
```

**Headers:**

```
Authorization: Bearer {token}
```

**Exemplo:**

```bash
curl https://financa-pessoal-production.up.railway.app/api/usuarios/email/admin@financeiro.com \
  -H "Authorization: Bearer {token}"
```

---

### 5️⃣ ✨ Criar Novo Usuário (🔒 ADMIN)

```bash
POST /api/usuarios
```

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**

```json
{
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "senha": "senha123",
  "papel": "USER",
  "ativo": true
}
```

**Papéis disponíveis:**

- `ADMIN` - Administrador (acesso total)
- `USER` - Usuário comum (acesso limitado)

**Validações:**

- ✅ Nome: 2-100 caracteres
- ✅ Email: formato válido e único
- ✅ Senha: mínimo 6 caracteres (será hashada com BCrypt)
- ✅ Papel: obrigatório (ADMIN ou USER)

**Exemplo com cURL:**

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/usuarios \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@exemplo.com",
    "senha": "senha123",
    "papel": "USER",
    "ativo": true
  }'
```

**Resposta 201 Created:**

```json
{
  "id": "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d",
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "papel": "USER",
  "ativo": true,
  "dataCriacao": "2025-10-06T22:30:00.000",
  "dataAtualizacao": null,
  "ultimoAcesso": null
}
```

**Resposta 409 Conflict:** (email já existe)
**Resposta 403 Forbidden:** (usuário não é ADMIN)

---

### 6️⃣ Atualizar Usuário (🔒 ADMIN)

```bash
PUT /api/usuarios/{id}
```

**Headers:**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body** (todos os campos são opcionais):

```json
{
  "nome": "João Silva Atualizado",
  "email": "joao.novo@exemplo.com",
  "senha": "novaSenha123",
  "papel": "ADMIN",
  "ativo": false
}
```

**Exemplo com cURL:**

```bash
curl -X PUT https://financa-pessoal-production.up.railway.app/api/usuarios/a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Atualizado",
    "papel": "ADMIN"
  }'
```

**Resposta 200 OK:**

```json
{
  "id": "a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d",
  "nome": "João Silva Atualizado",
  "email": "joao@exemplo.com",
  "papel": "ADMIN",
  "ativo": true,
  "dataCriacao": "2025-10-06T22:30:00.000",
  "dataAtualizacao": "2025-10-06T22:45:00.000",
  "ultimoAcesso": null
}
```

**Resposta 404 Not Found:** (usuário não existe)
**Resposta 403 Forbidden:** (usuário não é ADMIN)

---

### 7️⃣ Desativar Usuário - Soft Delete (🔒 ADMIN)

```bash
DELETE /api/usuarios/{id}
```

**Headers:**

```
Authorization: Bearer {token}
```

**Exemplo com cURL:**

```bash
curl -X DELETE https://financa-pessoal-production.up.railway.app/api/usuarios/a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d \
  -H "Authorization: Bearer {token}"
```

**Resposta 204 No Content:** (usuário desativado com sucesso)
**Resposta 404 Not Found:** (usuário não existe)
**Resposta 403 Forbidden:** (usuário não é ADMIN)

⚠️ **Nota:** Este endpoint não remove o usuário do banco, apenas define `ativo = false`

---

## 🧪 Testando em Produção

### 1. Fazer Login como Admin

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@financeiro.com",
    "senha": "teste123"
  }'
```

**Salve o token retornado:**

```bash
export TOKEN="eyJhbGciOiJIUzM4NCJ9..."
```

### 2. Criar Novo Usuário

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/usuarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@teste.com",
    "senha": "maria123",
    "papel": "USER",
    "ativo": true
  }' | python3 -m json.tool
```

### 3. Listar Todos os Usuários

```bash
curl https://financa-pessoal-production.up.railway.app/api/usuarios \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

### 4. Atualizar Usuário para ADMIN

```bash
curl -X PUT https://financa-pessoal-production.up.railway.app/api/usuarios/{id} \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "papel": "ADMIN"
  }' | python3 -m json.tool
```

---

## 🔒 Segurança Implementada

### ✅ Proteções Ativas:

1. **Autenticação JWT:** Todos os endpoints requerem token válido
2. **Autorização por Papel:**
   - `@PreAuthorize("hasRole('ADMIN')")` em POST/PUT/DELETE
   - Apenas admin pode criar/modificar usuários
3. **Hash de Senha:** BCrypt automático na criação/atualização
4. **Validação de Email:** Email único no sistema
5. **Soft Delete:** Usuários desativados, não removidos

### 🛡️ Validações de Entrada:

- ✅ `@Valid` em todos os requests
- ✅ `@NotBlank` para campos obrigatórios
- ✅ `@Email` para formato de email
- ✅ `@Size` para tamanho mínimo/máximo

---

## 📁 Arquivos Criados/Modificados:

1. ✅ `CriarUsuarioRequest.java` - DTO para criar usuário
2. ✅ `AtualizarUsuarioRequest.java` - DTO para atualizar usuário
3. ✅ `UsuarioController.java` - Controller com CRUD completo

---

## 🚀 Deploy

Os novos endpoints já estão **compilados** e prontos. Para fazer deploy:

```bash
./gradlew clean build -x test
railway up
```

---

**Criado em:** 06/10/2025  
**Status:** ✅ Implementado e compilado com sucesso  
**Segurança:** 🔒 Apenas ADMIN pode criar/modificar usuários
