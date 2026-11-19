CREATE TABLE equipes_responsaveis
(
    id           BIGSERIAL    NOT NULL,
    nome         VARCHAR(120) NOT NULL,
    area_atuacao VARCHAR(120) NOT NULL,

    CONSTRAINT pk_equipes_responsaveis PRIMARY KEY (id),
    CONSTRAINT uk_equipes_responsaveis_nome UNIQUE (nome)
);
