# 🎮 Sistema de Registro de Jogos v2.0

Sistema completo de gerenciamento de jogos desenvolvido em Java com múltiplas interfaces (JavaFX, Console, Web) e suporte a PostgreSQL e SQLite.

## ✨ Funcionalidades

- 📋 **CRUD Completo**: Criar, listar, atualizar e remover jogos
- 🔍 **Busca Avançada**: Pesquisa por nome, gênero, desenvolvedor
- 📊 **Estatísticas**: Preços médios, avaliações, relatórios
- 💾 **Duplo Backend**: PostgreSQL (Docker) + SQLite (local)
- 🖥️ **Múltiplas Interfaces**: JavaFX, Console, Web

## 🚀 Início Rápido

### 📦 **Instalação das Dependências**

> ⚠️ **Importante**: As dependências (JARs) não estão no Git para evitar arquivos grandes. Elas são baixadas automaticamente!

#### Opção 1: Setup Automático (Recomendado)
```bash
# 1. Clonar repositório
git clone <seu-repo>
cd SistemaRegistroJogos

# 2. Executar setup (baixa tudo automaticamente)
./setup.sh

# 3. Usar o sistema
./run-console-postgres.sh
```

#### Opção 2: Execução Direta (Download Automático)
```bash
# Os scripts baixam dependências automaticamente na primeira execução
./run-console-postgres.sh  # Baixa e executa
./run-javafx-fixed.sh      # Baixa JavaFX e executa
```

### Pré-requisitos
- Java 17+ (obrigatório)
- Docker (opcional, para PostgreSQL)
- macOS/Linux/Windows

## 🎯 Interfaces Disponíveis

### 1. 🖥️ Console + PostgreSQL (Recomendado)
```bash
./run-console-postgres.sh
```
- ✅ **Interface completa no terminal**
- ✅ **Backend PostgreSQL robusto**
- ✅ **Funciona em qualquer sistema**
- 📦 **Download automático**: SQLite + PostgreSQL drivers

### 2. 🌐 Interface Web (pgAdmin)
```bash
./docker-setup.sh  # Iniciar PostgreSQL
# Acesse: http://localhost:8080
# Login: admin@jogos.com / admin123
```
- ✅ **Interface web profissional**
- ✅ **Gerenciamento completo do banco**
- ✅ **Visualização de dados**

### 3. 🎮 JavaFX Desktop (Experimental macOS)
```bash
./run-javafx-fixed.sh
```
- ⚠️ **Pode ter problemas no macOS Apple Silicon**
- ✅ **Interface gráfica moderna**
- ✅ **Conecta ao PostgreSQL**
- 📦 **Download automático**: JavaFX 21 + drivers

### 4. 🔧 SQLite Local (Fallback)
```bash
./run-simple.sh
```
- ✅ **Backend local simples**
- ✅ **Sem dependências externas**
- 📦 **Download automático**: SQLite driver

## 💾 Gestão de Dependências

### Por que não há JARs no Git?
- **Problema**: JARs são arquivos grandes (20+ MB total)
- **Solução**: Download automático pelos scripts
- **Vantagem**: Repositório leve, sempre dependências atualizadas

### O que é baixado automaticamente?
```
lib/
├── sqlite-jdbc-3.42.0.0.jar         # SQLite driver
├── postgresql-42.6.0.jar            # PostgreSQL driver  
├── slf4j-api-2.0.7.jar              # Logging API
├── slf4j-simple-2.0.7.jar           # Logging implementação
├── javafx-base-21.0.1-*.jar         # JavaFX Core
├── javafx-controls-21.0.1-*.jar     # JavaFX Controles
├── javafx-fxml-21.0.1-*.jar         # JavaFX FXML
└── javafx-graphics-21.0.1-*.jar     # JavaFX Gráficos
```

### Script de Setup Dedicado
```bash
# Baixar todas as dependências de uma vez
./setup.sh

# O que faz:
# - Detecta sua plataforma (macOS/Linux/Windows)
# - Baixa todas as dependências necessárias
# - Compila o projeto
# - Testa se tudo está funcionando
```

## 🏗️ Arquitetura

### Backend
- **PostgreSQL**: Banco principal em Docker
- **SQLite**: Backup local automático
- **DAO Pattern**: Acesso aos dados
- **Singleton**: Gerenciamento de conexões

### Frontend
- **JavaFX**: Interface desktop moderna
- **Console**: Interface terminal interativa
- **pgAdmin**: Interface web profissional

### Estrutura
```
src/main/java/com/sistemaregistrojogos/
├── Main.java                 # Aplicação principal
├── database/
│   ├── DatabaseManager.java # Gerenciador inteligente
│   ├── JogoDAO.java         # Operações CRUD
│   └── PostgreSQLManager.java # Conexão PostgreSQL
├── model/
│   ├── Jogo.java            # Modelo de dados
│   └── enums/
├── view/
│   ├── MainController.java  # Controlador JavaFX
│   └── JogoFormController.java
└── util/
    └── AlertUtil.java       # Utilitários

lib/                         # 📦 Dependências (baixadas automaticamente)
├── .gitkeep                # Mantém diretório no Git
├── *.jar                   # JARs baixados pelos scripts

docker/
├── docker-compose.yml      # PostgreSQL + pgAdmin + Redis
├── database/init.sql       # Schema e dados iniciais
└── docker-setup.sh        # Gerenciamento Docker

scripts de execução/
├── setup.sh                # 📦 Baixar todas dependências
├── run-console-postgres.sh # Interface console
├── run-javafx-fixed.sh    # JavaFX otimizado
├── run-simple.sh          # SQLite local
└── run-macos.sh          # Diagnóstico macOS
```

## 🐳 Docker Setup

### Serviços Incluídos
- **PostgreSQL 15**: Banco principal
- **pgAdmin 4**: Interface web de administração
- **Redis**: Cache (opcional)

### Portas
- PostgreSQL: `localhost:5432`
- pgAdmin: `localhost:8080`
- Redis: `localhost:6379`

### Comandos Docker
```bash
# Iniciar todos os serviços
./docker-setup.sh

# Verificar status
docker ps

# Logs
docker-compose logs -f

# Parar serviços
docker-compose down
```

## 📊 Modelo de Dados

### Tabela: jogos
```sql
CREATE TABLE jogos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL CHECK(preco >= 0),
    genero VARCHAR(100),
    desenvolvedor VARCHAR(255),
    plataforma VARCHAR(100),
    ano_lancamento INTEGER CHECK(ano_lancamento >= 1970 AND ano_lancamento <= 2050),
    avaliacao DECIMAL(3,1) CHECK(avaliacao >= 0 AND avaliacao <= 10),
    descricao TEXT,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Solução de Problemas

### "Dependências não encontradas"
```bash
# Solução 1: Setup automático
./setup.sh

# Solução 2: Executar script direto (baixa automaticamente)
./run-console-postgres.sh

# Solução 3: Verificar se Java está instalado
java -version
```

### JavaFX não funciona no macOS
```bash
# Use a interface console (100% funcional)
./run-console-postgres.sh

# Ou interface web
./docker-setup.sh
```

### PostgreSQL não conecta
```bash
# Verificar Docker
docker ps

# Reiniciar PostgreSQL
docker-compose restart postgres

# Usar SQLite como fallback
./run-simple.sh
```

### Problemas de compilação
```bash
# Limpar e recompilar
rm -rf build/ lib/
./setup.sh
```

## 🎮 Exemplos de Uso

### Setup Inicial
```bash
$ git clone <seu-repo>
$ cd SistemaRegistroJogos
$ ./setup.sh

🎮==============================================
    Sistema de Registro de Jogos v2.0
            Setup de Dependências
===============================================
✅ Java 24 detectado
🎯 Plataforma detectada: mac-aarch64

📦 Baixando todas as dependências...
  📥 Baixando SQLite JDBC...
  ✅ SQLite JDBC baixado com sucesso
  📥 Baixando PostgreSQL JDBC...
  ✅ PostgreSQL JDBC baixado com sucesso
...
🎉 SETUP CONCLUÍDO COM SUCESSO!
```

### Interface Console
```bash
$ ./run-console-postgres.sh

📋 MENU PRINCIPAL:
   1. 📊 Listar todos os jogos
   2. ➕ Adicionar novo jogo
   3. 🔍 Buscar jogos
   4. 📈 Estatísticas
   5. 🗑️  Remover jogo
   6. 🌐 Abrir pgAdmin (Web)
   7. ❌ Sair

Escolha uma opção (1-7): 1

📊 LISTANDO JOGOS:
 id |      nome       |   preco   | genero | desenvolvedor  | nota
----+-----------------+-----------+--------+----------------+------
  1 | Cyberpunk 2077  | R$ 199.99 | RPG    | CD Projekt RED | 8.5/10
  2 | The Witcher 3   | R$ 89.99  | RPG    | CD Projekt RED | 9.8/10
```

### Conectar via psql
```bash
psql -h localhost -p 5432 -U jogos_user -d sistemaregistrojogos
```

## 📝 Licença

Este projeto é livre para uso educacional e pessoal.

---

## 🏆 Status do Projeto

- ✅ **Backend PostgreSQL**: 100% funcional
- ✅ **Interface Console**: 100% funcional  
- ✅ **Interface Web**: 100% funcional
- ⚠️ **JavaFX Desktop**: Funcional (problemas conhecidos no macOS)
- ✅ **SQLite Fallback**: 100% funcional
- ✅ **Docker Setup**: 100% funcional
- ✅ **Download Automático**: 100% funcional

## 💡 Para Novos Usuários

**Primeira vez clonando?**
```bash
# Comando único para ter tudo funcionando:
git clone <repo> && cd SistemaRegistroJogos && ./setup.sh
```

**Quer só testar rapidamente?**
```bash
# Executa direto (baixa dependências automaticamente):
./run-console-postgres.sh
```

**Sistema completo e pronto para produção!** 🎉
