-- Criando usuários
INSERT INTO usuarios (id, nome, email, senha, papel, ativo, data_criacao) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'João Silva', 'joao.silva@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'Maria Santos', 'maria.santos@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Admin Sistema', 'admin@financeiro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, NOW());

-- Criando categorias de receita
INSERT INTO categorias (id, nome, descricao, tipo, ativa, data_criacao) VALUES
('650e8400-e29b-41d4-a716-446655440001', 'Salário', 'Receita de salário mensal', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440002', 'Freelance', 'Trabalhos extras e freelances', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440003', 'Investimentos', 'Rendimentos de investimentos', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440004', 'Vendas', 'Vendas de produtos ou serviços', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440005', 'Aluguel Recebido', 'Receita de aluguel de imóveis', 'RECEITA', true, NOW());

-- Criando categorias de despesa
INSERT INTO categorias (id, nome, descricao, tipo, ativa, data_criacao) VALUES
('650e8400-e29b-41d4-a716-446655440011', 'Alimentação', 'Gastos com alimentação e supermercado', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440012', 'Transporte', 'Combustível, uber, transporte público', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440013', 'Moradia', 'Aluguel, condomínio, IPTU', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440014', 'Saúde', 'Plano de saúde, medicamentos, consultas', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440015', 'Lazer', 'Cinema, restaurantes, viagens', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440016', 'Educação', 'Cursos, livros, capacitação', 'DESPESA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440017', 'Utilities', 'Energia, água, internet, telefone', 'DESPESA', true, NOW());

-- Transações de Agosto 2025 (10 transações)
INSERT INTO transacoes (id, descricao, valor, tipo, data_transacao, categoria_id, usuario_id, data_criacao) VALUES
-- Receitas de agosto
('750e8400-e29b-41d4-a716-446655440001', 'Salário Agosto', 5500.00, 'RECEITA', '2025-08-01', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440002', 'Freelance Design', 1200.00, 'RECEITA', '2025-08-15', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440003', 'Rendimento Investimentos', 350.00, 'RECEITA', '2025-08-20', '650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', NOW()),

-- Despesas de agosto
('750e8400-e29b-41d4-a716-446655440011', 'Supermercado Agosto', 650.00, 'DESPESA', '2025-08-03', '650e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440012', 'Aluguel Agosto', 1800.00, 'DESPESA', '2025-08-05', '650e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440013', 'Combustível', 280.00, 'DESPESA', '2025-08-08', '650e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440014', 'Plano de Saúde', 420.00, 'DESPESA', '2025-08-10', '650e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440015', 'Conta de Luz', 180.00, 'DESPESA', '2025-08-12', '650e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440016', 'Jantar Restaurante', 120.00, 'DESPESA', '2025-08-18', '650e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440017', 'Curso Online', 150.00, 'DESPESA', '2025-08-25', '650e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440001', NOW());

-- Transações de Setembro 2025 (10 transações)
INSERT INTO transacoes (id, descricao, valor, tipo, data_transacao, categoria_id, usuario_id, data_criacao) VALUES
-- Receitas de setembro
('750e8400-e29b-41d4-a716-446655440021', 'Salário Setembro', 5500.00, 'RECEITA', '2025-09-01', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440022', 'Projeto Web', 2200.00, 'RECEITA', '2025-09-10', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440023', 'Venda Notebook', 1800.00, 'RECEITA', '2025-09-15', '650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', NOW()),

-- Despesas de setembro  
('750e8400-e29b-41d4-a716-446655440031', 'Supermercado Setembro', 720.00, 'DESPESA', '2025-09-02', '650e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440032', 'Aluguel Setembro', 1800.00, 'DESPESA', '2025-09-05', '650e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440033', 'Uber e Transporte', 320.00, 'DESPESA', '2025-09-07', '650e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440034', 'Farmácia', 85.00, 'DESPESA', '2025-09-12', '650e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440035', 'Internet + TV', 120.00, 'DESPESA', '2025-09-14', '650e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440036', 'Cinema', 45.00, 'DESPESA', '2025-09-20', '650e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440001', NOW()),
('750e8400-e29b-41d4-a716-446655440037', 'Livros Técnicos', 200.00, 'DESPESA', '2025-09-28', '650e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440001', NOW());