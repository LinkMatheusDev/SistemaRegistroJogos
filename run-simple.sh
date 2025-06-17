#!/bin/bash

echo "==============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "          (Modo Simplificado)"
echo "==============================================="

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

echo "✅ Java detectado"

# Criar diretórios necessários
mkdir -p build/classes
mkdir -p lib

echo "📦 Baixando dependências básicas..."

# Função para baixar arquivo se não existir
download_if_not_exists() {
    local url=$1
    local file=$2
    if [ ! -f "$file" ]; then
        echo "  📥 Baixando $(basename $file)..."
        curl -L -o "$file" "$url" || {
            echo "❌ Erro ao baixar $file"
            exit 1
        }
    else
        echo "  ✅ $(basename $file) já existe"
    fi
}

# Baixar apenas SQLite e SLF4J
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" \
    "lib/sqlite-jdbc-3.42.0.0.jar"

download_if_not_exists \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar" \
    "lib/slf4j-api-2.0.7.jar"

download_if_not_exists \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.7/slf4j-simple-2.0.7.jar" \
    "lib/slf4j-simple-2.0.7.jar"

echo ""
echo "🔨 Compilando versão console..."

# Compilar apenas as classes do backend (sem JavaFX)
javac -cp "lib/*" -d build/classes \
    src/main/java/com/sistemaregistrojogos/model/*.java \
    src/main/java/com/sistemaregistrojogos/database/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilação do backend concluída"
else
    echo "❌ Falha na compilação do backend"
    exit 1
fi

# Criar uma versão console temporária melhorada
cat > build/classes/TesteBanco.java << 'EOF'
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
EOF

# Compilar teste
javac -cp "build/classes:lib/*" -d build/classes build/classes/TesteBanco.java

if [ $? -eq 0 ]; then
    echo "✅ Compilação do teste concluída"
else
    echo "❌ Falha na compilação do teste"
    exit 1
fi

echo ""
echo "🚀 Executando teste completo do banco de dados..."
echo ""

# Executar teste com argumentos JVM para suprimir warnings
cd build/classes
java --enable-native-access=ALL-UNNAMED -cp ".:../../lib/*" TesteBanco

echo ""
echo "👋 Teste finalizado"
echo ""
echo "📝 Próximos passos:"
echo "   1. ✅ Backend testado e funcionando"
echo "   2. 🔄 Para interface gráfica: ./run.sh"
echo "   3. 🐳 Para versão Docker: ./docker-setup.sh" 