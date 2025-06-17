#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "     JavaFX 21 + PostgreSQL (Fixado)"
echo "==============================================="

# Verificar PostgreSQL
if ! docker ps | grep -q "jogos_postgres"; then
    echo "🚀 Iniciando PostgreSQL..."
    docker-compose up -d postgres
    sleep 5
fi

echo "✅ PostgreSQL disponível"
echo "✅ Java $(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1) detectado"

# Criar diretórios
mkdir -p build/classes lib

echo "📦 Baixando JavaFX 21 (mais estável para macOS)..."

# Detectar plataforma
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case $OS in
    darwin*) OS_NAME="mac" ;;
    linux*) OS_NAME="linux" ;;
    *) OS_NAME="win" ;;
esac

case $ARCH in
    x86_64|amd64) ARCH_NAME="x64" ;;
    aarch64|arm64) ARCH_NAME="aarch64" ;;
    *) ARCH_NAME="x64" ;;
esac

JAVAFX_PLATFORM="${OS_NAME}-${ARCH_NAME}"
JAVAFX_VERSION="21.0.1"

# Função para baixar JavaFX 21
download_javafx() {
    local component=$1
    local file="lib/javafx-${component}-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar"
    
    if [ ! -f "$file" ]; then
        echo "  📥 Baixando javafx-${component}..."
        curl -L -o "$file" "https://repo1.maven.org/maven2/org/openjfx/javafx-${component}/${JAVAFX_VERSION}/javafx-${component}-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" || {
            echo "❌ Erro ao baixar javafx-${component}"
            return 1
        }
    else
        echo "  ✅ javafx-${component} já existe"
    fi
}

# Baixar componentes JavaFX 21
download_javafx "base"
download_javafx "controls" 
download_javafx "fxml"
download_javafx "graphics"

echo ""
echo "🔨 Compilando com PostgreSQL + JavaFX 21..."

# Compilar
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

# Verificar se é macOS
IS_MACOS=false
if [[ "$OSTYPE" == "darwin"* ]]; then
    IS_MACOS=true
    echo "🍎 Detectado macOS - aplicando correções específicas"
fi

echo ""
echo "🚀 Iniciando JavaFX 21 com PostgreSQL..."

cd build/classes

# Argumentos base
JVM_ARGS=""

# macOS: Argumentos mais agressivos
if [ "$IS_MACOS" = true ]; then
    JVM_ARGS="$JVM_ARGS -XstartOnFirstThread"
    JVM_ARGS="$JVM_ARGS -Djava.awt.headless=false"
    JVM_ARGS="$JVM_ARGS -Dapple.awt.UIElement=false"
    JVM_ARGS="$JVM_ARGS -Dapple.laf.useScreenMenuBar=false"
    
    # Forçar software rendering
    JVM_ARGS="$JVM_ARGS -Dprism.order=sw"
    JVM_ARGS="$JVM_ARGS -Dprism.text=t2k"
    JVM_ARGS="$JVM_ARGS -Dprism.verbose=false"
    JVM_ARGS="$JVM_ARGS -Dprism.debug=false"
    
    # Desabilitar aceleração gráfica
    JVM_ARGS="$JVM_ARGS -Djavafx.animation.fullspeed=false"
    JVM_ARGS="$JVM_ARGS -Djavafx.animation.pulse=60"
    JVM_ARGS="$JVM_ARGS -Djavafx.embed.isEventThread=false"
    
    # Evitar problemas de threading no macOS
    JVM_ARGS="$JVM_ARGS -Dglass.win.uiScale=100%"
    JVM_ARGS="$JVM_ARGS -Dglass.gtk.uiScale=1.0"
    
    # Suprimir warnings e native access
    JVM_ARGS="$JVM_ARGS --enable-native-access=ALL-UNNAMED"
    JVM_ARGS="$JVM_ARGS --add-opens java.base/java.lang=ALL-UNNAMED"
    JVM_ARGS="$JVM_ARGS --add-opens java.desktop/sun.awt=ALL-UNNAMED"
    JVM_ARGS="$JVM_ARGS --add-opens javafx.graphics/com.sun.glass.ui.mac=ALL-UNNAMED"
    JVM_ARGS="$JVM_ARGS --add-opens javafx.graphics/com.sun.javafx.tk.quantum=ALL-UNNAMED"
fi

# Configurar JavaFX module path
JAVAFX_LIB_PATH="../../lib"
JVM_ARGS="$JVM_ARGS --module-path $JAVAFX_LIB_PATH"
JVM_ARGS="$JVM_ARGS --add-modules javafx.controls,javafx.fxml"

echo "🎯 Configurações aplicadas:"
echo "  📦 JavaFX: 21.0.1"
echo "  🗄️  Banco: PostgreSQL (Docker)"
echo "  🍎 macOS: Software Rendering"
echo ""

# Tentar execução
echo "🎮 Executando aplicação..."

java $JVM_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "🎉 JavaFX executou com sucesso!"
else
    echo ""
    echo "⚠️  JavaFX falhou (Código: $EXIT_CODE)"
    
    if [ "$IS_MACOS" = true ]; then
        echo ""
        echo "🔧 Tentativa 2: Modo de compatibilidade máxima..."
        
        # Tentar com argumentos ainda mais conservadores
        SAFE_ARGS="--enable-native-access=ALL-UNNAMED"
        SAFE_ARGS="$SAFE_ARGS --module-path $JAVAFX_LIB_PATH"
        SAFE_ARGS="$SAFE_ARGS --add-modules javafx.controls,javafx.fxml"
        SAFE_ARGS="$SAFE_ARGS -Dprism.order=sw"
        SAFE_ARGS="$SAFE_ARGS -Dprism.text=t2k"
        SAFE_ARGS="$SAFE_ARGS -Djava.awt.headless=false"
        SAFE_ARGS="$SAFE_ARGS -Dcom.apple.mrj.application.live-resize=false"
        
        echo "🔄 Executando modo seguro..."
        java $SAFE_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main
        
        if [ $? -eq 0 ]; then
            echo "✅ Sucesso no modo seguro!"
        else
            echo "❌ Ainda falhou. Usando alternativas..."
        fi
    fi
fi

echo ""
echo "💡 ALTERNATIVAS SEMPRE FUNCIONAIS:"
echo "   1. 🖥️  Console + PostgreSQL: ./run-console-postgres.sh"
echo "   2. 🌐 Interface Web: http://localhost:8080"
echo "   3. 🔧 SQLite Local: ./run-simple.sh"
echo ""
echo "👋 Finalizado" 