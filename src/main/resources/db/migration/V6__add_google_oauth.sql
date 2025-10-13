-- Migration para adicionar suporte ao login com Google

-- Adicionar coluna google_id (ID único do usuário no Google)
ALTER TABLE usuarios 
ADD COLUMN google_id VARCHAR(255) UNIQUE;

-- Adicionar coluna foto (URL da foto do perfil do Google)
ALTER TABLE usuarios 
ADD COLUMN foto VARCHAR(500);

-- Remover constraint NOT NULL da senha (para permitir login com Google sem senha)
ALTER TABLE usuarios 
ALTER COLUMN senha DROP NOT NULL;

-- Comentários para documentação
COMMENT ON COLUMN usuarios.google_id IS 'ID único do Google para autenticação OAuth 2.0';
COMMENT ON COLUMN usuarios.foto IS 'URL da foto do perfil do usuário (do Google ou upload manual)';
COMMENT ON COLUMN usuarios.senha IS 'Senha criptografada (BCrypt). NULL para usuários que fazem login apenas com Google';
