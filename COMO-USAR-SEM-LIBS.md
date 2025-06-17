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
```bash
# Baixa TODAS as dependências de uma vez
./setup.sh

# O que o script faz:
# ✅ Detecta sua plataforma (macOS/Linux/Windows)
# ✅ Baixa JavaFX para sua arquitetura específica
# ✅ Baixa drivers de banco de dados
# ✅ Baixa sistema de logs
# ✅ Compila o projeto
# ✅ Testa se tudo funciona
```

### 🎯 Opção 2: Execução Direta
```bash
# Cada script baixa suas dependências automaticamente:

./run-console-postgres.sh    # ⬇️ SQLite + PostgreSQL
./run-javafx-fixed.sh       # ⬇️ JavaFX + drivers
./run-simple.sh            # ⬇️ SQLite + logs
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
├── javafx-base-21.0.1-mac-aarch64.jar    # JavaFX Core
├── javafx-controls-21.0.1-mac-aarch64.jar # JavaFX UI
├── javafx-fxml-21.0.1-mac-aarch64.jar     # JavaFX FXML
└── javafx-graphics-21.0.1-mac-aarch64.jar # JavaFX Gráficos
```

> **Nota**: Os JARs do JavaFX variam por plataforma:
> - `mac-aarch64` (macOS Apple Silicon)
> - `mac-x64` (macOS Intel)
> - `linux-x64` (Linux)
> - `win-x64` (Windows)

## 🔄 Como Funciona o Download?

### 1. Detecção Automática
```bash
# Scripts detectam automaticamente:
OS=$(uname -s)        # Darwin, Linux, MINGW
ARCH=$(uname -m)      # arm64, x86_64, amd64
```

### 2. Download Inteligente
```bash
# Só baixa se não existir
if [ ! -f "lib/arquivo.jar" ]; then
    curl -L -o "lib/arquivo.jar" "https://repo1.maven.org/..."
fi
```

### 3. Verificação
```bash
# Verifica se download foi bem-sucedido
if [ $? -eq 0 ]; then
    echo "✅ Dependência baixada"
else
    echo "❌ Erro no download"
    exit 1
fi
```

## 🛠️ Solução de Problemas

### ❌ "Dependências não encontradas"
```bash
# Solução 1: Execute setup
./setup.sh

# Solução 2: Execute qualquer script (download automático)
./run-console-postgres.sh

# Solução 3: Verifique conexão internet
ping google.com
```

### ❌ "curl: command not found"
```bash
# macOS: Instalar ferramentas de desenvolvimento
xcode-select --install

# Linux Ubuntu/Debian:
sudo apt update && sudo apt install curl

# Linux CentOS/RHEL:
sudo yum install curl
```

### ❌ Download interrompido
```bash
# Limpar downloads parciais
rm -rf lib/*.jar

# Tentar novamente
./setup.sh
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

### Usuário Novo
```bash
# Comando único para ter tudo funcionando:
git clone <repo> && cd SistemaRegistroJogos && ./setup.sh
```

### Desenvolvedor
```bash
# Setup + execução
./setup.sh && ./run-console-postgres.sh
```

### Usuário Rápido
```bash
# Execução direta (download automático)
./run-console-postgres.sh
```

## 💡 Dicas Avançadas

### Ver o que foi baixado
```bash
ls -la lib/
```

### Verificar tamanhos
```bash
du -h lib/
```

### Re-baixar tudo
```bash
rm -rf lib/
./setup.sh
```

### Baixar apenas JavaFX
```bash
./run-javafx-fixed.sh  # Para e baixa só JavaFX
```

## 🏆 Vantagens do Sistema

### ✅ Para Usuários
- **Sem configuração**: Tudo funciona automaticamente
- **Sempre atualizado**: Baixa versões mais recentes
- **Multiplataforma**: Detecta seu sistema automaticamente

### ✅ Para Desenvolvedores
- **Repositório limpo**: Sem arquivos grandes no Git
- **CI/CD friendly**: Builds automatizados funcionam
- **Versionamento**: Fácil alterar versões das dependências

### ✅ Para Distribuição
- **Clone rápido**: Repositório leve
- **Autocontido**: Usuário não precisa instalar nada manualmente
- **Confiável**: Downloads sempre dos repositórios oficiais Maven

---

## 🎉 Resumo Final

**Você NÃO precisa se preocupar com dependências!**

1. 📥 **Clone o projeto**
2. 🚀 **Execute qualquer script**
3. ⏰ **Aguarde o download automático**
4. ✅ **Use o sistema normalmente**

**É simples assim!** 🎮 