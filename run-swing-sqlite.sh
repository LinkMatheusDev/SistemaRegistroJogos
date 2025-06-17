#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "        Interface Swing + SQLite"
echo "==============================================="

# Verificar Java
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✅ Java $JAVA_VERSION detectado"

# Verificar dependências e baixar se necessário
echo "📦 Verificando dependências..."
mkdir -p lib

# Função para baixar dependência
download_if_missing() {
    local url=$1
    local file=$2
    local name=$3
    
    if [ ! -f "$file" ]; then
        echo "  📥 Baixando $name..."
        curl -L --progress-bar -o "$file" "$url" || {
            echo "❌ Erro ao baixar $name"
            return 1
        }
        echo "  ✅ $name baixado"
    fi
}

# Baixar apenas SQLite (Swing não precisa de JavaFX)
download_if_missing \
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" \
    "lib/sqlite-jdbc-3.42.0.0.jar" \
    "SQLite JDBC"

download_if_missing \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar" \
    "lib/slf4j-api-2.0.7.jar" \
    "SLF4J API"

download_if_missing \
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.7/slf4j-simple-2.0.7.jar" \
    "lib/slf4j-simple-2.0.7.jar" \
    "SLF4J Simple"

echo "✅ Dependências verificadas"

echo ""
echo "🔨 Compilando interface Swing..."

# Criar diretórios
mkdir -p build/classes

# Compilar backend primeiro
find src/main/java -name "*.java" -print0 | xargs -0 javac -cp "lib/*" -d build/classes

if [ $? -eq 0 ]; then
    echo "✅ Backend compilado com sucesso"
else
    echo "❌ Falha na compilação do backend"
    exit 1
fi

# Compilar SwingApp
javac -cp "build/classes:lib/*" -d build/classes SwingApp.java

if [ $? -eq 0 ]; then
    echo "✅ Interface Swing compilada com sucesso"
else
    echo "❌ Falha na compilação da interface Swing"
    exit 1
fi

echo ""
echo "🚀 Iniciando Interface Swing com SQLite..."
echo "💡 Swing é nativo do Java - não precisa de dependências externas!"
echo "💾 Usando SQLite - banco local e leve"

# Configurar para usar SQLite
export DB_TYPE=sqlite

# Executar SwingApp
cd build/classes
java -cp ".:../../lib/*" -Ddb.type=sqlite SwingApp

echo ""
echo "👋 Interface Swing finalizada" 