-- Migration V7: Melhorias no sistema de recorrências
-- Data: 2025-10-15
-- Descrição: Adiciona novos campos para suporte completo a recorrências (parceladas e fixas)
-- NOTA: Campos recorrente, quantidade_parcelas, parcela_atual, transacao_pai_id já existem (V5)

-- Adicionar APENAS os campos NOVOS que não existem

-- Tipo de recorrência (NAO_RECORRENTE, PARCELADA, FIXA)
-- Populate existing rows with default based on recorrente flag
ALTER TABLE transacoes ADD COLUMN IF NOT EXISTS tipo_recorrencia VARCHAR(20);
UPDATE transacoes SET tipo_recorrencia = 
    CASE 
        WHEN recorrente = true THEN 'PARCELADA'
        ELSE 'NAO_RECORRENTE'
    END
WHERE tipo_recorrencia IS NULL;
ALTER TABLE transacoes ALTER COLUMN tipo_recorrencia SET DEFAULT 'NAO_RECORRENTE';
ALTER TABLE transacoes ALTER COLUMN tipo_recorrencia SET NOT NULL;

-- Frequência (usado apenas para FIXA)
ALTER TABLE transacoes ADD COLUMN IF NOT EXISTS frequencia VARCHAR(20);

-- Status da transação (se está ativa para gerar recorrências)
ALTER TABLE transacoes ADD COLUMN IF NOT EXISTS ativa BOOLEAN DEFAULT TRUE;
UPDATE transacoes SET ativa = TRUE WHERE ativa IS NULL;

-- Criar índices para performance (apenas se não existirem)
CREATE INDEX IF NOT EXISTS idx_tipo_recorrencia ON transacoes(tipo_recorrencia);
CREATE INDEX IF NOT EXISTS idx_frequencia ON transacoes(frequencia);
CREATE INDEX IF NOT EXISTS idx_transacao_pai_id ON transacoes(transacao_pai_id);
CREATE INDEX IF NOT EXISTS idx_ativa ON transacoes(ativa);

-- Adicionar foreign key para transacao_pai_id (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_transacao_pai'
    ) THEN
        ALTER TABLE transacoes 
        ADD CONSTRAINT fk_transacao_pai 
        FOREIGN KEY (transacao_pai_id) 
        REFERENCES transacoes(id) 
        ON DELETE SET NULL;
    END IF;
END $$;

-- Adicionar comentários para documentação
COMMENT ON COLUMN transacoes.tipo_recorrencia IS 'Tipo de recorrência: NAO_RECORRENTE, PARCELADA, FIXA';
COMMENT ON COLUMN transacoes.frequencia IS 'Frequência para transações fixas: DIARIO, SEMANAL, QUINZENAL, MENSAL, SEMESTRAL, ANUAL';
COMMENT ON COLUMN transacoes.parcela_atual IS 'Número da parcela atual (usado em PARCELADA)';
COMMENT ON COLUMN transacoes.transacao_pai_id IS 'UUID da transação modelo que gera as recorrências';
COMMENT ON COLUMN transacoes.ativa IS 'Se a transação/recorrência está ativa';
