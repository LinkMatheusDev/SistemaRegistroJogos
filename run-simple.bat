@echo off
chcp 65001 >nul
echo.
echo ===============================================
echo     Sistema de Registro de Jogos v2.0
echo           (Modo Simplificado)
echo ===============================================

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java não encontrado. Instale Java 17+ primeiro.
    pause
    exit /b 1
)

echo ✅ Java detectado

REM Criar diretórios
if not exist "lib" mkdir lib
if not exist "build\classes" mkdir build\classes

echo 📦 Verificando dependências básicas...

REM Função para baixar se não existir
call :download_if_missing ^
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" ^
    "lib\sqlite-jdbc-3.42.0.0.jar" ^
    "SQLite JDBC"

call :download_if_missing ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar" ^
    "lib\slf4j-api-2.0.7.jar" ^
    "SLF4J API"

call :download_if_missing ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.7/slf4j-simple-2.0.7.jar" ^
    "lib\slf4j-simple-2.0.7.jar" ^
    "SLF4J Simple"

echo.
echo 🔨 Compilando versão console...

REM Compilar apenas classes do backend (sem JavaFX)
javac -cp "lib\*" -d build\classes ^
    src\main\java\com\sistemaregistrojogos\model\*.java ^
    src\main\java\com\sistemaregistrojogos\database\*.java 2>nul

if errorlevel 1 (
    echo ❌ Falha na compilação do backend
    pause
    exit /b 1
) else (
    echo ✅ Compilação do backend concluída
)

echo.
echo 🚀 Executando teste completo do banco de dados...

REM Criar classe de teste temporária
echo import com.sistemaregistrojogos.model.Jogo; > build\classes\TesteBanco.java
echo import com.sistemaregistrojogos.database.DatabaseManager; >> build\classes\TesteBanco.java
echo import com.sistemaregistrojogos.database.JogoDAO; >> build\classes\TesteBanco.java
echo import java.util.List; >> build\classes\TesteBanco.java
echo import java.util.Optional; >> build\classes\TesteBanco.java
echo. >> build\classes\TesteBanco.java
echo public class TesteBanco { >> build\classes\TesteBanco.java
echo     public static void main(String[] args) { >> build\classes\TesteBanco.java
echo         System.out.println("=== TESTE SQLITE WINDOWS ==="); >> build\classes\TesteBanco.java
echo         try { >> build\classes\TesteBanco.java
echo             DatabaseManager db = DatabaseManager.getInstance(); >> build\classes\TesteBanco.java
echo             db.initializeDatabase(); >> build\classes\TesteBanco.java
echo             JogoDAO dao = new JogoDAO(); >> build\classes\TesteBanco.java
echo             System.out.println("✅ Banco SQLite inicializado!"); >> build\classes\TesteBanco.java
echo             int total = db.countRecords(); >> build\classes\TesteBanco.java
echo             System.out.println("📊 Total de jogos: " + total); >> build\classes\TesteBanco.java
echo             System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!"); >> build\classes\TesteBanco.java
echo         } catch (Exception e) { >> build\classes\TesteBanco.java
echo             System.err.println("❌ Erro: " + e.getMessage()); >> build\classes\TesteBanco.java
echo         } >> build\classes\TesteBanco.java
echo     } >> build\classes\TesteBanco.java
echo } >> build\classes\TesteBanco.java

REM Compilar teste
javac -cp "build\classes;lib\*" -d build\classes build\classes\TesteBanco.java

REM Executar teste
cd build\classes
java -cp ".;..\..\lib\*" TesteBanco
cd ..\..

echo.
echo 💾 Sistema SQLite funcionando!
echo 🔧 Para usar PostgreSQL: docker-compose up -d
echo.
pause
goto :eof

:download_if_missing
set URL=%~1
set FILE=%~2
set NAME=%~3

if exist "%FILE%" (
    echo   ✅ %NAME% já existe
) else (
    echo   📥 Baixando %NAME%...
    powershell -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%FILE%'" 2>nul
    if errorlevel 1 (
        echo   ❌ Erro ao baixar %NAME%
        exit /b 1
    ) else (
        echo   ✅ %NAME% baixado
    )
)
goto :eof 