#!/bin/bash

echo "🎮=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "     Interface Swing Standalone + SQLite"
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

# Baixar apenas SQLite (versão standalone)
download_if_missing \
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" \
    "lib/sqlite-jdbc-3.42.0.0.jar" \
    "SQLite JDBC"

echo "✅ Dependências verificadas"

echo ""
echo "🔨 Compilando interface Swing standalone..."

# Compilar SwingAppSQLite
javac -cp "lib/*" SwingAppSQLite.java

if [ $? -eq 0 ]; then
    echo "✅ Interface Swing standalone compilada com sucesso"
else
    echo "❌ Falha na compilação da interface Swing"
    exit 1
fi

echo ""
echo "🚀 Iniciando Interface Swing Standalone..."
echo "💡 Swing é nativo do Java - não precisa de dependências externas!"
echo "💾 Usando SQLite - banco local e leve"
echo "🔧 Versão standalone - não depende de outras classes"

# Executar SwingAppSQLite
java -cp ".:lib/*" SwingAppSQLite

echo ""
echo "👋 Interface Swing finalizada" 