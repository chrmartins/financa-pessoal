# ✅ Checklist de Integração Frontend

## 📋 Checklist Completo para Configuração dos Ambientes

Use este checklist para garantir que todos os passos foram executados corretamente.

---

## 🎯 Fase 1: Preparação (5-10 minutos)

### Pré-requisitos

- [ ] Node.js instalado (v16+)
- [ ] npm ou yarn configurado
- [ ] Git configurado
- [ ] Projeto frontend clonado/criado
- [ ] Editor de código aberto (VSCode recomendado)

### Documentação

- [ ] Leu o [FRONTEND_QUICKSTART.md](./FRONTEND_QUICKSTART.md)
- [ ] Leu o [ENVIRONMENTS.md](./ENVIRONMENTS.md)
- [ ] Tem acesso ao [FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md)

---

## 🔧 Fase 2: Configuração do Ambiente DEV (10-15 minutos)

### Variáveis de Ambiente

- [ ] Criou arquivo `.env.development` na raiz do projeto
- [ ] Configurou `REACT_APP_API_URL=http://localhost:8080` (ou VITE*/NG* conforme framework)
- [ ] Configurou `REACT_APP_API_USER=admin@financeiro.com`
- [ ] Configurou `REACT_APP_API_PASSWORD=password`
- [ ] Adicionou `.env.local` ao `.gitignore`

### Cliente HTTP

- [ ] Instalou Axios (`npm install axios`) OU vai usar Fetch nativo
- [ ] Criou arquivo `src/config/api.js` ou `src/services/api.js`
- [ ] Configurou baseURL para ler de `process.env`
- [ ] Implementou interceptor de autenticação Basic Auth
- [ ] Implementou interceptor de tratamento de erros

### Serviços

- [ ] Criou pasta `src/services/`
- [ ] Criou `src/services/categoriaService.js` com métodos CRUD
- [ ] Criou `src/services/transacaoService.js` com métodos CRUD
- [ ] Criou `src/services/usuarioService.js` (opcional)

### Backend Local Rodando

- [ ] PostgreSQL local iniciado (`docker-compose up postgres`)
- [ ] Backend local rodando (`./gradlew bootRun --args='--spring.profiles.active=dev'`)
- [ ] Testou healthcheck: `curl http://localhost:8080/actuator/health`
- [ ] Testou autenticação: `curl -u "admin@financeiro.com:password" http://localhost:8080/api/categorias`

### Frontend DEV

- [ ] Instalou dependências (`npm install`)
- [ ] Iniciou servidor de desenvolvimento (`npm start` ou `npm run dev`)
- [ ] Frontend abriu em `http://localhost:3000` (ou porta configurada)
- [ ] Abriu DevTools (F12) → Console
- [ ] Verificou que `process.env.REACT_APP_API_URL` = `http://localhost:8080`

---

## 🧪 Fase 3: Testes no Ambiente DEV (15-20 minutos)

### Teste de Conexão

- [ ] Testou requisição simples no console do navegador:
  ```javascript
  fetch("http://localhost:8080/api/categorias", {
    headers: {
      Authorization: "Basic " + btoa("admin@financeiro.com:password"),
    },
  })
    .then((r) => r.json())
    .then(console.log);
  ```
- [ ] Recebeu array com 11 categorias
- [ ] Verificou na aba Network que não há erros CORS

### Teste de Autenticação

- [ ] Implementou tela/componente de login (ou usou credenciais fixas)
- [ ] Fez login com `admin@financeiro.com` / `password`
- [ ] Recebeu resposta com dados do usuário
- [ ] Headers de autenticação estão sendo enviados em todas as requisições

### Teste de Listagem

- [ ] Listou categorias no frontend
- [ ] Listou transações no frontend
- [ ] Filtrou transações por tipo (RECEITA/DESPESA)
- [ ] Filtrou transações por período

### Teste de CRUD

- [ ] Criou nova categoria
- [ ] Editou categoria existente
- [ ] Ativou/Desativou categoria
- [ ] Deletou categoria (opcional, se implementado)
- [ ] Criou nova transação
- [ ] Editou transação existente
- [ ] Deletou transação

### Teste de Relatório

- [ ] Buscou resumo mensal
- [ ] Exibiu totalReceitas, totalDespesas, saldo
- [ ] Validou cálculos

---

## 🌐 Fase 4: Configuração do Ambiente PROD (5-10 minutos)

### Variáveis de Ambiente

- [ ] Criou arquivo `.env.production` na raiz do projeto
- [ ] Configurou `REACT_APP_API_URL=https://financa-pessoal-production.up.railway.app`
- [ ] Configurou `REACT_APP_API_USER=admin@financeiro.com`
- [ ] Configurou `REACT_APP_API_PASSWORD=password`

### Build de Produção

- [ ] Executou build: `npm run build`
- [ ] Build completou sem erros
- [ ] Verificou pasta `build/` ou `dist/` foi criada

### Frontend PROD (local apontando para Railway)

- [ ] Iniciou frontend em modo produção: `npm run start:prod` (ou servir build)
- [ ] Abriu DevTools → Console
- [ ] Verificou que `process.env.REACT_APP_API_URL` = URL do Railway

---

## 🧪 Fase 5: Testes no Ambiente PROD (15-20 minutos)

### Teste de Conexão PROD

- [ ] Testou requisição no console do navegador apontando para Railway:
  ```javascript
  fetch("https://financa-pessoal-production.up.railway.app/api/categorias", {
    headers: {
      Authorization: "Basic " + btoa("admin@financeiro.com:password"),
    },
  })
    .then((r) => r.json())
    .then(console.log);
  ```
- [ ] Recebeu array com categorias
- [ ] Verificou na aba Network que requisições vão para Railway
- [ ] Verificou que não há erros CORS

### Teste de Autenticação PROD

- [ ] Fez login em PROD
- [ ] Recebeu resposta com dados do usuário
- [ ] Headers de autenticação funcionando

### Teste de CRUD em PROD

- [ ] Criou transação em PROD
- [ ] Listou transações (deve aparecer a recém-criada)
- [ ] Editou transação
- [ ] Deletou transação de teste

### Validação de Dados

- [ ] Dados em PROD são os mesmos do DEV (migrados anteriormente)
- [ ] 3 usuários existem
- [ ] 11 categorias existem
- [ ] Transações de exemplo existem

---

## 🎨 Fase 6: Melhorias Opcionais (Tempo variável)

### UX/UI

- [ ] Adicionou componente de badge de ambiente (DEV/PROD)
- [ ] Implementou loading states
- [ ] Implementou mensagens de erro amigáveis
- [ ] Adicionou confirmações para ações destrutivas (deletar)
- [ ] Implementou feedback visual para ações bem-sucedidas

### Validações

- [ ] Validou formulários no frontend (antes de enviar para API)
- [ ] Adicionou máscaras de entrada (data, moeda)
- [ ] Implementou validações de tipos (RECEITA/DESPESA)

### Performance

- [ ] Implementou cache de requisições (React Query/SWR)
- [ ] Adicionou debounce em buscas
- [ ] Otimizou re-renders

### Segurança

- [ ] Moveu credenciais para variáveis de ambiente
- [ ] NÃO commitou arquivos `.env` (verificou .gitignore)
- [ ] Planejou migração para JWT (futuro)

---

## 📦 Fase 7: Deploy do Frontend (Futuro)

### Preparação

- [ ] Escolheu plataforma (Vercel/Netlify/Railway/etc)
- [ ] Criou conta na plataforma
- [ ] Conectou repositório Git

### Configuração

- [ ] Configurou variáveis de ambiente na plataforma
- [ ] Definiu comando de build
- [ ] Definiu pasta de output

### Deploy

- [ ] Executou deploy
- [ ] Verificou que build completou
- [ ] Acessou URL do frontend hospedado
- [ ] Testou funcionalidades

### DNS (Opcional)

- [ ] Configurou domínio customizado
- [ ] Configurou SSL/HTTPS
- [ ] Atualizou CORS no backend se necessário

---

## 🐛 Troubleshooting

### ❌ CORS Error

- [ ] Reiniciou backend local
- [ ] Verificou que URL do frontend está nas origens permitidas
- [ ] Testou com cURL primeiro

### ❌ 401 Unauthorized

- [ ] Verificou credenciais no `.env`
- [ ] Confirmou que header Authorization está sendo enviado
- [ ] Testou autenticação com cURL
- [ ] Verificou que PasswordEncoder está correto (BCrypt)

### ❌ Variáveis de ambiente não carregam

- [ ] Reiniciou servidor de desenvolvimento
- [ ] Verificou prefixo correto (REACT*APP*, VITE\_, etc)
- [ ] Verificou sintaxe do arquivo `.env`

### ❌ Build falha

- [ ] Verificou erros no console
- [ ] Atualizou dependências
- [ ] Limpou cache (`npm cache clean --force`)
- [ ] Reinstalou dependências (`rm -rf node_modules && npm install`)

---

## 📊 Status Final

### ✅ Completo quando:

- [ ] **Ambiente DEV funcional**: Frontend local + Backend local + PostgreSQL local
- [ ] **Ambiente PROD funcional**: Frontend local/hospedado + Backend Railway + PostgreSQL Railway
- [ ] **CRUD completo implementado**: Criar, Ler, Atualizar, Deletar
- [ ] **Filtros funcionando**: Tipo, período, categoria
- [ ] **Relatórios funcionando**: Resumo mensal
- [ ] **Sem erros CORS**: Requisições passam sem bloqueio
- [ ] **Sem erros 401**: Autenticação funcionando
- [ ] **Documentação lida**: Todos os guias consultados

---

## 📚 Referências Rápidas

| Documento                                                        | Quando Usar                 |
| ---------------------------------------------------------------- | --------------------------- |
| [FRONTEND_QUICKSTART.md](./FRONTEND_QUICKSTART.md)               | Início rápido (5 min)       |
| [FRONTEND_ENVIRONMENT_SETUP.md](./FRONTEND_ENVIRONMENT_SETUP.md) | Setup detalhado com códigos |
| [FRONTEND_INTEGRATION_GUIDE.md](./FRONTEND_INTEGRATION_GUIDE.md) | Referência completa da API  |
| [ENVIRONMENTS.md](./ENVIRONMENTS.md)                             | Visão geral dos ambientes   |
| [ARCHITECTURE_DIAGRAM.md](./ARCHITECTURE_DIAGRAM.md)             | Diagramas e fluxos          |

---

## 🆘 Ajuda

Se ficou travado em algum passo:

1. **Verifique os logs**

   - Backend DEV: Terminal onde rodou `./gradlew bootRun`
   - Backend PROD: Railway Dashboard → Logs
   - Frontend: DevTools (F12) → Console + Network

2. **Teste isoladamente**

   - Backend funcionando? → `curl http://localhost:8080/actuator/health`
   - Autenticação OK? → `curl -u "admin:password" http://localhost:8080/api/categorias`
   - Frontend carregando env? → `console.log(process.env.REACT_APP_API_URL)`

3. **Leia a documentação**

   - Cada guia tem seção de troubleshooting
   - Exemplos de código prontos para copiar

4. **Valide passo a passo**
   - Não pule etapas do checklist
   - Confirme cada item antes de avançar

---

## 🎉 Conclusão

Quando todos os itens estiverem marcados, você terá:

✅ **2 ambientes completos** (DEV + PROD)  
✅ **Frontend integrado** com backend  
✅ **CRUD funcional** para todas as entidades  
✅ **Autenticação** configurada  
✅ **Pronto para desenvolvimento** e implantação

---

**Última Atualização:** 04 de Outubro de 2025  
**Versão:** 1.0.0
