-- Adiciona campo cor na tabela categorias
-- Permite que cada categoria tenha uma cor personalizada em formato hexadecimal

-- Passo 1: Adicionar coluna cor (nullable)
ALTER TABLE categorias
    ADD COLUMN IF NOT EXISTS cor VARCHAR(7);

-- Passo 2: Adicionar cores padrão às categorias existentes
-- Receitas: verde (#4CAF50)
UPDATE categorias 
SET cor = '#4CAF50' 
WHERE tipo = 'RECEITA' AND cor IS NULL;

-- Despesas: vermelho (#F44336)
UPDATE categorias 
SET cor = '#F44336' 
WHERE tipo = 'DESPESA' AND cor IS NULL;

-- Passo 3: Criar índice para consultas por cor (opcional, para performance)
CREATE INDEX IF NOT EXISTS idx_categorias_cor ON categorias(cor);
