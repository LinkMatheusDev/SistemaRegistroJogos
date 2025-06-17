#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "    Console + PostgreSQL (Docker)"
echo "==============================================="

# Verificar PostgreSQL
echo "🔍 Verificando PostgreSQL..."
if ! docker ps | grep -q "jogos_postgres"; then
    echo "❌ PostgreSQL não está rodando"
    echo "🚀 Iniciando PostgreSQL..."
    docker-compose up -d postgres
    sleep 10
fi

if docker ps | grep -q "jogos_postgres"; then
    echo "✅ PostgreSQL (Docker) está rodando"
else
    echo "❌ Falha ao iniciar PostgreSQL"
    exit 1
fi

# Verificar Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✅ Java $JAVA_VERSION detectado"

# Compilar se necessário
if [ ! -f "build/classes/com/sistemaregistrojogos/ConsoleApp.class" ]; then
    echo "🔨 Compilando..."
    mkdir -p build/classes
    find src/main/java -name "*.java" -print0 | xargs -0 javac -cp "lib/*" -d build/classes
fi

echo ""
echo "🎮 SISTEMA DE REGISTRO DE JOGOS"
echo "   Backend: PostgreSQL (Docker)"
echo "   Interface: Console/Terminal"
echo ""

cd build/classes

# Menu interativo
while true; do
    echo "────────────────────────────────────────"
    echo "📋 MENU PRINCIPAL:"
    echo "   1. 📊 Listar todos os jogos"
    echo "   2. ➕ Adicionar novo jogo"
    echo "   3. 🔍 Buscar jogos"
    echo "   4. 📈 Estatísticas"
    echo "   5. 🗑️  Remover jogo"
    echo "   6. 🌐 Abrir pgAdmin (Web)"
    echo "   7. ❌ Sair"
    echo "────────────────────────────────────────"
    
    read -p "Escolha uma opção (1-7): " opcao
    
    case $opcao in
        1)
            echo ""
            echo "📊 LISTANDO JOGOS:"
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                SELECT 
                    id,
                    nome,
                    'R$ ' || preco as preco,
                    genero,
                    desenvolvedor,
                    avaliacao || '/10' as nota
                FROM jogos 
                ORDER BY nome;
            "
            ;;
            
        2)
            echo ""
            echo "➕ ADICIONAR NOVO JOGO:"
            read -p "Nome do jogo: " nome
            read -p "Preço (ex: 99.99): " preco
            read -p "Gênero: " genero
            read -p "Desenvolvedor: " desenvolvedor
            read -p "Plataforma: " plataforma
            read -p "Ano de lançamento: " ano
            read -p "Avaliação (0-10): " avaliacao
            read -p "Descrição: " descricao
            
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                INSERT INTO jogos (nome, preco, genero, desenvolvedor, plataforma, ano_lancamento, avaliacao, descricao)
                VALUES ('$nome', $preco, '$genero', '$desenvolvedor', '$plataforma', $ano, $avaliacao, '$descricao');
            "
            
            if [ $? -eq 0 ]; then
                echo "✅ Jogo adicionado com sucesso!"
            else
                echo "❌ Erro ao adicionar jogo"
            fi
            ;;
            
        3)
            echo ""
            echo "🔍 BUSCAR JOGOS:"
            read -p "Digite o nome ou parte do nome: " busca
            
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                SELECT 
                    id,
                    nome,
                    'R$ ' || preco as preco,
                    genero,
                    avaliacao || '/10' as nota
                FROM jogos 
                WHERE nome ILIKE '%$busca%'
                ORDER BY nome;
            "
            ;;
            
        4)
            echo ""
            echo "📈 ESTATÍSTICAS:"
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                SELECT 
                    COUNT(*) as total_jogos,
                    'R$ ' || ROUND(AVG(preco), 2) as preco_medio,
                    ROUND(AVG(avaliacao), 1) || '/10' as avaliacao_media,
                    MIN(ano_lancamento) as ano_mais_antigo,
                    MAX(ano_lancamento) as ano_mais_recente
                FROM jogos;
                
                SELECT 'Top 3 Gêneros:' as estatistica;
                SELECT genero, COUNT(*) as quantidade 
                FROM jogos 
                GROUP BY genero 
                ORDER BY quantidade DESC 
                LIMIT 3;
            "
            ;;
            
        5)
            echo ""
            echo "🗑️  REMOVER JOGO:"
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                SELECT id, nome FROM jogos ORDER BY nome;
            "
            
            read -p "Digite o ID do jogo para remover: " id_jogo
            
            docker exec jogos_postgres psql -U jogos_user -d sistemaregistrojogos -c "
                SET search_path TO jogos_app, public;
                DELETE FROM jogos WHERE id = $id_jogo;
            "
            
            if [ $? -eq 0 ]; then
                echo "✅ Jogo removido com sucesso!"
            else
                echo "❌ Erro ao remover jogo"
            fi
            ;;
            
        6)
            echo ""
            echo "🌐 Abrindo pgAdmin no navegador..."
            echo "   URL: http://localhost:8080"
            echo "   Email: admin@jogos.com"
            echo "   Senha: admin123"
            
            # Tentar abrir automaticamente no macOS
            if command -v open &> /dev/null; then
                open http://localhost:8080
            else
                echo "   Abra manualmente no seu navegador"
            fi
            ;;
            
        7)
            echo ""
            echo "👋 Saindo do sistema..."
            echo "✅ PostgreSQL continua rodando"
            echo "💡 Para parar: docker-compose down"
            exit 0
            ;;
            
        *)
            echo "❌ Opção inválida. Escolha entre 1-7."
            ;;
    esac
    
    echo ""
    read -p "Pressione Enter para continuar..."
    echo ""
done 