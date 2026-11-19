CREATE TABLE ocorrencias
(
    id                  BIGSERIAL                NOT NULL,
    protocolo           VARCHAR(40)              NOT NULL,
    tipo                VARCHAR(30)              NOT NULL,
    descricao           VARCHAR(1000)            NOT NULL,
    status              VARCHAR(20)              NOT NULL,
    criado_em           TIMESTAMP WITH TIME ZONE NOT NULL,
    atualizado_em       TIMESTAMP WITH TIME ZONE NOT NULL,
    endereco_logradouro VARCHAR(180)             NOT NULL,
    endereco_numero     VARCHAR(20)              NOT NULL,
    endereco_bairro     VARCHAR(100)             NOT NULL,
    endereco_cidade     VARCHAR(100)             NOT NULL,
    endereco_uf         VARCHAR(2)               NOT NULL,
    endereco_referencia VARCHAR(200),
    solicitante_id      BIGINT                   NOT NULL,
    equipe_id           BIGINT,

    CONSTRAINT pk_ocorrencias PRIMARY KEY (id),
    CONSTRAINT uk_ocorrencias_protocolo UNIQUE (protocolo),
    CONSTRAINT ck_ocorrencias_tipo CHECK (tipo IN ('VAZAMENTO_AGUA', 'ESGOTO_CEU_ABERTO', 'FALTA_ABASTECIMENTO', 'OUTROS')),
    CONSTRAINT ck_ocorrencias_status CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA')),
    CONSTRAINT fk_ocorrencias_solicitante FOREIGN KEY (solicitante_id) REFERENCES cidadaos (id),
    CONSTRAINT fk_ocorrencias_equipe FOREIGN KEY (equipe_id) REFERENCES equipes_responsaveis (id)
);

CREATE INDEX idx_ocorrencias_solicitante ON ocorrencias (solicitante_id);
CREATE INDEX idx_ocorrencias_equipe ON ocorrencias (equipe_id);
CREATE INDEX idx_ocorrencias_status ON ocorrencias (status);
