# Variáveis de Ambiente Configuradas no Railway

## ✅ Configuração Atual (26 de setembro de 2025)

### Variáveis da Aplicação no Railway:

```env
CORS_ALLOWED_ORIGINS=http://localhost:5173
DATABASE_PASSWORD=${PGPASSWORD}
DATABASE_URL=postgresql://${PGUSER}:${PGPASSWORD}@${PGHOST}:${PGPORT}/${PGDATABASE}
DATABASE_USERNAME=${PGUSER}
JWT_SECRET=financa-pessoal-super-secret-jwt-key-2025-muito-seguro-para-producao
SPRING_PROFILES_ACTIVE=prod
```

## 🔧 Correção Necessária

**CORS_ALLOWED_ORIGINS** está com URL duplicada:

- ❌ Valor atual: `https://http://localhost:5173`
- ✅ Valor correto: `http://localhost:5173`

## 🚀 Status do Deploy

Com essas variáveis configuradas, a aplicação deve:

1. ✅ Conectar ao PostgreSQL usando as referências PG\*
2. ✅ Inicializar o Spring Security com JWT_SECRET
3. ✅ Ativar o profile de produção
4. ✅ Configurar CORS corretamente (após correção)

## 📝 Próximos Passos

1. Corrigir `CORS_ALLOWED_ORIGINS` no Railway (remover https://)
2. Fazer deploy e monitorar logs
3. Testar endpoint de health: `/actuator/health`

## 🔍 Logs a Observar

Procurar por estas mensagens nos logs do Railway:

- "Variáveis de ambiente detectadas..."
- "Datasource JDBC URL resolvido para deploy..."
- "Started FinancasPessoalApplication"
