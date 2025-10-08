-- Migration Script: Adicionar relacionamento Usuario-Categoria
-- Data: 2025-10-07
-- Descrição: Isola categorias por usuário, vinculando cada categoria a um dono

BEGIN;

-- Passo 1: Adicionar coluna usuario_id (nullable temporariamente)
ALTER TABLE categorias
    ADD COLUMN usuario_id UUID;

-- Passo 2: Descobrir o último usuário que movimentou cada categoria
-- Estratégia: pegar o usuario_id da transação mais recente de cada categoria
WITH ult_movimentacao AS (
    SELECT DISTINCT ON (t.categoria_id)
           t.categoria_id,
           t.usuario_id
    FROM transacoes t
    ORDER BY t.categoria_id, t.data_transacao DESC, t.data_criacao DESC
)
UPDATE categorias c
SET usuario_id = u.usuario_id
FROM ult_movimentacao u
WHERE c.id = u.categoria_id;

-- Passo 3: Preencher categorias ainda nulas com o usuário admin padrão
UPDATE categorias c
SET usuario_id = admin.id
FROM usuarios admin
WHERE c.usuario_id IS NULL
  AND admin.email = 'admin@financeiro.com';

-- Passo 4: Verificar se ainda existem categorias sem usuário
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM categorias WHERE usuario_id IS NULL) THEN
        RAISE EXCEPTION 'ERRO: Ainda existem categorias sem usuario_id. Verifique manualmente.';
    END IF;
END $$;

-- Passo 5: Tornar a coluna obrigatória
ALTER TABLE categorias
    ALTER COLUMN usuario_id SET NOT NULL;

-- Passo 6: Criar chave estrangeira
ALTER TABLE categorias
    ADD CONSTRAINT fk_categoria_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

-- Passo 7: Garantir unicidade de nome por usuário (case insensitive)
-- Remove índice global antigo se existir
DROP INDEX IF EXISTS uq_categoria_nome;

-- Cria índice único por usuário + nome (case insensitive)
CREATE UNIQUE INDEX uq_categoria_usuario_nome
    ON categorias (usuario_id, lower(nome));

COMMIT;

-- Verificação pós-migração
SELECT 
    u.email,
    COUNT(c.id) as total_categorias
FROM categorias c
JOIN usuarios u ON u.id = c.usuario_id
GROUP BY u.email
ORDER BY total_categorias DESC;
