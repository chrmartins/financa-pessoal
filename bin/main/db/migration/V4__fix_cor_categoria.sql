-- Migration V4: Garantir que a coluna cor existe e tem valores corretos
-- Esta migration é idempotente e pode ser executada múltiplas vezes

-- Adicionar coluna cor se não existir (seguro executar novamente)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'categorias' AND column_name = 'cor'
    ) THEN
        ALTER TABLE categorias ADD COLUMN cor VARCHAR(7);
    END IF;
END $$;

-- Atualizar cores padrão apenas para categorias que não têm cor
UPDATE categorias 
SET cor = '#4CAF50' 
WHERE tipo = 'RECEITA' AND (cor IS NULL OR cor = '');

UPDATE categorias 
SET cor = '#F44336' 
WHERE tipo = 'DESPESA' AND (cor IS NULL OR cor = '');

-- Criar índice se não existir
CREATE INDEX IF NOT EXISTS idx_categorias_cor ON categorias(cor);
