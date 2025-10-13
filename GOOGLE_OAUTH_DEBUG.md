# 🔍 Google OAuth - Diagnóstico Completo

## ❌ Erro Atual

**Erro 400**: "A solicitação é inválida e não pôde ser processada pelo servidor"

**URL do erro**: `accounts.google.com/signin/oauth/error?authError=Cg5pbnZhbGlkX3JlcXVlc3Q`

Decodificado: `invalid_request`

---

## ✅ Checklist de Configuração

### Backend (Railway)

- ✅ `GOOGLE_CLIENT_ID`: Configurado
- ✅ `GOOGLE_CLIENT_SECRET`: Configurado
- ✅ Endpoint `/api/auth/google`: Implementado
- ✅ Aplicação rodando

### Frontend (Vercel)

- ✅ `VITE_GOOGLE_CLIENT_ID`: Configurado
- ⚠️ Deploy feito mas erro persiste

### Google Console

- ✅ Origens JavaScript autorizadas:
  - `http://localhost:5173`
  - `https://ncontrole.com.br`
  - `https://www.ncontrole.com.br`
- ✅ URIs de redirecionamento:
  - `http://localhost:5173/auth/callback`
  - `https://ncontrole.com.br/auth/callback`
  - `https://www.ncontrole.com.br/auth/callback`

---

## 🔍 Possíveis Causas do Erro 400

### 1. **Redirect URI Incorreto**

O Google está reclamando que a `redirect_uri` enviada não está na lista autorizada.

**Verificar:**

- A URL que você está acessando: `ncontrole.com.br` ou `www.ncontrole.com.br`?
- O frontend está enviando qual `redirect_uri` para o Google?

**Exemplo de redirect_uri incorreto:**

```
❌ https://ncontrole.com.br/login  (deve ser /auth/callback)
❌ http://ncontrole.com.br/auth/callback  (deve ser https)
```

### 2. **Client ID Não Encontrado no Build**

O frontend pode ainda estar usando um build antigo sem a variável.

**Como verificar:**

1. Abra o DevTools (F12)
2. Console
3. Digite: `import.meta.env.VITE_GOOGLE_CLIENT_ID`
4. Se retornar `undefined`, a variável não foi injetada no build

### 3. **Cache do Navegador**

O navegador pode estar usando um build em cache.

**Solução:**

- Ctrl + Shift + R (hard refresh)
- Ou limpar cache do navegador

### 4. **Vercel Deploy Não Terminou**

O deploy pode ainda estar em progresso.

**Como verificar:**

- Acesse: https://vercel.com/christian-martins-projects-cb2cf79d/financa-pessoal-front
- Verifique se o status é "Ready" (verde)
- Veja o timestamp do último deploy

---

## 🔧 Diagnóstico Passo a Passo

### Passo 1: Verificar se a variável está no build

```javascript
// No console do navegador (F12)
console.log(import.meta.env.VITE_GOOGLE_CLIENT_ID);

// Deve retornar:
// "757529673881-26qjth52q07dscnd0jshd98npquh3s85.apps.googleusercontent.com"
```

### Passo 2: Verificar a requisição que o frontend está fazendo

```javascript
// No DevTools → Network → Filtrar por "google"
// Procurar pela requisição que vai para accounts.google.com
// Verificar os parâmetros:
{
  client_id: "757529673881-26qjth52q07dscnd0jshd98npquh3s85.apps.googleusercontent.com",
  redirect_uri: "https://ncontrole.com.br/auth/callback",  // Deve estar aqui!
  response_type: "code" ou "token",
  scope: "email profile"
}
```

### Passo 3: Verificar o código do frontend

O componente de login deve estar assim:

```typescript
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";

function App() {
  return (
    <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
      <YourApp />
    </GoogleOAuthProvider>
  );
}

function LoginPage() {
  const handleSuccess = async (credentialResponse) => {
    try {
      const response = await fetch(
        "https://financa-pessoal-production.up.railway.app/api/auth/google",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ token: credentialResponse.credential }),
        }
      );

      const data = await response.json();
      // Armazenar tokens...
    } catch (error) {
      console.error("Erro:", error);
    }
  };

  return (
    <GoogleLogin
      onSuccess={handleSuccess}
      onError={() => console.log("Login Failed")}
    />
  );
}
```

---

## 🎯 Ações Imediatas

### 1. **Hard Refresh**

```
Windows/Linux: Ctrl + Shift + R
Mac: Cmd + Shift + R
```

### 2. **Verificar Variável no Console**

```javascript
// No DevTools Console:
import.meta.env.VITE_GOOGLE_CLIENT_ID;
```

### 3. **Verificar Deploy da Vercel**

- Acesse: https://vercel.com/deployments
- Confirme que o último deploy está "Ready"
- Veja se foi há menos de 5 minutos

### 4. **Limpar Cache do Navegador**

```
Chrome: Settings → Privacy → Clear browsing data → Cached images
```

---

## 📋 Informações para Debug

Cole estas informações aqui após verificar:

**1. Variável no console:**

```
import.meta.env.VITE_GOOGLE_CLIENT_ID = ____________
```

**2. Status do deploy Vercel:**

```
Status: ___________
Timestamp: ________
```

**3. URL que você está acessando:**

```
https://____________/login
```

**4. Erro completo do Network (se houver):**

```
Request URL: ___________
Status: _______________
Response: _____________
```

---

## 🆘 Solução Alternativa

Se nada funcionar, podemos:

1. **Hardcodar temporariamente** o Client ID no frontend (apenas para teste)
2. **Verificar se há conflito** com outra biblioteca
3. **Usar o fluxo popup** ao invés do redirect

---

**Próximo passo: Execute os diagnósticos acima e me passe os resultados!** 🔍
