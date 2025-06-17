#!/bin/bash

echo "🍎=============================================="
echo "    Sistema de Registro de Jogos v2.0"
echo "          Versão Especial macOS"
echo "==============================================="

# Verificar se estamos no macOS
if [[ "$OSTYPE" != "darwin"* ]]; then
    echo "❌ Este script é apenas para macOS. Use ./run.sh"
    exit 1
fi

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado."
    echo "📦 Instalar via Homebrew:"
    echo "   brew install --cask temurin17"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ necessário. Atual: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detectado"

# Criar estrutura
mkdir -p build/classes lib

# Compilar se necessário
if [ ! -d "build/classes/com" ] || [ "src/main/java/com/sistemaregistrojogos/Main.java" -nt "build/classes/com/sistemaregistrojogos/Main.class" ]; then
    echo "🔨 Compilando projeto..."
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
fi

echo ""
echo "🚀 Tentativas de execução para macOS:"
echo ""

cd build/classes

# Tentativa 1: Versão simplificada sem JavaFX
echo "1️⃣ Testando backend SQLite (sem interface)..."
if java -cp ".:../../lib/*" com.sistemaregistrojogos.database.DatabaseManager 2>/dev/null; then
    echo "✅ Backend SQLite funcionando!"
else
    echo "❌ Problema no backend"
fi

echo ""

# Tentativa 2: JavaFX com configuração mínima
echo "2️⃣ Tentando JavaFX com configuração mínima..."
MINIMAL_ARGS="-Dprism.order=sw -Dprism.text=t2k -Djava.awt.headless=false"
java $MINIMAL_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main &
PID=$!
sleep 5

if kill -0 $PID 2>/dev/null; then
    echo "✅ JavaFX iniciou com sucesso!"
    echo "   Aguardando execução..."
    wait $PID
else
    echo "❌ JavaFX falhou com configuração mínima"
fi

echo ""

# Tentativa 3: Software rendering completo
echo "3️⃣ Tentando software rendering completo..."
SW_ARGS="-Dprism.order=sw -Dprism.text=t2k -Dprism.verbose=false"
SW_ARGS="$SW_ARGS -Djava.awt.headless=false -Dapple.awt.UIElement=false"
SW_ARGS="$SW_ARGS -Djavafx.animation.pulse=60"

java $SW_ARGS -cp ".:../../lib/*" com.sistemaregistrojogos.Main &
PID=$!
sleep 5

if kill -0 $PID 2>/dev/null; then
    echo "✅ Software rendering funcionou!"
    wait $PID
else
    echo "❌ Software rendering também falhou"
fi

echo ""
echo "🔍 DIAGNÓSTICO COMPLETO:"
echo ""

# Verificar dependências
echo "📋 Verificando dependências:"
for jar in ../../lib/*.jar; do
    if [ -f "$jar" ]; then
        echo "  ✅ $(basename $jar)"
    fi
done

echo ""
echo "💻 Informações do sistema:"
echo "  OS: $(uname -s) $(uname -r)"
echo "  Arch: $(uname -m)"
echo "  Java: $(java -version 2>&1 | head -n 1)"

echo ""
echo "🛠️  SOLUÇÕES RECOMENDADAS:"
echo ""
echo "1. 🎯 BACKEND FUNCIONAL (Recomendado):"
echo "   ./run-simple.sh"
echo ""
echo "2. 🐳 INTERFACE WEB (PostgreSQL + pgAdmin):"
echo "   ./docker-setup.sh"
echo "   Acesse: http://localhost:8080"
echo ""
echo "3. ☕ JAVA OFICIAL ORACLE:"
echo "   Download: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html"
echo ""
echo "4. 🍺 HOMEBREW COMPLETO:"
echo "   brew install --cask temurin17"
echo "   brew install openjfx"
echo ""
echo "5. 🧪 TESTAR SEM GUI:"
echo "   cd build/classes"
echo "   java -cp '.:../../lib/*' com.sistemaregistrojogos.ConsoleTest"

echo ""
echo "👋 Diagnóstico concluído" 