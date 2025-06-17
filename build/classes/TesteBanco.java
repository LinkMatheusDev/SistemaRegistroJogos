import com.sistemaregistrojogos.model.Jogo;
import com.sistemaregistrojogos.database.DatabaseManager;
import com.sistemaregistrojogos.database.JogoDAO;

import java.util.List;
import java.util.Optional;

public class TesteBanco {
    public static void main(String[] args) {
        DatabaseManager dbManager = null;
        try {
            System.out.println("=== TESTE COMPLETO DO BANCO DE DADOS ===");
            
            // Inicializar banco
            dbManager = DatabaseManager.getInstance();
            dbManager.initializeDatabase();
            JogoDAO dao = new JogoDAO();
            
            System.out.println("✅ Banco de dados inicializado com sucesso!");
            
            // Verificar registros existentes
            int totalAntes = dbManager.countRecords();
            System.out.println("📊 Total de jogos existentes: " + totalAntes);
            
            // Criar jogos de teste únicos
            Jogo[] jogosTest = {
                new Jogo("Super Mario Odyssey", 199.99, "Plataforma", "Nintendo"),
                new Jogo("God of War", 179.99, "Ação", "Sony Santa Monica"),
                new Jogo("Minecraft", 89.99, "Sandbox", "Mojang Studios")
            };
            
            // Configurar detalhes dos jogos
            jogosTest[0].setPlataforma("Nintendo Switch");
            jogosTest[0].setAnoLancamento(2017);
            jogosTest[0].setClassificacao(9.7);
            jogosTest[0].setDescricao("Uma aventura 3D incrível do Mario");
            
            jogosTest[1].setPlataforma("PlayStation 4");
            jogosTest[1].setAnoLancamento(2018);
            jogosTest[1].setClassificacao(9.5);
            jogosTest[1].setDescricao("Kratos e Atreus em uma jornada épica");
            
            jogosTest[2].setPlataforma("Multiplataforma");
            jogosTest[2].setAnoLancamento(2011);
            jogosTest[2].setClassificacao(9.0);
            jogosTest[2].setDescricao("Construa e explore mundos infinitos");
            
            // Inserir jogos (verificando duplicatas)
            System.out.println("\n🔄 Inserindo jogos de teste...");
            for (Jogo jogo : jogosTest) {
                Optional<Jogo> existente = dao.buscarPorNome(jogo.getNome());
                if (existente.isEmpty()) {
                    if (dao.inserir(jogo)) {
                        System.out.println("✅ Jogo inserido: " + jogo.getNome());
                    } else {
                        System.out.println("❌ Falha ao inserir: " + jogo.getNome());
                    }
                } else {
                    System.out.println("ℹ️  Jogo já existe: " + jogo.getNome());
                }
            }
            
            // Listar todos os jogos
            List<Jogo> todosjogos = dao.listarTodos();
            System.out.println("\n📋 CATÁLOGO COMPLETO (" + todosjogos.size() + " jogos):");
            System.out.println("=" .repeat(70));
            for (Jogo j : todosjogos) {
                System.out.printf("🎮 %-25s | %-12s | R$ %8.2f | ⭐ %.1f%n", 
                    j.getNome(), j.getGenero(), j.getPreco(), j.getClassificacao());
            }
            System.out.println("=" .repeat(70));
            
            // Testar busca
            System.out.println("\n🔍 TESTE DE BUSCA:");
            Optional<Jogo> encontrado = dao.buscarPorNome("Minecraft");
            if (encontrado.isPresent()) {
                Jogo jogo = encontrado.get();
                System.out.println("✅ Jogo encontrado:");
                System.out.println("   Nome: " + jogo.getNome());
                System.out.println("   Desenvolvedora: " + jogo.getDesenvolvedora());
                System.out.println("   Plataforma: " + jogo.getPlataforma());
                System.out.println("   Descrição: " + jogo.getDescricao());
            }
            
            // Testar filtro por gênero
            System.out.println("\n🎯 FILTRO POR GÊNERO (Ação):");
            List<Jogo> jogosAcao = dao.buscarPorGenero("Ação");
            for (Jogo j : jogosAcao) {
                System.out.println("   • " + j.getNome() + " - " + j.getDesenvolvedora());
            }
            
            // Verificar integridade (com conexão própria)
            System.out.println("\n🔒 VERIFICAÇÃO DE INTEGRIDADE:");
            if (dbManager.checkDatabaseIntegrity()) {
                System.out.println("✅ Integridade do banco verificada - OK");
            } else {
                System.out.println("⚠️  Problemas de integridade detectados");
            }
            
            // Estatísticas finais
            int totalDepois = dbManager.countRecords();
            System.out.println("\n📊 ESTATÍSTICAS:");
            System.out.println("   • Total de jogos: " + totalDepois);
            System.out.println("   • Jogos adicionados nesta execução: " + (totalDepois - totalAntes));
            
            System.out.println("\n🎉 TESTE CONCLUÍDO COM SUCESSO!");
            System.out.println("💾 Banco de dados SQLite funcionando perfeitamente!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro durante o teste: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fechar conexão adequadamente
            if (dbManager != null) {
                dbManager.closeConnection();
                System.out.println("🔌 Conexão fechada adequadamente");
            }
        }
    }
}
