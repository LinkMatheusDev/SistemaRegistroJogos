# 🎮 Como Usar o Sistema SEM as Libs

## ❓ Problema: Cadê os JARs?

Quando você clonar este repositório, **NÃO** haverá arquivos `.jar` na pasta `lib/`. Isso é **PROPOSITAL**!

### Por que removemos os JARs do Git?
- 📦 **Tamanho**: JARs são arquivos grandes (20+ MB total)
- 🚫 **Limitação Git**: GitHub/Git têm limites para arquivos grandes
- 🔄 **Atualização**: Sempre baixamos as versões mais recentes
- 🌍 **Banda**: Clones mais rápidos

## ✅ Solução: Download Automático!

**TODOS os scripts baixam as dependências automaticamente!**

### 🚀 Opção 1: Setup Completo (Recomendado)

#### Linux/macOS:
```bash
# Baixa TODAS as dependências de uma vez
./setup.sh
```

#### Windows:
```cmd
:: Opção 1: Batch (funciona em qualquer Windows)
setup.bat

:: Opção 2: PowerShell (mais avançado, com barras de progresso)
powershell -ExecutionPolicy Bypass -File setup.ps1
```

### 🎯 Opção 2: Execução Direta

#### Linux/macOS:
```bash
./run-console-postgres.sh    # ⬇️ SQLite + PostgreSQL
./run-javafx-fixed.sh       # ⬇️ JavaFX + drivers
./run-simple.sh            # ⬇️ SQLite + logs
```

#### Windows:
```cmd
run-simple.bat             :: ⬇️ SQLite + logs
run-javafx.bat             :: ⬇️ JavaFX + drivers
docker-compose up -d       :: 🐳 PostgreSQL
```

## 📦 O que é baixado automaticamente?

### Para Interface Console
```
lib/
├── sqlite-jdbc-3.42.0.0.jar      # SQLite driver
└── postgresql-42.6.0.jar         # PostgreSQL driver
```

### Para Interface JavaFX
```
lib/
├── sqlite-jdbc-3.42.0.0.jar         # SQLite driver
├── postgresql-42.6.0.jar            # PostgreSQL driver
├── slf4j-api-2.0.7.jar              # Sistema de logs
├── slf4j-simple-2.0.7.jar           # Logs implementação
├── javafx-base-21.0.1-win-x64.jar   # JavaFX Core (Windows)
├── javafx-controls-21.0.1-win-x64.jar # JavaFX UI
├── javafx-fxml-21.0.1-win-x64.jar     # JavaFX FXML
└── javafx-graphics-21.0.1-win-x64.jar # JavaFX Gráficos
```

> **Nota**: Os JARs do JavaFX variam por plataforma:
> - `mac-aarch64` (macOS Apple Silicon)
> - `mac-x64` (macOS Intel)
> - `linux-x64` (Linux)
> - `win-x64` (Windows 64-bit)
> - `win-aarch64` (Windows ARM)

## 🔄 Como Funciona o Download?

### Linux/macOS (Bash):
```bash
# Scripts detectam automaticamente:
OS=$(uname -s)        # Darwin, Linux
ARCH=$(uname -m)      # arm64, x86_64, amd64

# Download com curl
curl -L -o "lib/arquivo.jar" "https://repo1.maven.org/..."
```

### Windows (Batch):
```batch
REM Detectar arquitetura
set ARCH=x64
if "%PROCESSOR_ARCHITECTURE%"=="ARM64" set ARCH=aarch64

REM Download com PowerShell
powershell -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%FILE%'"
```

### Windows (PowerShell):
```powershell
# Download com barra de progresso
$webClient = New-Object System.Net.WebClient
$webClient.DownloadFile($Url, $FilePath)
```

## 🛠️ Solução de Problemas

### ❌ "Dependências não encontradas"

#### Linux/macOS:
```bash
# Solução 1: Execute setup
./setup.sh

# Solução 2: Execute qualquer script (download automático)
./run-console-postgres.sh

# Solução 3: Verifique conexão internet
ping google.com
```

#### Windows:
```cmd
:: Solução 1: Execute setup
setup.bat

:: Solução 2: Execute qualquer script
run-simple.bat

:: Solução 3: Verificar conexão
ping google.com
```

### ❌ "curl: command not found" (Linux/macOS)
```bash
# macOS: Instalar ferramentas de desenvolvimento
xcode-select --install

# Linux Ubuntu/Debian:
sudo apt update && sudo apt install curl

# Linux CentOS/RHEL:
sudo yum install curl
```

### ❌ "PowerShell execution policy" (Windows)
```powershell
# Permitir execução de scripts PowerShell (uma vez só)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Ou executar diretamente:
powershell -ExecutionPolicy Bypass -File setup.ps1
```

### ❌ Download interrompido

#### Linux/macOS:
```bash
# Limpar downloads parciais
rm -rf lib/*.jar

# Tentar novamente
./setup.sh
```

#### Windows:
```cmd
:: Limpar downloads parciais
del lib\*.jar

:: Tentar novamente
setup.bat
```

## 📋 Lista de Dependências

### Core Java
- **SQLite JDBC 3.42.0.0**: Driver para banco SQLite
- **PostgreSQL JDBC 42.6.0**: Driver para PostgreSQL

### Sistema de Logs
- **SLF4J API 2.0.7**: Interface de logging
- **SLF4J Simple 2.0.7**: Implementação simples

### JavaFX 21.0.1 (por plataforma)
- **javafx-base**: Funcionalidades core
- **javafx-controls**: Botões, tabelas, etc.
- **javafx-fxml**: Carregamento de FXML
- **javafx-graphics**: Renderização gráfica

## 🎯 Primeira Execução

### Linux/macOS:
```bash
# Usuário Novo
git clone <repo> && cd SistemaRegistroJogos && ./setup.sh

# Desenvolvedor
./setup.sh && ./run-console-postgres.sh

# Usuário Rápido
./run-console-postgres.sh
```

### Windows:
```cmd
:: Usuário Novo
git clone <repo>
cd SistemaRegistroJogos
setup.bat

:: Desenvolvedor
setup.bat
run-simple.bat

:: Usuário Rápido
run-simple.bat
```

## 💡 Dicas Avançadas

### Ver o que foi baixado

#### Linux/macOS:
```bash
ls -la lib/
du -h lib/
```

#### Windows:
```cmd
dir lib\
dir lib\ /s
```

### Re-baixar tudo

#### Linux/macOS:
```bash
rm -rf lib/
./setup.sh
```

#### Windows:
```cmd
rmdir /s /q lib
setup.bat
```

### Scripts Específicos por Plataforma

#### Linux/macOS:
- `setup.sh` - Setup completo
- `run-console-postgres.sh` - Console + PostgreSQL
- `run-javafx-fixed.sh` - JavaFX Desktop
- `run-simple.sh` - SQLite local

#### Windows:
- `setup.bat` - Setup Batch simples
- `setup.ps1` - Setup PowerShell avançado
- `run-simple.bat` - SQLite local
- `run-javafx.bat` - JavaFX Desktop

## 🏆 Vantagens do Sistema

### ✅ Para Usuários
- **Sem configuração**: Tudo funciona automaticamente
- **Sempre atualizado**: Baixa versões mais recentes
- **Multiplataforma**: Detecta seu sistema automaticamente
- **Windows-friendly**: Scripts .bat funcionam em qualquer Windows

### ✅ Para Desenvolvedores
- **Repositório limpo**: Sem arquivos grandes no Git
- **CI/CD friendly**: Builds automatizados funcionam
- **Versionamento**: Fácil alterar versões das dependências
- **Cross-platform**: Mesmo código roda em Linux/macOS/Windows

### ✅ Para Distribuição
- **Clone rápido**: Repositório leve
- **Autocontido**: Usuário não precisa instalar nada manualmente
- **Confiável**: Downloads sempre dos repositórios oficiais Maven
- **Suporte Windows**: Funciona sem WSL ou ferramentas Linux

---

## 🎉 Resumo Final

**Você NÃO precisa se preocupar com dependências!**

### Linux/macOS:
1. 📥 **Clone o projeto**
2. 🚀 **Execute: `./setup.sh`**
3. ⏰ **Aguarde o download automático**
4. ✅ **Use: `./run-console-postgres.sh`**

### Windows:
1. 📥 **Clone o projeto**
2. 🚀 **Execute: `setup.bat`**
3. ⏰ **Aguarde o download automático**
4. ✅ **Use: `run-simple.bat`**

**É simples assim em qualquer sistema operacional!** 🎮 