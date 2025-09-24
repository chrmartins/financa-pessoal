-- Script de inicialização do banco de dados PostgreSQL
-- Este script é executado automaticamente quando o container do PostgreSQL é criado

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Verificar se o banco foi criado corretamente
SELECT 'Database financeiro_db initialized successfully!' as message;