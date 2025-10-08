package com.financeiro.migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Script de migração para adicionar relacionamento Usuario-Categoria
 * EXECUTAR UMA ÚNICA VEZ em produção
 */
public class MigrationCategoriaUsuario {

    public static void main(String[] args) {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            System.err.println("❌ Erro: DATABASE_URL não configurada");
            System.exit(1);
        }

        System.out.println("🔍 Conectando ao banco de dados...");
        
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            conn.setAutoCommit(false);
            
            System.out.println("✅ Conexão estabelecida");
            System.out.println("📊 Iniciando migração...\n");
            
            try (Statement stmt = conn.createStatement()) {
                
                // Passo 1: Adicionar coluna
                System.out.println("⏳ Passo 1: Adicionando coluna usuario_id...");
                stmt.execute("ALTER TABLE categorias ADD COLUMN usuario_id UUID");
                System.out.println("✅ Coluna adicionada\n");
                
                // Passo 2: Vincular categorias aos usuários das transações
                System.out.println("⏳ Passo 2: Vinculando categorias aos usuários das transações...");
                int updated = stmt.executeUpdate(
                    "WITH ult_movimentacao AS (" +
                    "    SELECT DISTINCT ON (t.categoria_id) " +
                    "           t.categoria_id, t.usuario_id " +
                    "    FROM transacoes t " +
                    "    ORDER BY t.categoria_id, t.data_transacao DESC, t.data_criacao DESC" +
                    ") " +
                    "UPDATE categorias c " +
                    "SET usuario_id = u.usuario_id " +
                    "FROM ult_movimentacao u " +
                    "WHERE c.id = u.categoria_id"
                );
                System.out.println("✅ " + updated + " categorias vinculadas via transações\n");
                
                // Passo 3: Vincular categorias órfãs ao admin
                System.out.println("⏳ Passo 3: Vinculando categorias restantes ao admin...");
                updated = stmt.executeUpdate(
                    "UPDATE categorias c " +
                    "SET usuario_id = admin.id " +
                    "FROM usuarios admin " +
                    "WHERE c.usuario_id IS NULL " +
                    "  AND admin.email = 'admin@financeiro.com'"
                );
                System.out.println("✅ " + updated + " categorias vinculadas ao admin\n");
                
                // Passo 4: Verificar se ainda existem nulos
                System.out.println("⏳ Passo 4: Verificando categorias sem usuário...");
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categorias WHERE usuario_id IS NULL");
                rs.next();
                int nullCount = rs.getInt(1);
                if (nullCount > 0) {
                    System.err.println("❌ ERRO: Ainda existem " + nullCount + " categorias sem usuario_id");
                    conn.rollback();
                    System.exit(1);
                }
                System.out.println("✅ Todas as categorias possuem usuário\n");
                
                // Passo 5: Tornar NOT NULL
                System.out.println("⏳ Passo 5: Tornando coluna obrigatória...");
                stmt.execute("ALTER TABLE categorias ALTER COLUMN usuario_id SET NOT NULL");
                System.out.println("✅ Constraint NOT NULL aplicada\n");
                
                // Passo 6: Criar FK
                System.out.println("⏳ Passo 6: Criando chave estrangeira...");
                stmt.execute(
                    "ALTER TABLE categorias " +
                    "ADD CONSTRAINT fk_categoria_usuario " +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios (id)"
                );
                System.out.println("✅ Chave estrangeira criada\n");
                
                // Passo 7: Criar índice único
                System.out.println("⏳ Passo 7: Criando índice único por usuário...");
                stmt.execute("DROP INDEX IF EXISTS uq_categoria_nome");
                stmt.execute(
                    "CREATE UNIQUE INDEX uq_categoria_usuario_nome " +
                    "ON categorias (usuario_id, lower(nome))"
                );
                System.out.println("✅ Índice único criado\n");
                
                // Commit
                conn.commit();
                System.out.println("✅ MIGRAÇÃO CONCLUÍDA COM SUCESSO!\n");
                
                // Verificação
                System.out.println("📊 Distribuição de categorias por usuário:");
                rs = stmt.executeQuery(
                    "SELECT u.email, COUNT(c.id) as total " +
                    "FROM categorias c " +
                    "JOIN usuarios u ON u.id = c.usuario_id " +
                    "GROUP BY u.email " +
                    "ORDER BY total DESC"
                );
                
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("email") + ": " + rs.getInt("total") + " categorias");
                }
                
            } catch (Exception e) {
                System.err.println("\n❌ Erro durante migração: " + e.getMessage());
                e.printStackTrace();
                conn.rollback();
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro de conexão: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
