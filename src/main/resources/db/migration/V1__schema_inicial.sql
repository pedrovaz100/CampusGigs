CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120)  NOT NULL,
    email         VARCHAR(150)  NOT NULL,
    senha_hash    VARCHAR(255)  NOT NULL,
    papel         VARCHAR(20)   NOT NULL DEFAULT 'USER',
    cep           VARCHAR(9),
    cidade        VARCHAR(100),
    uf            VARCHAR(2),
    criado_em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT ck_usuarios_papel CHECK (papel IN ('USER', 'ADMIN'))
);

CREATE TABLE servicos (
    id            BIGSERIAL PRIMARY KEY,
    prestador_id  BIGINT        NOT NULL,
    titulo        VARCHAR(150)  NOT NULL,
    descricao     VARCHAR(2000) NOT NULL,
    categoria     VARCHAR(80)   NOT NULL,
    preco         NUMERIC(10,2) NOT NULL,
    situacao      VARCHAR(20)   NOT NULL DEFAULT 'ATIVO',
    criado_em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_servicos_prestador FOREIGN KEY (prestador_id) REFERENCES usuarios (id),
    CONSTRAINT ck_servicos_situacao CHECK (situacao IN ('ATIVO', 'PAUSADO', 'ENCERRADO')),
    CONSTRAINT ck_servicos_preco_positivo CHECK (preco > 0)
);

CREATE TABLE contratacoes (
    id              BIGSERIAL PRIMARY KEY,
    servico_id      BIGINT        NOT NULL,
    contratante_id  BIGINT        NOT NULL,
    situacao        VARCHAR(20)   NOT NULL DEFAULT 'SOLICITADA',
    criado_em       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contratacoes_servico FOREIGN KEY (servico_id) REFERENCES servicos (id),
    CONSTRAINT fk_contratacoes_contratante FOREIGN KEY (contratante_id) REFERENCES usuarios (id),
    CONSTRAINT ck_contratacoes_situacao CHECK (situacao IN ('SOLICITADA', 'ACEITA', 'CONCLUIDA', 'CANCELADA'))
);

CREATE INDEX idx_servicos_prestador ON servicos (prestador_id);
CREATE INDEX idx_servicos_situacao ON servicos (situacao);
CREATE INDEX idx_contratacoes_servico ON contratacoes (servico_id);
CREATE INDEX idx_contratacoes_contratante ON contratacoes (contratante_id);
