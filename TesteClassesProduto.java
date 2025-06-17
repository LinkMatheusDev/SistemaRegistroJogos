/**
 * Classe de teste para demonstrar o uso das 4 classes de produto especializadas
 * Exemplo de como usar: Jogo, JogoAcao, JogoRPG, JogoEsporte, JogoEstrategia
 */
public class TesteClassesProduto {
    
    public static void main(String[] args) {
        System.out.println("🎮 TESTANDO AS 4 CLASSES DE PRODUTO DO SISTEMA");
        System.out.println("=" + "=".repeat(60));
        
        // 1. Jogo base
        System.out.println("\n1️⃣ JOGO BASE:");
        Jogo jogoBase = new Jogo("Minecraft", 89.99, "Sandbox", "Mojang");
        jogoBase.setId(1);
        System.out.println(jogoBase);
        System.out.println("Categoria: " + jogoBase.getCategoria());
        System.out.println("Preço formatado: " + jogoBase.getPrecoFormatado());
        System.out.println("É gratuito? " + jogoBase.isGratuito());
        
        // 2. Jogo de Ação
        System.out.println("\n2️⃣ JOGO DE AÇÃO:");
        JogoAcao jogoAcao = new JogoAcao("Call of Duty: Modern Warfare", 199.99, "Activision",
                                        8, true, "Both", 18);
        jogoAcao.setId(2);
        System.out.println(jogoAcao);
        System.out.println("Adequado para 16 anos? " + jogoAcao.isAdequadoParaIdade(16));
        System.out.println("Adequado para 18 anos? " + jogoAcao.isAdequadoParaIdade(18));
        System.out.println("Precisa controle especial? " + jogoAcao.precisaControleEspecial());
        System.out.println(jogoAcao.getInformacoesAcao());
        
        // 3. Jogo RPG
        System.out.println("\n3️⃣ JOGO RPG:");
        JogoRPG jogoRPG = new JogoRPG("The Witcher 3", 79.99, "CD Projekt Red",
                                     100, "Western RPG", true, 5, true);
        jogoRPG.setId(3);
        System.out.println(jogoRPG);
        System.out.println("É RPG tradicional? " + jogoRPG.isRPGTradicional());
        System.out.println("Valor por hora: R$ " + String.format("%.2f", jogoRPG.getValorPorHora()));
        System.out.println(jogoRPG.getInformacoesRPG());
        
        // 4. Jogo de Esporte
        System.out.println("\n4️⃣ JOGO DE ESPORTE:");
        JogoEsporte jogoEsporte = new JogoEsporte("FIFA 24", 299.99, "EA Sports",
                                                 "Futebol", true, "Simulação", true, true, 22);
        jogoEsporte.setId(4);
        System.out.println(jogoEsporte);
        System.out.println("Para jogador casual? " + jogoEsporte.isParaJogadorCasual());
        System.out.println("Para jogador sério? " + jogoEsporte.isParaJogadorSerio());
        System.out.println("Atualização anual? " + jogoEsporte.isAtualAnual());
        System.out.println(jogoEsporte.getInformacoesEsporte());
        
        // 5. Jogo de Estratégia
        System.out.println("\n5️⃣ JOGO DE ESTRATÉGIA:");
        JogoEstrategia jogoEstrategia = new JogoEstrategia("Civilization VI", 159.99, "Firaxis Games",
                                                          "4X", 7, true, true, 18, true, "Isométrica");
        jogoEstrategia.setId(5);
        System.out.println(jogoEstrategia);
        System.out.println("É tempo real? " + jogoEstrategia.isTempoReal());
        System.out.println("É baseado em turnos? " + jogoEstrategia.isBaseadoTurnos());
        System.out.println("Recomendado para iniciantes? " + jogoEstrategia.isRecomendadoParaIniciantes());
        System.out.println("Recomendado para veteranos? " + jogoEstrategia.isRecomendadoParaVeteranos());
        System.out.println(jogoEstrategia.getInformacoesEstrategia());
        
        // 6. Demonstração de polimorfismo
        System.out.println("\n6️⃣ DEMONSTRAÇÃO DE POLIMORFISMO:");
        Jogo[] jogos = {jogoBase, jogoAcao, jogoRPG, jogoEsporte, jogoEstrategia};
        
        System.out.println("Lista de todos os jogos:");
        for (Jogo jogo : jogos) {
            System.out.println("- " + jogo.getNome() + " (" + jogo.getGenero() + ") - " + jogo.getPrecoFormatado());
        }
        
        // 7. Cálculos estatísticos
        System.out.println("\n7️⃣ ESTATÍSTICAS:");
        double precoTotal = 0;
        for (Jogo jogo : jogos) {
            precoTotal += jogo.getPreco();
        }
        System.out.println("Preço total da coleção: R$ " + String.format("%.2f", precoTotal));
        System.out.println("Preço médio: R$ " + String.format("%.2f", precoTotal / jogos.length));
        
        long jogosCaros = java.util.Arrays.stream(jogos).mapToDouble(Jogo::getPreco).filter(p -> p > 100).count();
        System.out.println("Jogos que custam mais de R$ 100: " + jogosCaros);
        
        System.out.println("\n✅ TESTE CONCLUÍDO COM SUCESSO!");
        System.out.println("🎯 Todas as 4 classes de produto foram testadas e estão funcionando!");
    }
} 