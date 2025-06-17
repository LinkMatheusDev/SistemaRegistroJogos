@echo off
chcp 65001 >nul
echo.
echo 🎮=============================================="
echo     Sistema de Registro de Jogos v2.0
echo              Interface JavaFX
echo               (Windows)
echo ===============================================

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java não encontrado. Instale Java 17+ primeiro.
    pause
    exit /b 1
)

echo ✅ Java detectado

REM Detectar arquitetura
set ARCH=x64
if "%PROCESSOR_ARCHITECTURE%"=="ARM64" set ARCH=aarch64
set JAVAFX_PLATFORM=win-%ARCH%

echo 🎯 Plataforma: %JAVAFX_PLATFORM%

REM Criar diretórios
if not exist "lib" mkdir lib
if not exist "build\classes" mkdir build\classes

echo 📦 Verificando dependências JavaFX...

REM Versões
set JAVAFX_VERSION=21.0.1
set SQLITE_VERSION=3.42.0.0
set POSTGRESQL_VERSION=42.6.0
set SLF4J_VERSION=2.0.7

REM Baixar drivers básicos
call :download_dep ^
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/%SQLITE_VERSION%/sqlite-jdbc-%SQLITE_VERSION%.jar" ^
    "lib\sqlite-jdbc-%SQLITE_VERSION%.jar" ^
    "SQLite JDBC"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/postgresql/postgresql/%POSTGRESQL_VERSION%/postgresql-%POSTGRESQL_VERSION%.jar" ^
    "lib\postgresql-%POSTGRESQL_VERSION%.jar" ^
    "PostgreSQL JDBC"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/%SLF4J_VERSION%/slf4j-api-%SLF4J_VERSION%.jar" ^
    "lib\slf4j-api-%SLF4J_VERSION%.jar" ^
    "SLF4J API"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/%SLF4J_VERSION%/slf4j-simple-%SLF4J_VERSION%.jar" ^
    "lib\slf4j-simple-%SLF4J_VERSION%.jar" ^
    "SLF4J Simple"

echo 🖥️  Baixando JavaFX %JAVAFX_VERSION%...

REM JavaFX Components
call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-base/%JAVAFX_VERSION%/javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-base-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Base"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/%JAVAFX_VERSION%/javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-controls-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Controls"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/%JAVAFX_VERSION%/javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-fxml-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX FXML"

call :download_dep ^
    "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/%JAVAFX_VERSION%/javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "lib\javafx-graphics-%JAVAFX_VERSION%-%JAVAFX_PLATFORM%.jar" ^
    "JavaFX Graphics"

echo.
echo 🔨 Compilando projeto...

REM Compilar tudo
javac -cp "lib\*" -d build\classes ^
    src\main\java\com\sistemaregistrojogos\*.java ^
    src\main\java\com\sistemaregistrojogos\model\*.java ^
    src\main\java\com\sistemaregistrojogos\database\*.java ^
    src\main\java\com\sistemaregistrojogos\view\*.java ^
    src\main\java\com\sistemaregistrojogos\util\*.java 2>nul

if errorlevel 1 (
    echo ❌ Falha na compilação
    pause
    exit /b 1
) else (
    echo ✅ Compilação concluída
)

REM Copiar recursos FXML
if exist "src\main\resources" (
    xcopy /s /y "src\main\resources\*" "build\classes\" >nul 2>&1
)

echo.
echo 🚀 Iniciando JavaFX...

REM Argumentos JVM para Windows
set JVM_ARGS=-Djavafx.embed.isEventThread=false
set JVM_ARGS=%JVM_ARGS% -Dprism.order=sw
set JVM_ARGS=%JVM_ARGS% -Dprism.verbose=true
set JVM_ARGS=%JVM_ARGS% --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
set JVM_ARGS=%JVM_ARGS% --add-exports javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED

REM Executar aplicação
cd build\classes
java %JVM_ARGS% -cp ".;..\..\lib\*" com.sistemaregistrojogos.Main

cd ..\..

echo.
echo 👋 JavaFX finalizado
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
    powershell -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%FILE%'" 2>nul
    if errorlevel 1 (
        echo   ❌ Erro ao baixar %NAME%
        exit /b 1
    ) else (
        echo   ✅ %NAME% baixado
    )
)
goto :eof 