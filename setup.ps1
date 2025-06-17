# Sistema de Registro de Jogos v2.0 - Setup para Windows (PowerShell)
# Encoding: UTF-8

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host ""
Write-Host "🎮=============================================="
Write-Host "    Sistema de Registro de Jogos v2.0"
Write-Host "            Setup de Dependências"
Write-Host "            (Windows PowerShell)"
Write-Host "==============================================="

# Verificar se está sendo executado como administrador (opcional)
function Test-Administrator {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# Verificar Java
Write-Host "🔍 Verificando Java..."
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
    if ($javaVersion -match '"(\d+)\.') {
        $majorVersion = [int]$matches[1]
        if ($majorVersion -lt 17) {
            Write-Host "❌ Java 17+ necessário. Versão atual: $majorVersion" -ForegroundColor Red
            Write-Host "   📥 Download: https://adoptium.net/" -ForegroundColor Yellow
            Read-Host "Pressione Enter para sair"
            exit 1
        }
        Write-Host "✅ Java $majorVersion detectado" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Java não encontrado. Instale Java 17+ primeiro." -ForegroundColor Red
    Write-Host "   📥 Download: https://adoptium.net/" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Criar diretórios
Write-Host "📁 Criando diretórios..."
New-Item -ItemType Directory -Force -Path "lib" | Out-Null
New-Item -ItemType Directory -Force -Path "build\classes" | Out-Null

Write-Host ""
Write-Host "📦 Baixando todas as dependências..."

# Detectar arquitetura
$arch = if ($env:PROCESSOR_ARCHITECTURE -eq "ARM64") { "aarch64" } else { "x64" }
$javafxPlatform = "win-$arch"
Write-Host "🎯 Plataforma detectada: $javafxPlatform" -ForegroundColor Cyan

# Versões das dependências
$javafxVersion = "21.0.1"
$sqliteVersion = "3.42.0.0"
$postgresqlVersion = "42.6.0"
$slf4jVersion = "2.0.7"

# Função para baixar arquivo com barra de progresso
function Download-Dependency {
    param(
        [string]$Url,
        [string]$FilePath,
        [string]$Name
    )
    
    if (Test-Path $FilePath) {
        Write-Host "  ✅ $Name já existe" -ForegroundColor Green
        return $true
    }
    
    try {
        Write-Host "  📥 Baixando $Name..." -ForegroundColor Yellow
        
        # Download com barra de progresso
        $webClient = New-Object System.Net.WebClient
        $webClient.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        
        # Evento para mostrar progresso
        Register-ObjectEvent -InputObject $webClient -EventName DownloadProgressChanged -Action {
            $percent = $Event.SourceEventArgs.ProgressPercentage
            Write-Progress -Activity "Baixando $Name" -Status "$percent% completo" -PercentComplete $percent
        } | Out-Null
        
        $webClient.DownloadFile($Url, $FilePath)
        $webClient.Dispose()
        
        Write-Progress -Activity "Baixando $Name" -Completed
        Write-Host "  ✅ $Name baixado com sucesso" -ForegroundColor Green
        return $true
        
    } catch {
        Write-Host "  ❌ Erro ao baixar $Name`: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

Write-Host ""
Write-Host "🗄️  Baixando drivers de banco de dados..."

# SQLite JDBC
$success = Download-Dependency `
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/$sqliteVersion/sqlite-jdbc-$sqliteVersion.jar" `
    "lib\sqlite-jdbc-$sqliteVersion.jar" `
    "SQLite JDBC"

if (-not $success) { exit 1 }

# PostgreSQL JDBC
$success = Download-Dependency `
    "https://repo1.maven.org/maven2/org/postgresql/postgresql/$postgresqlVersion/postgresql-$postgresqlVersion.jar" `
    "lib\postgresql-$postgresqlVersion.jar" `
    "PostgreSQL JDBC"

if (-not $success) { exit 1 }

Write-Host ""
Write-Host "📄 Baixando sistema de logs..."

# SLF4J API
$success = Download-Dependency `
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/$slf4jVersion/slf4j-api-$slf4jVersion.jar" `
    "lib\slf4j-api-$slf4jVersion.jar" `
    "SLF4J API"

if (-not $success) { exit 1 }

# SLF4J Simple
$success = Download-Dependency `
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/$slf4jVersion/slf4j-simple-$slf4jVersion.jar" `
    "lib\slf4j-simple-$slf4jVersion.jar" `
    "SLF4J Simple"

if (-not $success) { exit 1 }

Write-Host ""
Write-Host "🖥️  Baixando JavaFX $javafxVersion para $javafxPlatform..."

# JavaFX Components
$javafxComponents = @(
    @{ Name = "JavaFX Base"; Component = "base" },
    @{ Name = "JavaFX Controls"; Component = "controls" },
    @{ Name = "JavaFX FXML"; Component = "fxml" },
    @{ Name = "JavaFX Graphics"; Component = "graphics" }
)

foreach ($component in $javafxComponents) {
    $success = Download-Dependency `
        "https://repo1.maven.org/maven2/org/openjfx/javafx-$($component.Component)/$javafxVersion/javafx-$($component.Component)-$javafxVersion-$javafxPlatform.jar" `
        "lib\javafx-$($component.Component)-$javafxVersion-$javafxPlatform.jar" `
        $component.Name
    
    if (-not $success) { exit 1 }
}

Write-Host ""
Write-Host "🔨 Compilando projeto..."

# Obter todos os arquivos Java
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    Write-Host "❌ Nenhum arquivo Java encontrado em src\main\java" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

try {
    # Compilar com classpath
    $classpath = "lib\*"
    $javaFilesString = $javaFiles -join " "
    
    $compileResult = Start-Process -FilePath "javac" -ArgumentList "-cp `"$classpath`" -d build\classes $javaFilesString" -NoNewWindow -Wait -PassThru
    
    if ($compileResult.ExitCode -eq 0) {
        Write-Host "✅ Compilação concluída com sucesso" -ForegroundColor Green
        
        # Copiar recursos se existirem
        if (Test-Path "src\main\resources") {
            Copy-Item -Path "src\main\resources\*" -Destination "build\classes" -Recurse -Force
            Write-Host "✅ Recursos copiados" -ForegroundColor Green
        }
    } else {
        Write-Host "❌ Falha na compilação" -ForegroundColor Red
        Read-Host "Pressione Enter para sair"
        exit 1
    }
} catch {
    Write-Host "❌ Erro durante a compilação: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""
Write-Host "📊 Resumo das dependências baixadas:"
Write-Host "────────────────────────────────────────"

$jarFiles = Get-ChildItem -Path "lib" -Filter "*.jar" | Sort-Object Name
foreach ($jar in $jarFiles) {
    $sizeKB = [math]::Round($jar.Length / 1KB, 1)
    Write-Host "  📦 $($jar.Name) ($sizeKB KB)" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "🎉 SETUP CONCLUÍDO COM SUCESSO!" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Agora você pode executar:"
Write-Host "   1. 🖥️  Interface Console: .\run-console-postgres.bat" -ForegroundColor Yellow
Write-Host "   2. 🎮 Interface JavaFX: .\run-javafx.bat" -ForegroundColor Yellow
Write-Host "   3. 🔧 Backend SQLite: .\run-simple.bat" -ForegroundColor Yellow
Write-Host "   4. 🐳 Docker Setup: docker-compose up -d" -ForegroundColor Yellow
Write-Host ""
Write-Host "💡 Todas as dependências estão em lib\ e são baixadas automaticamente!"
Write-Host "👋 Divirta-se!" -ForegroundColor Green
Write-Host ""
Read-Host "Pressione Enter para continuar" 