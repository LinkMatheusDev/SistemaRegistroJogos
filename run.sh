#!/bin/bash

echo "==============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "==============================================="

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

# Verificar versão do Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 ou superior é necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"

# Verificar se estamos no macOS
IS_MACOS=false
if [[ "$OSTYPE" == "darwin"* ]]; then
    IS_MACOS=true
    echo "🍎 macOS detectado - aplicando correções específicas"
fi

# Criar diretórios necessários
mkdir -p build/classes
mkdir -p lib

echo "📦 Baixando dependências..."

# URLs das dependências - usando JavaFX 17 para maior compatibilidade
JAVAFX_VERSION="17.0.2"
SQLITE_VERSION="3.42.0.0"
SLF4J_VERSION="2.0.7"

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

# Baixar SQLite JDBC
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/${SQLITE_VERSION}/sqlite-jdbc-${SQLITE_VERSION}.jar" \
    "lib/sqlite-jdbc-${SQLITE_VERSION}.jar"

# Baixar SLF4J API
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/${SLF4J_VERSION}/slf4j-api-${SLF4J_VERSION}.jar" \
    "lib/slf4j-api-${SLF4J_VERSION}.jar"

# Baixar SLF4J Simple
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/${SLF4J_VERSION}/slf4j-simple-${SLF4J_VERSION}.jar" \
    "lib/slf4j-simple-${SLF4J_VERSION}.jar"

# Detectar OS e arquitetura para JavaFX
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case $OS in
    darwin*) OS_NAME="mac" ;;
    linux*) OS_NAME="linux" ;;
    mingw*|cygwin*|msys*) OS_NAME="win" ;;
    *) echo "❌ Sistema operacional não suportado: $OS"; exit 1 ;;
esac

case $ARCH in
    x86_64|amd64) ARCH_NAME="x64" ;;
    aarch64|arm64) ARCH_NAME="aarch64" ;;
    *) echo "❌ Arquitetura não suportada: $ARCH"; exit 1 ;;
esac

JAVAFX_PLATFORM="${OS_NAME}-${ARCH_NAME}"
echo "🎯 Plataforma detectada: $JAVAFX_PLATFORM"

# Baixar JavaFX Controls
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/${JAVAFX_VERSION}/javafx-controls-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-controls-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar"

# Baixar JavaFX Base
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/${JAVAFX_VERSION}/javafx-base-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-base-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar"

# Baixar JavaFX Graphics
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/${JAVAFX_VERSION}/javafx-graphics-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-graphics-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar"

# Baixar JavaFX FXML
download_if_not_exists \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/${JAVAFX_VERSION}/javafx-fxml-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-fxml-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar"

echo ""
echo "🔨 Compilando projeto..."

# Construir classpath para compilação
CLASSPATH="lib/*"

# Compilar o projeto usando classpath
find src/main/java -name "*.java" -print0 | xargs -0 javac -cp "$CLASSPATH" -d build/classes

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso"
else
    echo "❌ Falha na compilação"
    exit 1
fi

# Copiar recursos
if [ -d "src/main/resources" ]; then
    echo "📁 Copiando recursos..."
    cp -r src/main/resources/* build/classes/ 2>/dev/null || true
fi

echo ""
echo "🚀 Iniciando aplicação..."
echo ""

# Executar a aplicação com configuração adequada do JavaFX
cd build/classes

# Construir o module path para JavaFX
JAVAFX_LIB_PATH="../../lib"
MODULE_PATH="$JAVAFX_LIB_PATH"

# Definir argumentos JVM base
JVM_ARGS="--enable-native-access=ALL-UNNAMED"
JVM_ARGS="$JVM_ARGS --add-opens java.base/java.lang=ALL-UNNAMED"
JVM_ARGS="$JVM_ARGS --add-opens java.desktop/sun.awt=ALL-UNNAMED"

# Configurar JavaFX
JVM_ARGS="$JVM_ARGS --module-path $MODULE_PATH"
JVM_ARGS="$JVM_ARGS --add-modules javafx.controls,javafx.fxml"

# Argumentos específicos para macOS
if [ "$IS_MACOS" = true ]; then
    echo "🍎 Aplicando correções para macOS..."
    JVM_ARGS="$JVM_ARGS -XstartOnFirstThread"
    JVM_ARGS="$JVM_ARGS -Djava.awt.headless=false"
    JVM_ARGS="$JVM_ARGS -Dapple.awt.UIElement=false"
    JVM_ARGS="$JVM_ARGS -Dapple.laf.useScreenMenuBar=true"
    JVM_ARGS="$JVM_ARGS -Dcom.apple.mrj.application.apple.menu.about.name=SistemaJogos"
    JVM_ARGS="$JVM_ARGS -Dprism.order=sw"
    JVM_ARGS="$JVM_ARGS -Dprism.verbose=false"
    JVM_ARGS="$JVM_ARGS -Djavafx.animation.pulse=60"
    JVM_ARGS="$JVM_ARGS -Dglass.win.uiScale=100%"
    
    # Suprimir warnings específicos do macOS
    JVM_ARGS="$JVM_ARGS --add-opens javafx.graphics/com.sun.glass.ui.mac=ALL-UNNAMED"
    JVM_ARGS="$JVM_ARGS --add-opens javafx.graphics/com.sun.javafx.tk.quantum=ALL-UNNAMED"
fi

# Tentar executar com configuração completa
echo "🎮 Iniciando interface gráfica..."
java $JVM_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main

EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo "⚠️  Erro ao executar interface JavaFX (Código: $EXIT_CODE)"
    
    if [ "$IS_MACOS" = true ]; then
        echo ""
        echo "🍎 Tentativa alternativa para macOS com software rendering..."
        
        # Tentar com software rendering
        ALT_JVM_ARGS="--enable-native-access=ALL-UNNAMED"
        ALT_JVM_ARGS="$ALT_JVM_ARGS --module-path $MODULE_PATH"
        ALT_JVM_ARGS="$ALT_JVM_ARGS --add-modules javafx.controls,javafx.fxml"
        ALT_JVM_ARGS="$ALT_JVM_ARGS -Dprism.order=sw"
        ALT_JVM_ARGS="$ALT_JVM_ARGS -Dprism.text=t2k"
        ALT_JVM_ARGS="$ALT_JVM_ARGS -Djava.awt.headless=false"
        
        java $ALT_JVM_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main
        
        if [ $? -ne 0 ]; then
            echo "❌ Falha também com software rendering"
        fi
    fi
    
    echo ""
    echo "💡 ALTERNATIVAS FUNCIONAIS:"
    echo "   1. ✅ Backend SQLite (100% funcional):"
    echo "      ./run-simple.sh"
    echo ""
    echo "   2. 🐳 PostgreSQL + Interface Web:"
    echo "      ./docker-setup.sh"
    echo "      Depois acesse: http://localhost:8080"
    echo ""
    echo "   3. 📖 Instalar JavaFX oficial:"
    echo "      brew install --cask temurin17"
    echo "      brew install openjfx"
    echo ""
    echo "📋 O backend está 100% funcional!"
    echo "   Use as alternativas acima para acessar a interface."
fi

echo ""
echo "👋 Aplicação finalizada" 