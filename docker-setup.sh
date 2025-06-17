#!/bin/bash

echo "🐳=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "           Docker Setup"
echo "==============================================="

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não encontrado. Por favor, instale o Docker Desktop."
    echo "   📥 Download: https://www.docker.com/products/docker-desktop/"
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null 2>&1; then
    echo "❌ Docker Compose não encontrado."
    exit 1
fi

echo "✅ Docker detectado"

# Função para escolher comando compose
get_compose_cmd() {
    if command -v docker-compose &> /dev/null; then
        echo "docker-compose"
    else
        echo "docker compose"
    fi
}

COMPOSE_CMD=$(get_compose_cmd)
echo "✅ Usando comando: $COMPOSE_CMD"

# Menu de opções
show_menu() {
    echo ""
    echo "📋 Opções disponíveis:"
    echo "   1. 🚀 Iniciar serviços (PostgreSQL + pgAdmin)"
    echo "   2. 🛑 Parar serviços"
    echo "   3. 🔄 Reiniciar serviços"
    echo "   4. 📊 Ver status dos serviços"
    echo "   5. 📋 Ver logs"
    echo "   6. 🧹 Limpar tudo (CUIDADO: Remove dados!)"
    echo "   7. 💾 Backup do banco"
    echo "   8. 🔗 Informações de conexão"
    echo "   9. ❌ Sair"
    echo ""
}

# Função para iniciar serviços
start_services() {
    echo "🚀 Iniciando serviços Docker..."
    
    # Criar diretório para banco se não existir
    mkdir -p database
    
    $COMPOSE_CMD up -d
    
    if [ $? -eq 0 ]; then
        echo "✅ Serviços iniciados com sucesso!"
        echo ""
        echo "📊 Aguardando inicialização do PostgreSQL..."
        sleep 10
        
        # Verificar se PostgreSQL está funcionando
        docker exec jogos_postgres pg_isready -U jogos_user -d sistemaregistrojogos > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo "✅ PostgreSQL está funcionando!"
            show_connection_info
        else
            echo "⚠️  PostgreSQL ainda está inicializando. Aguarde alguns momentos."
            echo "   Execute novamente 'Informações de conexão' em alguns minutos."
        fi
    else
        echo "❌ Erro ao iniciar serviços"
    fi
}

# Função para parar serviços
stop_services() {
    echo "🛑 Parando serviços..."
    $COMPOSE_CMD down
    echo "✅ Serviços parados"
}

# Função para reiniciar serviços
restart_services() {
    echo "🔄 Reiniciando serviços..."
    $COMPOSE_CMD restart
    echo "✅ Serviços reiniciados"
}

# Função para ver status
show_status() {
    echo "📊 Status dos serviços:"
    $COMPOSE_CMD ps
}

# Função para ver logs
show_logs() {
    echo "📋 Logs dos serviços:"
    echo "   Digite Ctrl+C para sair"
    $COMPOSE_CMD logs -f
}

# Função para limpar tudo
clean_all() {
    echo "⚠️  ATENÇÃO: Esta ação irá remover TODOS os dados!"
    read -p "   Digite 'CONFIRMAR' para continuar: " confirm
    
    if [ "$confirm" = "CONFIRMAR" ]; then
        echo "🧹 Removendo containers, volumes e dados..."
        $COMPOSE_CMD down -v
        docker volume prune -f
        echo "✅ Limpeza concluída"
    else
        echo "❌ Operação cancelada"
    fi
}

# Função para backup
backup_database() {
    echo "💾 Criando backup do banco de dados..."
    
    # Verificar se PostgreSQL está rodando
    if ! docker exec jogos_postgres pg_isready -U jogos_user -d sistemaregistrojogos > /dev/null 2>&1; then
        echo "❌ PostgreSQL não está funcionando. Inicie os serviços primeiro."
        return
    fi
    
    BACKUP_FILE="backup_jogos_$(date +%Y%m%d_%H%M%S).sql"
    
    docker exec jogos_postgres pg_dump -U jogos_user -d sistemaregistrojogos > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✅ Backup criado: $BACKUP_FILE"
        echo "   Tamanho: $(du -h "$BACKUP_FILE" | cut -f1)"
    else
        echo "❌ Erro ao criar backup"
    fi
}

# Função para mostrar informações de conexão
show_connection_info() {
    echo ""
    echo "🔗 INFORMAÇÕES DE CONEXÃO:"
    echo "=================================================="
    echo ""
    echo "📊 PostgreSQL:"
    echo "   Host: localhost"
    echo "   Port: 5432"
    echo "   Database: sistemaregistrojogos"
    echo "   Username: jogos_user"
    echo "   Password: jogos_password"
    echo "   Schema: jogos_app"
    echo ""
    echo "🌐 pgAdmin (Interface Web):"
    echo "   URL: http://localhost:8080"
    echo "   Email: admin@jogos.com"
    echo "   Password: admin123"
    echo ""
    echo "📱 Redis (Cache):"
    echo "   Host: localhost"
    echo "   Port: 6379"
    echo ""
    echo "🔌 String de Conexão JDBC:"
    echo "   jdbc:postgresql://localhost:5432/sistemaregistrojogos"
    echo ""
    echo "💡 Para conectar via psql:"
    echo "   psql -h localhost -p 5432 -U jogos_user -d sistemaregistrojogos"
    echo ""
    echo "=================================================="
}

# Loop principal
while true; do
    show_menu
    read -p "Escolha uma opção (1-9): " choice
    
    case $choice in
        1)
            start_services
            ;;
        2)
            stop_services
            ;;
        3)
            restart_services
            ;;
        4)
            show_status
            ;;
        5)
            show_logs
            ;;
        6)
            clean_all
            ;;
        7)
            backup_database
            ;;
        8)
            show_connection_info
            ;;
        9)
            echo "👋 Saindo..."
            exit 0
            ;;
        *)
            echo "❌ Opção inválida. Escolha entre 1-9."
            ;;
    esac
    
    echo ""
    read -p "Pressione Enter para continuar..."
done 