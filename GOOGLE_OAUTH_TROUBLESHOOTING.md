# 🔍 Troubleshooting - Google OAuth no Railway

## ✅ Verificação Completa Realizada

### 1. **Controller `/api/auth/google`** ✅

- **Arquivo**: `AuthenticationController.java`
- **Método**: `authenticateWithGoogle()`
- **Endpoint**: `POST /api/auth/google`
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

```java
@PostMapping("/google")
public ResponseEntity<LoginResponse> authenticateWithGoogle(@Valid @RequestBody GoogleAuthRequest request)
```

### 2. **Validação do Token JWT do Google** ✅

- **Arquivo**: `GoogleAuthService.java`
- **Método**: `verifyToken(String idTokenString)`
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

```java
GoogleIdToken.Payload payload = googleAuthService.verifyToken(request.getToken());
```

### 3. **Buscar/Criar Usuário no Banco** ✅

- **Lógica**: Busca por email, se não existe, cria novo
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

```java
Usuario usuario = usuarioRepository.findByEmail(email)
    .map(u -> {
        // Atualiza usuário existente
        if (foto != null && !foto.equals(u.getFoto())) {
            u.setFoto(foto);
        }
        if (u.getGoogleId() == null) {
            u.setGoogleId(googleId);
        }
        u.atualizarUltimoAcesso();
        return usuarioRepository.save(u);
    })
    .orElseGet(() -> {
        // Cria novo usuário
        Usuario novoUsuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .googleId(googleId)
                .foto(foto)
                .papel(Usuario.Papel.USER)
                .ativo(true)
                .build();
        return usuarioRepository.save(novoUsuario);
    });
```

### 4. **Gerar Token JWT do Sistema** ✅

- **Método**: `jwtService.generateToken(usuario)`
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

```java
String jwtToken = jwtService.generateToken(usuario);
String refreshToken = jwtService.generateRefreshToken(usuario);
```

### 5. **Retornar Token + Dados do Usuário** ✅

- **Response**: `LoginResponse` com usuário, token, refreshToken, expiresIn
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

```java
LoginResponse response = LoginResponse.builder()
    .usuario(UsuarioResponse.fromEntity(usuario))
    .token(jwtToken)
    .refreshToken(refreshToken)
    .expiresIn(securityProperties.getJwt().getExpirationTime())
    .build();

return ResponseEntity.ok(response);
```

---

## 🔧 Componentes Adicionais Verificados

### 6. **GoogleProperties** ✅

- **Arquivo**: `GoogleProperties.java`
- **Configuração**: `@ConfigurationProperties(prefix = "google")`
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

### 7. **GoogleAuthRequest DTO** ✅

- **Arquivo**: `GoogleAuthRequest.java`
- **Validação**: `@NotBlank` no campo token
- **Status**: ✅ **IMPLEMENTADO CORRETAMENTE**

### 8. **Segurança (endpoint público)** ✅

- **SecurityConfig**: `.requestMatchers("/api/auth/**").permitAll()`
- **Status**: ✅ **ENDPOINT PÚBLICO CONFIGURADO**

### 9. **Código no Repositório** ✅

- **Commit**: `ad65d7b feat: adiciona suporte à autenticação com Google OAuth`
- **Branch**: `main` e `origin/main`
- **Status**: ✅ **CÓDIGO COMMITADO E NO REPOSITÓRIO REMOTO**

### 10. **Compilação** ✅

- **Erros**: Nenhum erro de compilação
- **Status**: ✅ **CÓDIGO COMPILA SEM ERROS**

---

## ⚠️ Possíveis Causas do Erro 404

### 1. **Railway não fez redeploy após adicionar variáveis** 🔴

- **Problema**: Variáveis adicionadas, mas Railway não redeployou
- **Solução**: Forçar redeploy manual

```bash
railway up --detach
```

### 2. **Variáveis de ambiente faltando no Railway** 🟡

- **Verificar**: `GOOGLE_CLIENT_ID` e `GOOGLE_CLIENT_SECRET`
- **Comando**:

```bash
railway variables
```

### 3. **Railway ainda está buildando** 🟡

- **Problema**: Deploy em progresso
- **Solução**: Aguardar conclusão do build

```bash
railway logs
```

### 4. **Erro no build/startup** 🔴

- **Problema**: Aplicação não iniciou corretamente
- **Solução**: Verificar logs

```bash
railway logs --deployment <deployment-id>
```

---

## ✅ Checklist de Diagnóstico

Execute estes comandos para diagnosticar:

### 1. Verificar variáveis configuradas:

```bash
railway variables
```

**Deve mostrar:**

- ✅ `GOOGLE_CLIENT_ID`
- ✅ `GOOGLE_CLIENT_SECRET`
- ✅ `JWT_SECRET`
- ✅ `SPRING_PROFILES_ACTIVE=prod`
- ✅ `CORS_ALLOWED_ORIGINS`

### 2. Verificar status do deployment:

```bash
railway status
```

### 3. Verificar logs do Railway:

```bash
railway logs | grep -i "google\|started\|error"
```

**Procure por:**

- ✅ `Started FinancasPessoalApplication`
- ✅ `Tomcat started on port`
- ❌ Erros relacionados a Google
- ❌ `Could not resolve placeholder`

### 4. Testar o healthcheck:

```bash
curl https://financa-pessoal-production.up.railway.app/actuator/health
```

**Deve retornar:**

```json
{ "status": "UP" }
```

### 5. Testar se o endpoint existe:

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/auth/google \
  -H "Content-Type: application/json" \
  -d '{"token":"test"}'
```

**Resposta esperada:**

- ❌ 404 = Endpoint não existe (Railway não redeployou)
- ✅ 401/400 = Endpoint existe (token inválido, mas endpoint OK!)

---

## 🚀 Solução Recomendada

### Passo 1: Verificar variáveis

```bash
railway variables
```

### Passo 2: Forçar redeploy

```bash
railway up --detach
```

### Passo 3: Acompanhar logs

```bash
railway logs --follow
```

### Passo 4: Aguardar mensagem

```
Started FinancasPessoalApplication in X.XXX seconds
```

### Passo 5: Testar endpoint

```bash
curl -X POST https://financa-pessoal-production.up.railway.app/api/auth/google \
  -H "Content-Type: application/json" \
  -d '{"token":"test"}'
```

**Se retornar 400/401 = Sucesso! Endpoint está funcionando!**

---

## 📊 Status Atual

| Item                           | Status         |
| ------------------------------ | -------------- |
| Controller implementado        | ✅             |
| GoogleAuthService implementado | ✅             |
| Validação de token             | ✅             |
| Criar/buscar usuário           | ✅             |
| Gerar JWT                      | ✅             |
| Retornar response              | ✅             |
| Endpoint público               | ✅             |
| Código compilando              | ✅             |
| Código no Git                  | ✅             |
| Variáveis no Railway           | ❓ (verificar) |
| Deploy atualizado              | ❓ (verificar) |

---

## 🎯 Conclusão

**Tudo está implementado corretamente no código!** ✅

O problema mais provável é que:

1. Railway não fez redeploy automático após adicionar as variáveis
2. Ou o deploy está em andamento

**Execute os comandos acima para diagnosticar e forçar o redeploy.**
