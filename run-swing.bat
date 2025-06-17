@echo off
chcp 65001 >nul
echo.
echo 🎮==============================================
echo     Sistema de Registro de Jogos v2.0
echo           Interface Swing (Nativa)
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

echo 📦 Verificando dependências...

REM Baixar apenas SQLite (Swing não precisa de JavaFX)
call :download_if_missing ^
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar" ^
    "lib\sqlite-jdbc-3.42.0.0.jar" ^
    "SQLite JDBC"

echo ✅ Dependências verificadas

echo.
echo 🔨 Compilando interface Swing...

REM Compilar backend primeiro
javac -cp "lib\*" -d build\classes ^
    src\main\java\com\sistemaregistrojogos\model\*.java ^
    src\main\java\com\sistemaregistrojogos\database\*.java 2>nul

if errorlevel 1 (
    echo ❌ Falha na compilação do backend
    pause
    exit /b 1
) else (
    echo ✅ Backend compilado com sucesso
)

REM Compilar SwingApp
javac -cp "build\classes;lib\*" -d build\classes SwingApp.java 2>nul

if errorlevel 1 (
    echo ❌ Falha na compilação da interface Swing
    pause
    exit /b 1
) else (
    echo ✅ Interface Swing compilada com sucesso
)

echo.
echo 🚀 Iniciando Interface Swing...
echo 💡 Swing é nativo do Java - não precisa de dependências externas!

REM Executar SwingApp
cd build\classes
java -cp ".;..\..\lib\*" SwingApp
cd ..\..

echo.
echo 👋 Interface Swing finalizada
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