-- ===================================================================
-- SCRIPT COMPLETO DE SETUP DO BANCO DE DADOS
-- Execute este script para configurar todos os dados de teste
-- ===================================================================

-- 1. LIMPAR DADOS EXISTENTES (CUIDADO EM PRODUÇÃO!)
DELETE FROM transacoes;
DELETE FROM categorias;
DELETE FROM usuarios WHERE id IN (
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440002', 
    '550e8400-e29b-41d4-a716-446655440003'
);

-- 2. INSERIR USUÁRIOS DE TESTE
INSERT INTO usuarios (id, nome, email, papel, ativo, data_criacao, data_atualizacao) VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'João Silva', 'joao.silva@email.com', 'USER', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'Maria Santos', 'maria.santos@email.com', 'USER', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Admin Sistema', 'admin@financeiro.com', 'ADMIN', true, NOW(), NOW());

-- 3. INSERIR CATEGORIAS PADRÃO
INSERT INTO categorias (id, nome, descricao, tipo, ativa, data_criacao, data_atualizacao) VALUES 
-- Receitas
('650e8400-e29b-41d4-a716-446655440001', 'Salário', 'Renda do trabalho principal', 'RECEITA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440002', 'Freelance', 'Renda de trabalhos freelance', 'RECEITA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440003', 'Investimentos', 'Rendimentos de investimentos', 'RECEITA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440004', 'Vendas', 'Vendas de produtos ou serviços', 'RECEITA', true, NOW(), NOW()),

-- Despesas
('650e8400-e29b-41d4-a716-446655440011', 'Alimentação', 'Gastos com comida e bebidas', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440012', 'Transporte', 'Gastos com transporte e combustível', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440013', 'Moradia', 'Aluguel, financiamento, condomínio', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440014', 'Saúde', 'Gastos com medicamentos e consultas', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440015', 'Entretenimento', 'Gastos com lazer e diversão', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440016', 'Educação', 'Gastos com cursos e livros', 'DESPESA', true, NOW(), NOW()),
('650e8400-e29b-41d4-a716-446655440017', 'Utilidades', 'Contas de luz, água, internet', 'DESPESA', true, NOW(), NOW());

-- 4. POPULAR TRANSAÇÕES PARA TODOS OS USUÁRIOS
DO $$
DECLARE 
    -- Array de usuários
    usuarios UUID[] := ARRAY[
        '550e8400-e29b-41d4-a716-446655440001'::UUID,  -- João Silva
        '550e8400-e29b-41d4-a716-446655440002'::UUID,  -- Maria Santos
        '550e8400-e29b-41d4-a716-446655440003'::UUID   -- Admin Sistema
    ];
    
    usuario_atual UUID;
    base_date DATE := CURRENT_DATE;
BEGIN
    -- Loop através de cada usuário
    FOREACH usuario_atual IN ARRAY usuarios
    LOOP
        RAISE NOTICE 'Inserindo transações para usuário: %', usuario_atual;
        
        -- RECEITAS (3 transações)
        
        -- Transação 1: Salário
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440001',
            'Salário Setembro',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 5500.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 4800.00
                ELSE 8000.00
            END,
            'RECEITA',
            base_date - INTERVAL '1 day',
            NOW(),
            NOW()
        );
        
        -- Transação 2: Freelance
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440002',
            'Projeto Web',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 1200.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 900.00
                ELSE 2200.00
            END,
            'RECEITA',
            base_date - INTERVAL '10 days',
            NOW(),
            NOW()
        );
        
        -- Transação 3: Venda/Investimento
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440003' THEN '650e8400-e29b-41d4-a716-446655440003'  -- Investimentos
                ELSE '650e8400-e29b-41d4-a716-446655440004'  -- Vendas
            END,
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440003' THEN 'Rendimento Investimentos'
                ELSE 'Venda Notebook'
            END,
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 800.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 600.00
                ELSE 1800.00
            END,
            'RECEITA',
            base_date - INTERVAL '15 days',
            NOW(),
            NOW()
        );
        
        -- DESPESAS (7 transações)
        
        -- Despesa 1: Aluguel/Moradia
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440013',
            'Aluguel Setembro',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 1800.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 1200.00
                ELSE 2500.00
            END,
            'DESPESA',
            base_date - INTERVAL '5 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 2: Supermercado
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440011',
            'Supermercado Setembro',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 650.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 520.00
                ELSE 720.00
            END,
            'DESPESA',
            base_date - INTERVAL '2 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 3: Transporte
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440012',
            'Uber e Transporte',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 280.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 250.00
                ELSE 320.00
            END,
            'DESPESA',
            base_date - INTERVAL '7 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 4: Saúde
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440014',
            'Farmácia',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 85.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 95.00
                ELSE 120.00
            END,
            'DESPESA',
            base_date - INTERVAL '12 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 5: Utilidades
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440017',
            'Internet + TV',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 120.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 100.00
                ELSE 180.00
            END,
            'DESPESA',
            base_date - INTERVAL '14 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 6: Entretenimento
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440015',
            'Cinema',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 45.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 40.00
                ELSE 80.00
            END,
            'DESPESA',
            base_date - INTERVAL '20 days',
            NOW(),
            NOW()
        );
        
        -- Despesa 7: Educação
        INSERT INTO transacoes (
            id, usuario_id, categoria_id, descricao, valor, tipo, 
            data_transacao, data_criacao, data_atualizacao
        ) VALUES (
            gen_random_uuid(),
            usuario_atual,
            '650e8400-e29b-41d4-a716-446655440016',
            'Livros Técnicos',
            CASE 
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440001' THEN 150.00
                WHEN usuario_atual = '550e8400-e29b-41d4-a716-446655440002' THEN 200.00
                ELSE 300.00
            END,
            'DESPESA',
            base_date - INTERVAL '28 days',
            NOW(),
            NOW()
        );
        
    END LOOP;
    
    RAISE NOTICE 'Setup completo! Cada usuário tem 10 transações (3 receitas + 7 despesas).';
    
END $$;

-- 5. VERIFICAR DADOS INSERIDOS
SELECT 
    '=== RESUMO POR USUÁRIO ===' as info,
    '' as usuario,
    '' as total_transacoes,
    '' as total_receitas,
    '' as total_despesas,
    '' as saldo_final;

SELECT 
    u.nome as usuario,
    u.email as email,
    COUNT(t.id)::TEXT as total_transacoes,
    COALESCE(SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END), 0)::TEXT as total_receitas,
    COALESCE(SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END), 0)::TEXT as total_despesas,
    COALESCE((SUM(CASE WHEN t.tipo = 'RECEITA' THEN t.valor ELSE 0 END) - 
     SUM(CASE WHEN t.tipo = 'DESPESA' THEN t.valor ELSE 0 END)), 0)::TEXT as saldo_final
FROM usuarios u
LEFT JOIN transacoes t ON u.id = t.usuario_id
WHERE u.id IN (
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440002', 
    '550e8400-e29b-41d4-a716-446655440003'
)
GROUP BY u.id, u.nome, u.email
ORDER BY u.nome;

-- 6. VERIFICAR ALGUMAS TRANSAÇÕES DE EXEMPLO
SELECT 
    '=== TRANSAÇÕES DE EXEMPLO ===' as info,
    '' as usuario,
    '' as descricao,
    '' as categoria,
    '' as valor,
    '' as tipo;

SELECT 
    u.nome as usuario,
    t.descricao,
    c.nome as categoria,
    CONCAT('R$ ', t.valor::TEXT) as valor,
    t.tipo
FROM transacoes t
JOIN usuarios u ON t.usuario_id = u.id
JOIN categorias c ON t.categoria_id = c.id
WHERE u.id IN (
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440002', 
    '550e8400-e29b-41d4-a716-446655440003'
)
ORDER BY u.nome, t.tipo DESC, t.valor DESC
LIMIT 20;

-- SCRIPT FINALIZADO COM SUCESSO!