-- Inicialização do banco PostgreSQL para Sistema de Registro de Jogos
-- Created: $(date)

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Criar schema principal
CREATE SCHEMA IF NOT EXISTS jogos_app;

-- Usar o schema
SET search_path TO jogos_app, public;

-- Tabela principal de jogos
CREATE TABLE IF NOT EXISTS jogos (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
    genero VARCHAR(100),
    desenvolvedora VARCHAR(200),
    plataforma VARCHAR(100),
    ano_lancamento INTEGER CHECK (ano_lancamento >= 1970 AND ano_lancamento <= 2050),
    classificacao DECIMAL(3,1) CHECK (classificacao >= 0 AND classificacao <= 10),
    descricao TEXT,
    data_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT true,
    
    -- Índices para performance
    CONSTRAINT jogos_nome_check CHECK (LENGTH(TRIM(nome)) > 0),
    CONSTRAINT jogos_preco_check CHECK (preco >= 0)
);

-- Tabela de categorias/gêneros (normalizada)
CREATE TABLE IF NOT EXISTS generos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    cor_hex VARCHAR(7) DEFAULT '#007bff',
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de desenvolvedoras
CREATE TABLE IF NOT EXISTS desenvolvedoras (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL UNIQUE,
    pais VARCHAR(100),
    website VARCHAR(500),
    fundacao INTEGER,
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de plataformas
CREATE TABLE IF NOT EXISTS plataformas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    fabricante VARCHAR(100),
    tipo VARCHAR(50), -- console, pc, mobile, etc
    lancamento INTEGER,
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimização
CREATE INDEX IF NOT EXISTS idx_jogos_nome ON jogos USING gin(nome gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_jogos_genero ON jogos(genero);
CREATE INDEX IF NOT EXISTS idx_jogos_desenvolvedora ON jogos(desenvolvedora);
CREATE INDEX IF NOT EXISTS idx_jogos_plataforma ON jogos(plataforma);
CREATE INDEX IF NOT EXISTS idx_jogos_preco ON jogos(preco);
CREATE INDEX IF NOT EXISTS idx_jogos_classificacao ON jogos(classificacao);
CREATE INDEX IF NOT EXISTS idx_jogos_data_cadastro ON jogos(data_cadastro);
CREATE INDEX IF NOT EXISTS idx_jogos_ativo ON jogos(ativo);

-- Função para atualizar data_atualizacao automaticamente
CREATE OR REPLACE FUNCTION update_data_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para atualizar data_atualizacao
DROP TRIGGER IF EXISTS trigger_update_jogos_data_atualizacao ON jogos;
CREATE TRIGGER trigger_update_jogos_data_atualizacao
    BEFORE UPDATE ON jogos
    FOR EACH ROW
    EXECUTE FUNCTION update_data_atualizacao();

-- Inserir dados iniciais de gêneros
INSERT INTO generos (nome, descricao, cor_hex) VALUES
    ('Ação', 'Jogos com foco em combate e reflexos', '#dc3545'),
    ('Aventura', 'Jogos com exploração e narrativa', '#28a745'),
    ('RPG', 'Role-playing games com progressão de personagem', '#6f42c1'),
    ('Estratégia', 'Jogos que requerem planejamento tático', '#fd7e14'),
    ('Simulação', 'Simuladores de atividades reais', '#20c997'),
    ('Esporte', 'Jogos baseados em esportes', '#17a2b8'),
    ('Corrida', 'Jogos de corrida e veículos', '#ffc107'),
    ('Puzzle', 'Jogos de quebra-cabeça e lógica', '#e83e8c'),
    ('FPS', 'First-person shooters', '#6c757d'),
    ('MMORPG', 'Massively multiplayer online RPG', '#343a40'),
    ('Indie', 'Jogos independentes', '#007bff'),
    ('Terror', 'Jogos de horror e suspense', '#000000'),
    ('Plataforma', 'Jogos de saltos e plataformas', '#198754'),
    ('Musical', 'Jogos baseados em música e ritmo', '#d63384'),
    ('Educativo', 'Jogos com propósito educacional', '#0dcaf0'),
    ('Casual', 'Jogos casuais e acessíveis', '#adb5bd')
ON CONFLICT (nome) DO NOTHING;

-- Inserir dados iniciais de desenvolvedoras
INSERT INTO desenvolvedoras (nome, pais, website) VALUES
    ('Nintendo', 'Japão', 'https://www.nintendo.com'),
    ('Sony Santa Monica', 'Estados Unidos', 'https://sms.playstation.com'),
    ('Mojang Studios', 'Suécia', 'https://www.mojang.com'),
    ('CD Projekt Red', 'Polônia', 'https://www.cdprojektred.com'),
    ('Valve Corporation', 'Estados Unidos', 'https://www.valvesoftware.com'),
    ('Rockstar Games', 'Estados Unidos', 'https://www.rockstargames.com'),
    ('Blizzard Entertainment', 'Estados Unidos', 'https://www.blizzard.com'),
    ('Epic Games', 'Estados Unidos', 'https://www.epicgames.com'),
    ('Ubisoft', 'França', 'https://www.ubisoft.com'),
    ('Electronic Arts', 'Estados Unidos', 'https://www.ea.com')
ON CONFLICT (nome) DO NOTHING;

-- Inserir dados iniciais de plataformas
INSERT INTO plataformas (nome, fabricante, tipo, lancamento) VALUES
    ('PC', 'Vários', 'pc', 1981),
    ('PlayStation 5', 'Sony', 'console', 2020),
    ('PlayStation 4', 'Sony', 'console', 2013),
    ('Xbox Series X/S', 'Microsoft', 'console', 2020),
    ('Xbox One', 'Microsoft', 'console', 2013),
    ('Nintendo Switch', 'Nintendo', 'console', 2017),
    ('Mobile', 'Vários', 'mobile', 2007),
    ('Linux', 'Vários', 'pc', 1991),
    ('Mac', 'Apple', 'pc', 1984),
    ('Steam Deck', 'Valve', 'handheld', 2022),
    ('Web', 'Vários', 'web', 1990)
ON CONFLICT (nome) DO NOTHING;

-- Inserir alguns jogos de exemplo
INSERT INTO jogos (nome, preco, genero, desenvolvedora, plataforma, ano_lancamento, classificacao, descricao) VALUES
    ('The Legend of Zelda: Breath of the Wild', 299.99, 'Aventura', 'Nintendo', 'Nintendo Switch', 2017, 9.7, 'Uma aventura épica em mundo aberto em Hyrule'),
    ('God of War', 179.99, 'Ação', 'Sony Santa Monica', 'PlayStation 4', 2018, 9.5, 'Kratos e Atreus em uma jornada nórdica emocionante'),
    ('Minecraft', 89.99, 'Simulação', 'Mojang Studios', 'PC', 2011, 9.0, 'Construa e explore mundos infinitos de blocos'),
    ('The Witcher 3: Wild Hunt', 149.99, 'RPG', 'CD Projekt Red', 'PC', 2015, 9.8, 'Uma obra-prima de RPG com narrativa incrível'),
    ('Half-Life: Alyx', 199.99, 'FPS', 'Valve Corporation', 'PC', 2020, 9.3, 'Revolucionário jogo de VR na série Half-Life')
ON CONFLICT (nome) DO NOTHING;

-- Criar view para relatórios
CREATE OR REPLACE VIEW vw_jogos_completo AS
SELECT 
    j.id,
    j.uuid,
    j.nome,
    j.preco,
    j.genero,
    j.desenvolvedora,
    j.plataforma,
    j.ano_lancamento,
    j.classificacao,
    j.descricao,
    j.data_cadastro,
    j.data_atualizacao,
    j.ativo,
    CASE 
        WHEN j.classificacao >= 9.0 THEN 'Excelente'
        WHEN j.classificacao >= 8.0 THEN 'Muito Bom'
        WHEN j.classificacao >= 7.0 THEN 'Bom'
        WHEN j.classificacao >= 6.0 THEN 'Regular'
        ELSE 'Fraco'
    END AS categoria_classificacao,
    CASE 
        WHEN EXTRACT(YEAR FROM CURRENT_DATE) - j.ano_lancamento <= 1 THEN 'Lançamento'
        WHEN EXTRACT(YEAR FROM CURRENT_DATE) - j.ano_lancamento <= 3 THEN 'Recente'
        WHEN EXTRACT(YEAR FROM CURRENT_DATE) - j.ano_lancamento <= 10 THEN 'Clássico Moderno'
        ELSE 'Clássico'
    END AS categoria_idade
FROM jogos j
WHERE j.ativo = true;

-- Comentários nas tabelas
COMMENT ON TABLE jogos IS 'Tabela principal com informações dos jogos';
COMMENT ON TABLE generos IS 'Categorias/gêneros de jogos normalizados';
COMMENT ON TABLE desenvolvedoras IS 'Empresas desenvolvedoras de jogos';
COMMENT ON TABLE plataformas IS 'Plataformas onde os jogos são executados';

-- Garantir permissões para o usuário
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA jogos_app TO jogos_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA jogos_app TO jogos_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA jogos_app TO jogos_user;

-- Configurações de otimização
ALTER DATABASE sistemaregistrojogos SET work_mem = '16MB';
ALTER DATABASE sistemaregistrojogos SET effective_cache_size = '256MB';
ALTER DATABASE sistemaregistrojogos SET random_page_cost = 1.1;

-- Log da inicialização
DO $$
BEGIN
    RAISE NOTICE 'Banco de dados Sistema de Registro de Jogos inicializado com sucesso!';
    RAISE NOTICE 'Schema: jogos_app criado com % tabelas', 
        (SELECT count(*) FROM information_schema.tables WHERE table_schema = 'jogos_app');
    RAISE NOTICE 'Conecte-se usando: Host=localhost, Port=5432, Database=sistemaregistrojogos, User=jogos_user';
END
$$; 