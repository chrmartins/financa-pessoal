-- ===============================================
-- DADOS DE EXEMPLO - Sistema Financeiro Pessoal
-- ===============================================

-- Inserir usuário de exemplo
INSERT INTO usuarios (id, nome, email, senha, papel, ativo, data_criacao) VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'João Silva', 'joao.silva@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'Maria Santos', 'maria.santos@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Admin Sistema', 'admin@financeiro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, NOW());

-- Inserir categorias de RECEITA
INSERT INTO categorias (id, nome, descricao, tipo, ativa, data_criacao) VALUES 
('650e8400-e29b-41d4-a716-446655440001', 'Salário', 'Remuneração mensal do trabalho', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440002', 'Freelances', 'Trabalhos extras e projetos independentes', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440003', 'Investimentos', 'Rendimentos de aplicações e dividendos', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440004', 'Vendas', 'Venda de produtos ou serviços', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440005', 'Aluguéis', 'Receita de imóveis alugados', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440006', 'Prêmios', 'Bonificações e premiações', 'RECEITA', true, NOW()),
('650e8400-e29b-41d4-a716-446655440007', 'Restituição', 'Devolução de impostos e taxas', 'RECEITA', true, NOW());

-- Inserir categorias de DESPESA
INSERT INTO categorias (id, nome, descricao, tipo, ativa, data_criacao) VALUES 
('750e8400-e29b-41d4-a716-446655440001', 'Alimentação', 'Supermercado, restaurantes e delivery', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440002', 'Transporte', 'Combustível, transporte público e manutenção', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440003', 'Moradia', 'Aluguel, financiamento e condomínio', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440004', 'Saúde', 'Plano de saúde, médicos e medicamentos', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440005', 'Educação', 'Cursos, livros e material didático', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440006', 'Lazer', 'Cinema, viagens e entretenimento', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440007', 'Roupas', 'Vestuário e calçados', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440008', 'Tecnologia', 'Eletrônicos, software e assinaturas', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440009', 'Impostos', 'IPTU, IPVA e outros tributos', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440010', 'Cartão', 'Anuidade e taxas bancárias', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440011', 'Pets', 'Veterinário, ração e cuidados', 'DESPESA', true, NOW()),
('750e8400-e29b-41d4-a716-446655440012', 'Limpeza', 'Produtos de limpeza e higiene', 'DESPESA', true, NOW());

-- ===============================================
-- TRANSAÇÕES DE RECEITA - João Silva (Últimos 3 meses)
-- ===============================================

INSERT INTO transacoes (id, descricao, valor, data_transacao, tipo, categoria_id, usuario_id, observacoes, data_criacao) VALUES 
-- Setembro 2024
('850e8400-e29b-41d4-a716-446655440001', 'Salário Setembro', 5500.00, '2024-09-05', 'RECEITA', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Salário mensal empresa ABC', NOW()),
('850e8400-e29b-41d4-a716-446655440002', 'Freelance Website', 1200.00, '2024-09-15', 'RECEITA', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Desenvolvimento site corporativo', NOW()),
('850e8400-e29b-41d4-a716-446655440003', 'Dividendos ITUB4', 85.50, '2024-09-10', 'RECEITA', '650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'Dividendos ações Itaú', NOW()),
('850e8400-e29b-41d4-a716-446655440004', 'Venda Notebook Antigo', 800.00, '2024-09-20', 'RECEITA', '650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Venda notebook Dell usado', NOW()),

-- Agosto 2024
('850e8400-e29b-41d4-a716-446655440005', 'Salário Agosto', 5500.00, '2024-08-05', 'RECEITA', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Salário mensal empresa ABC', NOW()),
('850e8400-e29b-41d4-a716-446655440006', 'Freelance App Mobile', 2500.00, '2024-08-25', 'RECEITA', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Desenvolvimento aplicativo iOS', NOW()),
('850e8400-e29b-41d4-a716-446655440007', 'Aluguel Kitnet', 600.00, '2024-08-10', 'RECEITA', '650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'Aluguel do imóvel na Vila Madalena', NOW()),
('850e8400-e29b-41d4-a716-446655440008', 'Prêmio Produtividade', 800.00, '2024-08-30', 'RECEITA', '650e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'Bonificação por metas atingidas', NOW()),

-- Julho 2024
('850e8400-e29b-41d4-a716-446655440009', 'Salário Julho', 5500.00, '2024-07-05', 'RECEITA', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Salário mensal empresa ABC', NOW()),
('850e8400-e29b-41d4-a716-446655440010', 'Restituição IR', 1250.00, '2024-07-15', 'RECEITA', '650e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'Restituição Imposto de Renda 2024', NOW()),
('850e8400-e29b-41d4-a716-446655440011', 'Consultoria TI', 1500.00, '2024-07-20', 'RECEITA', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Consultoria sistema ERP', NOW());

-- ===============================================
-- TRANSAÇÕES DE DESPESA - João Silva (Últimos 3 meses)
-- ===============================================

-- Setembro 2024 - Despesas
INSERT INTO transacoes (id, descricao, valor, data_transacao, tipo, categoria_id, usuario_id, observacoes, data_criacao) VALUES 
('950e8400-e29b-41d4-a716-446655440001', 'Supermercado Extra', 350.80, '2024-09-02', 'DESPESA', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Compras mensais', NOW()),
('950e8400-e29b-41d4-a716-446655440002', 'Combustível', 280.00, '2024-09-03', 'DESPESA', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Abastecimento posto Shell', NOW()),
('950e8400-e29b-41d4-a716-446655440003', 'Aluguel', 1200.00, '2024-09-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'Aluguel apartamento', NOW()),
('950e8400-e29b-41d4-a716-446655440004', 'Plano Saúde', 320.00, '2024-09-08', 'DESPESA', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Amil - plano família', NOW()),
('950e8400-e29b-41d4-a716-446655440005', 'Curso Udemy', 89.90, '2024-09-10', 'DESPESA', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'Curso Spring Boot Avançado', NOW()),
('950e8400-e29b-41d4-a716-446655440006', 'Cinema Imax', 65.00, '2024-09-12', 'DESPESA', '750e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'Ingresso filme Duna 2', NOW()),
('950e8400-e29b-41d4-a716-446655440007', 'Camisa Social', 120.00, '2024-09-15', 'DESPESA', '750e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'Camisa marca Colombo', NOW()),
('950e8400-e29b-41d4-a716-446655440008', 'Netflix', 32.90, '2024-09-18', 'DESPESA', '750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 'Assinatura mensal Netflix', NOW()),
('950e8400-e29b-41d4-a716-446655440009', 'IPTU', 158.50, '2024-09-20', 'DESPESA', '750e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440001', 'IPTU 3ª parcela 2024', NOW()),
('950e8400-e29b-41d4-a716-446655440010', 'Delivery iFood', 45.80, '2024-09-22', 'DESPESA', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Jantar chinês', NOW()),
('950e8400-e29b-41d4-a716-446655440011', 'Veterinário', 180.00, '2024-09-25', 'DESPESA', '750e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', 'Consulta e vacina gato', NOW()),

-- Agosto 2024 - Despesas
('950e8400-e29b-41d4-a716-446655440012', 'Supermercado Carrefour', 410.30, '2024-08-01', 'DESPESA', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Compras mensais', NOW()),
('950e8400-e29b-41d4-a716-446655440013', 'Combustível', 260.00, '2024-08-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Abastecimento posto BR', NOW()),
('950e8400-e29b-41d4-a716-446655440014', 'Aluguel', 1200.00, '2024-08-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'Aluguel apartamento', NOW()),
('950e8400-e29b-41d4-a716-446655440015', 'Plano Saúde', 320.00, '2024-08-08', 'DESPESA', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Amil - plano família', NOW()),
('950e8400-e29b-41d4-a716-446655440016', 'Dentista', 200.00, '2024-08-12', 'DESPESA', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Limpeza dental', NOW()),
('950e8400-e29b-41d4-a716-446655440017', 'Viagem Campos Jordão', 850.00, '2024-08-15', 'DESPESA', '750e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 'Final de semana romântico', NOW()),
('950e8400-e29b-41d4-a716-446655440018', 'iPhone 15 Pro', 6500.00, '2024-08-20', 'DESPESA', '750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 'Upgrade smartphone', NOW()),
('950e8400-e29b-41d4-a716-446655440019', 'Anuidade Cartão', 480.00, '2024-08-25', 'DESPESA', '750e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440001', 'Anuidade cartão Black', NOW()),

-- Julho 2024 - Despesas  
('950e8400-e29b-41d4-a716-446655440020', 'Supermercado Pão Açúcar', 380.90, '2024-07-03', 'DESPESA', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Compras mensais', NOW()),
('950e8400-e29b-41d4-a716-446655440021', 'Combustível', 290.00, '2024-07-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Abastecimento Ipiranga', NOW()),
('950e8400-e29b-41d4-a716-446655440022', 'Aluguel', 1200.00, '2024-07-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 'Aluguel apartamento', NOW()),
('950e8400-e29b-41d4-a716-446655440023', 'Plano Saúde', 320.00, '2024-07-08', 'DESPESA', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Amil - plano família', NOW()),
('950e8400-e29b-41d4-a716-446655440024', 'Curso Inglês', 250.00, '2024-07-10', 'DESPESA', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 'Mensalidade CNA', NOW()),
('950e8400-e29b-41d4-a716-446655440025', 'Tênis Nike', 380.00, '2024-07-15', 'DESPESA', '750e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 'Air Max 90', NOW()),
('950e8400-e29b-41d4-a716-446655440026', 'IPVA', 550.00, '2024-07-20', 'DESPESA', '750e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440001', 'IPVA 2024 parcela única', NOW()),
('950e8400-e29b-41d4-a716-446655440027', 'Produtos Limpeza', 85.40, '2024-07-25', 'DESPESA', '750e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', 'Produtos de limpeza', NOW());

-- ===============================================
-- TRANSAÇÕES - Maria Santos (Amostra menor)
-- ===============================================

INSERT INTO transacoes (id, descricao, valor, data_transacao, tipo, categoria_id, usuario_id, observacoes, data_criacao) VALUES 
-- Receitas Maria
('a50e8400-e29b-41d4-a716-446655440001', 'Salário Professora', 4200.00, '2024-09-05', 'RECEITA', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Salário escola estadual', NOW()),
('a50e8400-e29b-41d4-a716-446655440002', 'Aulas Particulares', 600.00, '2024-09-15', 'RECEITA', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Aulas matemática ensino médio', NOW()),
('a50e8400-e29b-41d4-a716-446655440003', 'Salário Agosto', 4200.00, '2024-08-05', 'RECEITA', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Salário escola estadual', NOW()),

-- Despesas Maria
('b50e8400-e29b-41d4-a716-446655440001', 'Supermercado', 450.00, '2024-09-10', 'DESPESA', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Compras família', NOW()),
('b50e8400-e29b-41d4-a716-446655440002', 'Financiamento Casa', 980.00, '2024-09-05', 'DESPESA', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'Parcela Caixa Econômica', NOW()),
('b50e8400-e29b-41d4-a716-446655440003', 'Material Escolar', 180.00, '2024-09-08', 'DESPESA', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440002', 'Material para aulas', NOW()),
('b50e8400-e29b-41d4-a716-446655440004', 'Uber', 85.50, '2024-09-12', 'DESPESA', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Corridas trabalho', NOW());

-- Commit das transações
COMMIT;