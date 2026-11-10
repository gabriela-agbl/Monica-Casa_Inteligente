-- 0. Banco usado pela Evolution API (DATABASE_CONNECTION_URI do docker-compose aponta para evolution_db)
CREATE DATABASE evolution_db;

-- 1. Criar tabela de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) UNIQUE NOT NULL,
    perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('ADMIN', 'COMUM')),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Criar tabela de Dispositivos
CREATE TABLE IF NOT EXISTS dispositivos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('ILUMINACAO', 'CLIMATIZACAO', 'IRRIGACAO', 'SEGURANCA', 'OUTRO')),
    status BOOLEAN DEFAULT FALSE,
    comodo VARCHAR(50),
    acesso_convidado BOOLEAN NOT NULL DEFAULT FALSE,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_dispositivo FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Criar tabela de Rotinas Agendadas
CREATE TABLE IF NOT EXISTS rotinas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    horario TIME NOT NULL,
    dias_semana VARCHAR(50) NOT NULL,
    acao VARCHAR(20) NOT NULL CHECK (acao IN ('LIGAR', 'DESLIGAR')),
    ativo BOOLEAN DEFAULT TRUE,
    dispositivo_id BIGINT NOT NULL,
    CONSTRAINT fk_dispositivo_rotina FOREIGN KEY (dispositivo_id) REFERENCES dispositivos(id) ON DELETE CASCADE
);

-- 4. Criar tabela de Alertas e Notificacoes
CREATE TABLE IF NOT EXISTS alertas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    mensagem TEXT NOT NULL,
    nivel VARCHAR(20) NOT NULL CHECK (nivel IN ('INFO', 'ALERTA', 'CRITICO')),
    enviado_whatsapp BOOLEAN DEFAULT FALSE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_alerta FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 5. Criar tabela de Configuracoes
CREATE TABLE IF NOT EXISTS configuracoes (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(100) UNIQUE NOT NULL,
    valor TEXT NOT NULL,
    descricao VARCHAR(255),
    usuario_id BIGINT,
    CONSTRAINT fk_usuario_configuracao FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

--6. Historico de uso/acionamento dos dispositivos
CREATE TABLE IF NOT EXISTS historico_dispositivos (
    id BIGSERIAL PRIMARY KEY,
    dispositivo_id BIGINT NOT NULL,
    ligado BOOLEAN NOT NULL,
    origem VARCHAR(20) NOT NULL CHECK (origem IN ('MANUAL', 'ROTINA')),
    acionado_por VARCHAR(150),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dispositivo_historico FOREIGN KEY (dispositivo_id) REFERENCES dispositivos(id) ON DELETE CASCADE
);

-- Compatibilidade com bancos criados por versões anteriores do projeto.
ALTER TABLE dispositivos ADD COLUMN IF NOT EXISTS acesso_convidado BOOLEAN NOT NULL DEFAULT FALSE;
