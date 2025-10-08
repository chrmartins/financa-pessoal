-- Adiciona relacionamento Usuario-Categoria
-- Isola categorias por usuário, vinculando cada categoria ao seu dono

-- Passo 1: Adicionar coluna usuario_id (nullable temporariamente)
ALTER TABLE categorias
    ADD COLUMN IF NOT EXISTS usuario_id UUID;

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
WHERE c.id = u.categoria_id
  AND c.usuario_id IS NULL;

-- Passo 3: Preencher categorias ainda nulas com o primeiro usuário admin encontrado
UPDATE categorias c
SET usuario_id = (
    SELECT id FROM usuarios 
    WHERE email = 'admin@financeiro.com' 
       OR papel = 'ADMIN'
    LIMIT 1
)
WHERE c.usuario_id IS NULL;

-- Passo 4: Se ainda não houver usuário, usar o primeiro usuário disponível
UPDATE categorias c
SET usuario_id = (SELECT id FROM usuarios LIMIT 1)
WHERE c.usuario_id IS NULL;

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

-- Passo 8: Criar índice para performance
CREATE INDEX idx_categorias_usuario ON categorias(usuario_id);
