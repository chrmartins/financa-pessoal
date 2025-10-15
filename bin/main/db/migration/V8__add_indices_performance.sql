-- ================================================================================
-- V8: Adiciona índices para performance de relatórios e queries históricas
-- ================================================================================
-- Data: 15/10/2025
-- Objetivo: Otimizar queries de relatórios, análises históricas e listagens
-- ================================================================================

-- Índice para queries por data (relatórios mensais, anuais, períodos)
CREATE INDEX IF NOT EXISTS idx_transacoes_data_transacao 
ON transacoes(data_transacao);

-- Índice composto para queries por data e tipo (relatórios de receitas vs despesas)
CREATE INDEX IF NOT EXISTS idx_transacoes_data_tipo 
ON transacoes(data_transacao, tipo);

-- Índice para buscar ocorrências de uma transação pai (usado pelo JOB e queries de séries)
CREATE INDEX IF NOT EXISTS idx_transacoes_pai_data 
ON transacoes(transacao_pai_id, data_transacao);

-- Índice para queries por usuário e data (listagens filtradas por período)
CREATE INDEX IF NOT EXISTS idx_transacoes_usuario_data 
ON transacoes(usuario_id, data_transacao);

-- Índice para queries por categoria e data (evolução de gastos por categoria)
CREATE INDEX IF NOT EXISTS idx_transacoes_categoria_data 
ON transacoes(categoria_id, data_transacao);

-- Índice para queries por usuário e tipo (totalizações por usuário)
CREATE INDEX IF NOT EXISTS idx_transacoes_usuario_tipo 
ON transacoes(usuario_id, tipo);

-- Índice composto para queries complexas (usuário + data + tipo)
-- Útil para relatórios filtrados: "Despesas do usuário X no período Y"
CREATE INDEX IF NOT EXISTS idx_transacoes_usuario_data_tipo 
ON transacoes(usuario_id, data_transacao, tipo);

-- Índice para transações recorrentes ativas (usado pelo JOB diário)
CREATE INDEX IF NOT EXISTS idx_transacoes_recorrencia_ativa 
ON transacoes(tipo_recorrencia, ativa) WHERE transacao_pai_id IS NULL;

-- ================================================================================
-- COMENTÁRIOS:
-- ================================================================================
-- 
-- idx_transacoes_data_transacao:
--   - Acelera: SELECT * FROM transacoes WHERE data_transacao BETWEEN x AND y
--   - Usado em: Listagens por período, relatórios mensais/anuais
--
-- idx_transacoes_data_tipo:
--   - Acelera: SELECT SUM(valor) WHERE data_transacao BETWEEN x AND y AND tipo = 'DESPESA'
--   - Usado em: Totalizações de receitas vs despesas por período
--
-- idx_transacoes_pai_data:
--   - Acelera: SELECT * WHERE transacao_pai_id = x ORDER BY data_transacao DESC
--   - Usado em: JOB de recorrências (encontrar última ocorrência)
--
-- idx_transacoes_usuario_data:
--   - Acelera: SELECT * WHERE usuario_id = x AND data_transacao BETWEEN y AND z
--   - Usado em: Listagens personalizadas por usuário e período
--
-- idx_transacoes_categoria_data:
--   - Acelera: SELECT SUM(valor) WHERE categoria_id = x GROUP BY mes
--   - Usado em: Gráficos de evolução de gastos por categoria
--
-- idx_transacoes_usuario_tipo:
--   - Acelera: SELECT COUNT(*) WHERE usuario_id = x AND tipo = 'DESPESA'
--   - Usado em: Estatísticas gerais por usuário
--
-- idx_transacoes_usuario_data_tipo:
--   - Acelera: Queries complexas combinando usuário, período e tipo
--   - Usado em: Relatórios avançados e dashboards
--
-- idx_transacoes_recorrencia_ativa:
--   - Acelera: SELECT * WHERE tipo_recorrencia = 'FIXA' AND ativa = true AND transacao_pai_id IS NULL
--   - Usado em: JOB diário de processamento de recorrências
--
-- ================================================================================
-- IMPACTO ESPERADO:
-- ================================================================================
-- 
-- ✅ Queries de listagem: 5-10x mais rápidas
-- ✅ Relatórios anuais: 10-20x mais rápidos
-- ✅ Gráficos de evolução: 5-15x mais rápidos
-- ✅ JOB de recorrências: 2-5x mais rápido
-- ✅ Totalizações: 10-50x mais rápidas
--
-- Overhead de espaço: ~5-10% do tamanho da tabela (aceitável)
-- Overhead de inserção: Mínimo (~5-10ms por INSERT, imperceptível)
--
-- ================================================================================
