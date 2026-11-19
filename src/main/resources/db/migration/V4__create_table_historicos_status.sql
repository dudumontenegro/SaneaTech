CREATE TABLE historicos_status
(
    id              BIGSERIAL                NOT NULL,
    status_anterior VARCHAR(20),
    status_novo     VARCHAR(20)              NOT NULL,
    observacao      VARCHAR(300)             NOT NULL,
    registrado_em   TIMESTAMP WITH TIME ZONE NOT NULL,
    ocorrencia_id   BIGINT                   NOT NULL,
    equipe_id       BIGINT,

    CONSTRAINT pk_historicos_status PRIMARY KEY (id),
    CONSTRAINT ck_historicos_status_anterior CHECK (status_anterior IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA')),
    CONSTRAINT ck_historicos_status_novo CHECK (status_novo IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA')),
    CONSTRAINT fk_historicos_status_ocorrencia FOREIGN KEY (ocorrencia_id) REFERENCES ocorrencias (id) ON DELETE CASCADE,
    CONSTRAINT fk_historicos_status_equipe FOREIGN KEY (equipe_id) REFERENCES equipes_responsaveis (id)
);

CREATE INDEX idx_historicos_status_ocorrencia ON historicos_status (ocorrencia_id);
