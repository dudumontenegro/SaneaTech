CREATE TABLE cidadaos
(
    id       BIGSERIAL    NOT NULL,
    nome     VARCHAR(120) NOT NULL,
    email    VARCHAR(160) NOT NULL,
    telefone VARCHAR(25),

    CONSTRAINT pk_cidadaos PRIMARY KEY (id),
    CONSTRAINT uk_cidadaos_email UNIQUE (email)
);
