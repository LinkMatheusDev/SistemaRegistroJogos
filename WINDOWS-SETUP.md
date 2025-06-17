# 🎮 Sistema de Registro de Jogos - Windows Setup

## 🪟 Guia Específico para Windows

Este projeto funciona **perfeitamente no Windows** com scripts dedicados `.bat` e `.ps1`!

## ⚡ Início Rápido (Windows)

### 1️⃣ Pré-requisitos
- **Java 17+** (obrigatório)
  - Download: https://adoptium.net/
  - Verificar: `java -version`
- **Git** (para clonar)
  - Download: https://git-scm.com/
- **Docker Desktop** (opcional, para PostgreSQL)
  - Download: https://www.docker.com/products/docker-desktop/

### 2️⃣ Instalação Completa
```cmd
:: 1. Clonar projeto
git clone <seu-repo>
cd SistemaRegistroJogos

:: 2. Setup automático (escolha uma opção)

:: Opção A: Batch (simples, funciona sempre)
setup.bat

:: Opção B: PowerShell (avançado, com barras de progresso)
powershell -ExecutionPolicy Bypass -File setup.ps1

:: 3. Executar sistema
run-simple.bat
```

## 🚀 Scripts Disponíveis

### 🔧 Setup e Configuração

#### `setup.bat` - Setup Batch (Recomendado)
```cmd
setup.bat
```
- ✅ Funciona em **qualquer** Windows (7, 8, 10, 11)
- ✅ Não precisa de configurações especiais
- ✅ Download automático de dependências
- ✅ Compilação automática

#### `setup.ps1` - Setup PowerShell (Avançado)
```powershell
powershell -ExecutionPolicy Bypass -File setup.ps1
```
- ✅ Barras de progresso nos downloads
- ✅ Melhor tratamento de erros
- ✅ Cores e formatação avançada
- ⚠️ Pode precisar ajustar ExecutionPolicy

### 🎮 Execução da Aplicação

#### `run-simple.bat` - SQLite Local
```cmd
run-simple.bat
```
- ✅ **Mais confiável para Windows**
- ✅ Banco SQLite local
- ✅ Download automático de dependências
- ✅ Não precisa de Docker

#### `run-javafx.bat` - Interface Gráfica
```cmd
run-javafx.bat
```
- ✅ Interface JavaFX moderna
- ✅ Download automático do JavaFX
- ✅ Otimizado para Windows
- ⚠️ Pode ter problemas em sistemas muito antigos

## 🔧 Resolução de Problemas Windows

### ❌ "Java não encontrado"
```cmd
:: Verificar se Java está instalado
java -version

:: Se não estiver, baixar e instalar:
:: https://adoptium.net/
```

### ❌ "PowerShell execution policy"
```powershell
:: Opção 1: Permitir scripts do usuário atual
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

:: Opção 2: Executar diretamente (recomendado)
powershell -ExecutionPolicy Bypass -File setup.ps1
```

### ❌ "Sistema não consegue encontrar o caminho especificado"
```cmd
:: Verificar se está no diretório correto
dir

:: Deve mostrar arquivos como: setup.bat, src/, etc.
:: Se não, navegar para o diretório correto:
cd SistemaRegistroJogos
```

### ❌ Downloads falham
```cmd
:: Verificar conexão com internet
ping google.com

:: Tentar novamente:
setup.bat
```

### ❌ Erro de compilação
```cmd
:: Limpar arquivos anteriores
rmdir /s /q build
rmdir /s /q lib

:: Tentar setup novamente
setup.bat
```

## 📁 O que cada script faz?

### `setup.bat`
1. ✅ Verifica Java 17+
2. ✅ Detecta arquitetura (x64/ARM64)
3. ✅ Cria diretórios necessários
4. ✅ Baixa SQLite JDBC (~3.4 MB)
5. ✅ Baixa PostgreSQL JDBC (~1.1 MB)
6. ✅ Baixa SLF4J Logging (~0.3 MB)
7. ✅ Baixa JavaFX completo (~15 MB)
8. ✅ Compila todo o projeto
9. ✅ Mostra resumo final

### `run-simple.bat`
1. ✅ Verifica dependências básicas
2. ✅ Baixa drivers se necessário
3. ✅ Compila backend (sem JavaFX)
4. ✅ Executa teste do SQLite
5. ✅ Mostra resultados

### `run-javafx.bat`
1. ✅ Verifica dependências JavaFX
2. ✅ Baixa JavaFX específico para Windows
3. ✅ Compila interface completa
4. ✅ Executa aplicação JavaFX

## 🎯 Recomendações Windows

### ✅ **Para Usuários Iniciantes**
```cmd
:: Comando único e simples:
setup.bat
run-simple.bat
```

### ✅ **Para Desenvolvedores**
```cmd
:: Setup completo + interface gráfica:
setup.bat
run-javafx.bat
```

### ✅ **Para Servidores/Produção**
```cmd
:: PostgreSQL com Docker:
docker-compose up -d
:: Acesse pgAdmin: http://localhost:8080
```

## 🔍 Verificação de Funcionamento

### Teste 1: Java
```cmd
C:\> java -version
openjdk version "17.0.7" 2023-04-18
OpenJDK Runtime Environment Temurin-17.0.7+7 (build 17.0.7+7)
OpenJDK 64-Bit Server VM Temurin-17.0.7+7 (build 17.0.7+7, mixed mode, sharing)
```

### Teste 2: Setup
```cmd
C:\> setup.bat
🎮==============================================
    Sistema de Registro de Jogos v2.0
             Setup de Dependências
              (Windows Batch)
===============================================
✅ Java detectado
🎯 Plataforma detectada: win-x64
...
🎉 SETUP CONCLUÍDO COM SUCESSO!
```

### Teste 3: Execução
```cmd
C:\> run-simple.bat
===============================================
    Sistema de Registro de Jogos v2.0
          (Modo Simplificado)
===============================================
✅ Java detectado
...
🎉 TESTE CONCLUÍDO COM SUCESSO!
```

## 📊 Comparação de Opções

| Opção | Complexidade | Confiabilidade | Recursos |
|-------|-------------|---------------|----------|
| `setup.bat` | ⭐ Simples | ⭐⭐⭐ Alta | ⭐⭐ Básico |
| `setup.ps1` | ⭐⭐ Médio | ⭐⭐⭐ Alta | ⭐⭐⭐ Avançado |
| `run-simple.bat` | ⭐ Simples | ⭐⭐⭐ Alta | ⭐⭐ Console |
| `run-javafx.bat` | ⭐⭐ Médio | ⭐⭐ Média | ⭐⭐⭐ Interface |

## 🎉 Windows vs Linux/macOS

### ✅ **Vantagens Windows**
- Scripts `.bat` nativos do sistema
- Não precisa instalar ferramentas extras
- PowerShell disponível por padrão
- Funciona desde Windows 7

### ⚠️ **Limitações Windows**
- Interface console PostgreSQL ainda não disponível
- Docker Desktop é mais pesado que Docker nativo
- PowerShell pode ter restrições de execução

### 💡 **Recomendação Final**
Para Windows, use:
1. **`setup.bat`** - Para configurar tudo
2. **`run-simple.bat`** - Para usar o sistema (SQLite)
3. **Docker + pgAdmin** - Para PostgreSQL avançado

**O sistema funciona 100% no Windows!** 🎮🪟 