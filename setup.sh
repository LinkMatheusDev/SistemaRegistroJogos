#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "            Setup de Dependências"
echo "==============================================="

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Instale Java 17+ primeiro."
    echo "   📥 Download: https://adoptium.net/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"

# Criar diretórios
mkdir -p lib build/classes

echo ""
echo "📦 Baixando todas as dependências..."

# Detectar plataforma para JavaFX
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case $OS in
    darwin*) OS_NAME="mac" ;;
    linux*) OS_NAME="linux" ;;
    mingw*|cygwin*|msys*) OS_NAME="win" ;;
    *) OS_NAME="linux" ;;
esac

case $ARCH in
    x86_64|amd64) ARCH_NAME="x64" ;;
    aarch64|arm64) ARCH_NAME="aarch64" ;;
    *) ARCH_NAME="x64" ;;
esac

JAVAFX_PLATFORM="${OS_NAME}-${ARCH_NAME}"
echo "🎯 Plataforma detectada: $JAVAFX_PLATFORM"

# Versões das dependências
JAVAFX_VERSION="21.0.1"
SQLITE_VERSION="3.42.0.0"
POSTGRESQL_VERSION="42.6.0"
SLF4J_VERSION="2.0.7"

# Função para baixar arquivo
download_dep() {
    local url=$1
    local file=$2
    local name=$3
    
    if [ ! -f "$file" ]; then
        echo "  📥 Baixando $name..."
        curl -L --progress-bar -o "$file" "$url" || {
            echo "❌ Erro ao baixar $name"
            return 1
        }
        echo "  ✅ $name baixado com sucesso"
    else
        echo "  ✅ $name já existe"
    fi
}

echo ""
echo "🗄️  Baixando drivers de banco de dados..."

# SQLite JDBC
download_dep \
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/${SQLITE_VERSION}/sqlite-jdbc-${SQLITE_VERSION}.jar" \
    "lib/sqlite-jdbc-${SQLITE_VERSION}.jar" \
    "SQLite JDBC"

# PostgreSQL JDBC
download_dep \
    "https://repo1.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar" \
    "lib/postgresql-${POSTGRESQL_VERSION}.jar" \
    "PostgreSQL JDBC"

echo ""
echo "📄 Baixando sistema de logs..."

# SLF4J API
download_dep \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/${SLF4J_VERSION}/slf4j-api-${SLF4J_VERSION}.jar" \
    "lib/slf4j-api-${SLF4J_VERSION}.jar" \
    "SLF4J API"

# SLF4J Simple
download_dep \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/${SLF4J_VERSION}/slf4j-simple-${SLF4J_VERSION}.jar" \
    "lib/slf4j-simple-${SLF4J_VERSION}.jar" \
    "SLF4J Simple"

echo ""
echo "🖥️  Baixando JavaFX ${JAVAFX_VERSION} para ${JAVAFX_PLATFORM}..."

# JavaFX Base
download_dep \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/${JAVAFX_VERSION}/javafx-base-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-base-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "JavaFX Base"

# JavaFX Controls
download_dep \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/${JAVAFX_VERSION}/javafx-controls-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-controls-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "JavaFX Controls"

# JavaFX FXML
download_dep \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/${JAVAFX_VERSION}/javafx-fxml-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-fxml-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "JavaFX FXML"

# JavaFX Graphics
download_dep \
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/${JAVAFX_VERSION}/javafx-graphics-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "lib/javafx-graphics-${JAVAFX_VERSION}-${JAVAFX_PLATFORM}.jar" \
    "JavaFX Graphics"

echo ""
echo "🔨 Compilando projeto..."

# Compilar uma vez
find src/main/java -name "*.java" -print0 | xargs -0 javac -cp "lib/*" -d build/classes

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso"
    
    # Copiar recursos
    if [ -d "src/main/resources" ]; then
        cp -r src/main/resources/* build/classes/ 2>/dev/null || true
    fi
else
    echo "❌ Falha na compilação"
    exit 1
fi

echo ""
echo "📊 Resumo das dependências baixadas:"
echo "────────────────────────────────────────"
ls -la lib/ | grep -E "\.(jar)$" | awk '{print "  📦", $9, "(" $5 " bytes)"}'

echo ""
echo "🎉 SETUP CONCLUÍDO COM SUCESSO!"
echo ""
echo "🚀 Agora você pode executar:"
echo "   1. 🖥️  Interface Console: ./run-console-postgres.sh"
echo "   2. 🎮 Interface JavaFX: ./run-javafx-fixed.sh"
echo "   3. 🔧 Backend SQLite: ./run-simple.sh"
echo "   4. 🐳 Docker Setup: ./docker-setup.sh"
echo ""
echo "💡 Todas as dependências estão em lib/ e são baixadas automaticamente!"
echo "👋 Divirta-se!" 