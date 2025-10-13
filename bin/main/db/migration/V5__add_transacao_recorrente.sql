-- Migration para adicionar campos de transações recorrentes (parceladas)

-- Adicionar coluna recorrente (indica se a transação faz parte de uma série de parcelas)
ALTER TABLE transacoes 
ADD COLUMN recorrente BOOLEAN DEFAULT FALSE;

-- Adicionar coluna quantidade_parcelas (total de parcelas na série)
ALTER TABLE transacoes 
ADD COLUMN quantidade_parcelas INTEGER;

-- Adicionar coluna parcela_atual (número da parcela: 1, 2, 3, etc.)
ALTER TABLE transacoes 
ADD COLUMN parcela_atual INTEGER;

-- Adicionar coluna transacao_pai_id (referência à primeira parcela da série)
ALTER TABLE transacoes 
ADD COLUMN transacao_pai_id UUID;

-- Comentários para documentação
COMMENT ON COLUMN transacoes.recorrente IS 'Indica se a transação é parte de uma série de parcelas recorrentes';
COMMENT ON COLUMN transacoes.quantidade_parcelas IS 'Quantidade total de parcelas na série (se recorrente = true)';
COMMENT ON COLUMN transacoes.parcela_atual IS 'Número da parcela atual (1, 2, 3... até quantidade_parcelas)';
COMMENT ON COLUMN transacoes.transacao_pai_id IS 'ID da primeira transação da série (parcela 1). NULL para a primeira parcela ou transações não-recorrentes';
