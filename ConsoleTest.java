import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Teste simples do backend SQLite
 * Para verificar se o sistema está funcionando sem JavaFX
 */
public class ConsoleTest {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    Teste Simples do Backend SQLite");
        System.out.println("═══════════════════════════════════════");
        System.out.println();
        
        try {
            // Testar conexão SQLite
            System.out.println("🔌 Testando conexão SQLite...");
            String url = "jdbc:sqlite:jogos.db";
            
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("✅ Conexão SQLite estabelecida!");
                    
                    // Criar tabela se não existir
                    String createTable = """
                        CREATE TABLE IF NOT EXISTS jogos (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            preco REAL NOT NULL,
                            genero TEXT,
                            desenvolvedor TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """;
                    
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(createTable);
                        System.out.println("✅ Tabela verificada/criada!");
                        
                        // Inserir jogo de teste
                        String insert = """
                            INSERT OR REPLACE INTO jogos (nome, preco, genero, desenvolvedor) 
                            VALUES ('Teste macOS Game', 59.99, 'Action', 'Test Studio')
                        """;
                        stmt.execute(insert);
                        System.out.println("✅ Jogo de teste inserido!");
                        
                        // Listar jogos
                        String query = "SELECT * FROM jogos ORDER BY created_at DESC LIMIT 10";
                        try (ResultSet rs = stmt.executeQuery(query)) {
                            System.out.println();
                            System.out.println("📋 Jogos no banco:");
                            int count = 0;
                            while (rs.next()) {
                                count++;
                                System.out.printf("   🎮 %d. %s - R$ %.2f (%s) - %s%n",
                                    rs.getInt("id"),
                                    rs.getString("nome"),
                                    rs.getDouble("preco"),
                                    rs.getString("genero"),
                                    rs.getString("desenvolvedor")
                                );
                            }
                            
                            if (count == 0) {
                                System.out.println("   📭 Nenhum jogo encontrado");
                            } else {
                                System.out.println("   📦 Total: " + count + " jogo(s)");
                            }
                        }
                        
                        // Estatísticas
                        System.out.println();
                        System.out.println("📊 Estatísticas:");
                        
                        String countQuery = "SELECT COUNT(*) as total FROM jogos";
                        try (ResultSet rs = stmt.executeQuery(countQuery)) {
                            if (rs.next()) {
                                System.out.println("   🎮 Total de jogos: " + rs.getInt("total"));
                            }
                        }
                        
                        String avgQuery = "SELECT AVG(preco) as media FROM jogos";
                        try (ResultSet rs = stmt.executeQuery(avgQuery)) {
                            if (rs.next()) {
                                double media = rs.getDouble("media");
                                if (media > 0) {
                                    System.out.printf("   💰 Preço médio: R$ %.2f%n", media);
                                }
                            }
                        }
                    }
                }
            }
            
            System.out.println();
            System.out.println("🎉 TESTE COMPLETO - SUCESSO!");
            System.out.println("✅ Backend SQLite está funcionando perfeitamente!");
            System.out.println();
            System.out.println("💡 Próximos passos:");
            System.out.println("   1. 🎯 Backend funcional: ./run-simple.sh");
            System.out.println("   2. 🖥️  Interface JavaFX: ./run.sh");
            System.out.println("   3. 🐳 Interface Web: ./docker-setup.sh");
            System.out.println("   4. 🍎 Diagnóstico macOS: ./run-macos.sh");
            
        } catch (Exception e) {
            System.err.println("❌ ERRO NO TESTE:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            
            System.out.println();
            System.out.println("🔧 Possíveis soluções:");
            System.out.println("   1. Verificar se SQLite JDBC está no classpath");
            System.out.println("   2. Verificar permissões de escrita no diretório");
            System.out.println("   3. Tentar com: java -cp '.:lib/*' ConsoleTest");
        }
        
        System.out.println();
        System.out.println("👋 Teste concluído!");
    }
} 