@echo off
chcp 65001 >nul
echo.
echo 🎮==============================================
echo     Sistema de Registro de Jogos v2.0
echo             Setup de Dependências
echo              (Windows Batch)
echo ===============================================

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java não encontrado. Instale Java 17+ primeiro.
    echo    📥 Download: https://adoptium.net/
    pause
    exit /b 1
)

echo ✅ Java detectado

REM Criar diretórios
if not exist "lib" mkdir lib
if not exist "build\classes" mkdir build\classes

echo.
echo 📦 Baixando todas as dependências...

REM Detectar arquitetura
set ARCH=x64
if "%PROCESSOR_ARCHITECTURE%"=="ARM64" set ARCH=aarch64

set JAVAFX_PLATFORM=win-%ARCH%
echo 🎯 Plataforma detectada: %JAVAFX_PLATFORM%

REM Versões das dependências
set JAVAFX_VERSION=21.0.1
set SQLITE_VERSION=3.42.0.0
set POSTGRESQL_VERSION=42.6.0
set SLF4J_VERSION=2.0.7

echo.
echo 🗄️  Baixando drivers de banco de dados...

REM SQLite JDBC
call :download_dep ^
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/%SQLITE_VERSION%/sqlite-jdbc-%SQLITE_VERSION%.jar" ^
    "lib\sqlite-jdbc-%SQLITE_VERSION%.jar" ^
    "SQLite JDBC"

REM PostgreSQL JDBC
call :download_dep ^
    "https://repo1.maven.org/maven2/org/postgresql/postgresql/%POSTGRESQL_VERSION%/postgresql-%POSTGRESQL_VERSION%.jar" ^
    "lib\postgresql-%POSTGRESQL_VERSION%.jar" ^
    "PostgreSQL JDBC"

echo.
echo 📄 Baixando sistema de logs...

REM SLF4J API
call :download_dep ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/%SLF4J_VERSION%/slf4j-api-%SLF4J_VERSION%.jar" ^
    "lib\slf4j-api-%SLF4J_VERSION%.jar" ^
    "SLF4J API"

REM SLF4J Simple
call :download_dep ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/%SLF4J_VERSION%/slf4j-simple-%SLF4J_VERSION%.jar" ^
    "lib\slf4j-simple-%SLF4J_VERSION%.jar" ^
    "SLF4J Simple"

echo.
echo 🖥️  Baixando JavaFX %JAVAFX_VERSION% para %JAVAFX_PLATFORM%...

REM JavaFX Base
call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/%JAVAFX_VERSION%/javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Base"

REM JavaFX Controls
call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/%JAVAFX_VERSION%/javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Controls"

REM JavaFX FXML
call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/%JAVAFX_VERSION%/javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX FXML"

REM JavaFX Graphics
call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/%JAVAFX_VERSION%/javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Graphics"

echo.
echo 🔨 Compilando projeto...

REM Compilar código
for /r src\main\java %%f in (*.java) do (
    set JAVA_FILES=!JAVA_FILES! "%%f"
)

javac -cp "lib\*" -d build\classes src\main\java\com\sistemaregistrojogos\*.java src\main\java\com\sistemaregistrojogos\model\*.java src\main\java\com\sistemaregistrojogos\database\*.java src\main\java\com\sistemaregistrojogos\view\*.java src\main\java\com\sistemaregistrojogos\util\*.java 2>nul

if errorlevel 1 (
    echo ❌ Falha na compilação
    pause
    exit /b 1
) else (
    echo ✅ Compilação concluída com sucesso
)

REM Copiar recursos
if exist "src\main\resources" (
    xcopy /s /y "src\main\resources\*" "build\classes\" >nul 2>&1
)

echo.
echo 📊 Resumo das dependências baixadas:
echo ────────────────────────────────────────
dir lib\*.jar /b 2>nul | findstr .jar

echo.
echo 🎉 SETUP CONCLUÍDO COM SUCESSO!
echo.
echo 🚀 Agora você pode executar:
echo    1. 🖥️  Interface Console: run-console-postgres.bat
echo    2. 🎮 Interface JavaFX: run-javafx.bat
echo    3. 🔧 Backend SQLite: run-simple.bat
echo    4. 🐳 Docker Setup: docker-compose up -d
echo.
echo 💡 Todas as dependências estão em lib\ e são baixadas automaticamente!
echo 👋 Divirta-se!
echo.
pause
goto :eof

:download_dep
set URL=%~1
set FILE=%~2
set NAME=%~3

if exist "%FILE%" (
    echo   ✅ %NAME% já existe
) else (
    echo   📥 Baixando %NAME%...
    powershell -Command "& {Invoke-WebRequest -Uri '%URL%' -OutFile '%FILE%' -UserAgent 'Mozilla/5.0'}" 2>nul
    if errorlevel 1 (
        echo   ❌ Erro ao baixar %NAME%
        exit /b 1
    ) else (
        echo   ✅ %NAME% baixado com sucesso
    )
)
goto :eof 