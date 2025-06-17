#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "      JavaFX + PostgreSQL (Docker)"
echo "==============================================="

# Verificar se PostgreSQL está rodando
echo "🔍 Verificando PostgreSQL..."
if docker ps | grep -q "jogos_postgres"; then
    echo "✅ PostgreSQL (Docker) está rodando"
else
    echo "❌ PostgreSQL não está rodando"
    echo "🚀 Iniciando PostgreSQL..."
    
    if command -v docker-compose &> /dev/null; then
        docker-compose up -d postgres
    else
        docker compose up -d postgres
    fi
    
    # Aguardar PostgreSQL inicializar
    echo "⏳ Aguardando PostgreSQL inicializar..."
    sleep 10
    
    # Verificar novamente
    if docker ps | grep -q "jogos_postgres"; then
        echo "✅ PostgreSQL iniciado com sucesso"
    else
        echo "❌ Falha ao iniciar PostgreSQL"
        echo "💡 Execute manualmente: ./docker-setup.sh"
        exit 1
    fi
fi

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ necessário. Atual: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"

# Criar diretórios
mkdir -p build/classes

echo ""
echo "🔨 Compilando projeto..."

# Compilar com PostgreSQL driver
find src/main/java -name "*.java" -print0 | xargs -0 javac -cp "lib/*" -d build/classes

if [ $? -ne 0 ]; then
    echo "❌ Falha na compilação"
    exit 1
fi

# Copiar recursos
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* build/classes/ 2>/dev/null || true
fi

echo "✅ Compilação concluída"

# Verificar se arquivos FXML existem
if [ ! -f "build/classes/view/main-view.fxml" ]; then
    echo "⚠️  Arquivos FXML não encontrados, copiando..."
    mkdir -p build/classes/view
    if [ -f "src/main/resources/view/main-view.fxml" ]; then
        cp src/main/resources/view/*.fxml build/classes/view/ 2>/dev/null || true
    fi
fi

echo ""
echo "🚀 Iniciando JavaFX com PostgreSQL..."
echo ""

cd build/classes

# Detectar macOS para argumentos específicos
IS_MACOS=false
if [[ "$OSTYPE" == "darwin"* ]]; then
    IS_MACOS=true
fi

# Construir argumentos JVM
JVM_ARGS="--enable-native-access=ALL-UNNAMED"
JVM_ARGS="$JVM_ARGS --add-opens java.base/java.lang=ALL-UNNAMED"
JVM_ARGS="$JVM_ARGS --add-opens java.desktop/sun.awt=ALL-UNNAMED"

# Configurar JavaFX
JAVAFX_LIB_PATH="../../lib"
JVM_ARGS="$JVM_ARGS --module-path $JAVAFX_LIB_PATH"
JVM_ARGS="$JVM_ARGS --add-modules javafx.controls,javafx.fxml"

# Argumentos específicos para macOS
if [ "$IS_MACOS" = true ]; then
    echo "🍎 Aplicando correções para macOS..."
    JVM_ARGS="$JVM_ARGS -Dprism.order=sw"
    JVM_ARGS="$JVM_ARGS -Dprism.text=t2k"
    JVM_ARGS="$JVM_ARGS -Djava.awt.headless=false"
    JVM_ARGS="$JVM_ARGS -Dapple.awt.UIElement=false"
fi

echo "🎮 Conectando JavaFX ao PostgreSQL..."
echo "📊 Banco: PostgreSQL (localhost:5432)"
echo "🌐 Interface: JavaFX Desktop"
echo ""

# Executar aplicação
java $JVM_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main

EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo "⚠️  Erro na execução (Código: $EXIT_CODE)"
    echo ""
    echo "🔍 INFORMAÇÕES DE DEBUG:"
    echo "  📦 Banco: $(docker ps | grep postgres | wc -l) container(s) PostgreSQL"
    echo "  🧱 JARs: $(ls -1 ../../lib/*.jar 2>/dev/null | wc -l) dependências"
    echo "  ☕ Java: $JAVA_VERSION"
    echo ""
    echo "💡 ALTERNATIVAS:"
    echo "   1. 🌐 Interface Web: http://localhost:8080"
    echo "   2. 🧪 Teste backend: java -cp '.:../../lib/*' ConsoleTest"
    echo "   3. 🔧 SQLite local: ./run-simple.sh"
    echo "   4. 🍎 Diagnóstico macOS: ./run-macos.sh"
fi

echo ""
echo "👋 Finalizado" 