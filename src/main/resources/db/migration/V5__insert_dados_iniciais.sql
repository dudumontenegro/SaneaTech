-- Dados iniciais para as features que dependem de registros já existentes no banco:
-- consulta de equipes, consulta de ocorrências e atualização de status.
INSERT INTO cidadaos (nome, email, telefone) VALUES ('Maria Silva', 'maria@saneatech.exemplo', '83999990000');

INSERT INTO equipes_responsaveis (nome, area_atuacao) VALUES ('Equipe de Redes', 'Água e esgoto');
INSERT INTO equipes_responsaveis (nome, area_atuacao) VALUES ('Equipe de Drenagem', 'Drenagem pluvial');

-- SAN-SEED-0001: PENDENTE com equipe (pode ir para EM_ANDAMENTO)
INSERT INTO ocorrencias (protocolo, tipo, descricao, status, criado_em, atualizado_em,
    endereco_logradouro, endereco_numero, endereco_bairro, endereco_cidade, endereco_uf, endereco_referencia,
    solicitante_id, equipe_id)
VALUES ('SAN-SEED-0001', 'VAZAMENTO_AGUA', 'Vazamento de água em via pública', 'PENDENTE',
    TIMESTAMP WITH TIME ZONE '2026-11-18 09:00:00+00', TIMESTAMP WITH TIME ZONE '2026-11-18 09:00:00+00',
    'Rua das Flores', '120', 'Centro', 'João Pessoa', 'PB', 'Próximo à praça',
    (SELECT id FROM cidadaos WHERE email = 'maria@saneatech.exemplo'),
    (SELECT id FROM equipes_responsaveis WHERE nome = 'Equipe de Redes'));

-- SAN-SEED-0002: EM_ANDAMENTO (pode ir para CONCLUIDA)
INSERT INTO ocorrencias (protocolo, tipo, descricao, status, criado_em, atualizado_em,
    endereco_logradouro, endereco_numero, endereco_bairro, endereco_cidade, endereco_uf, endereco_referencia,
    solicitante_id, equipe_id)
VALUES ('SAN-SEED-0002', 'ESGOTO_CEU_ABERTO', 'Esgoto a céu aberto na calçada', 'EM_ANDAMENTO',
    TIMESTAMP WITH TIME ZONE '2026-11-18 10:00:00+00', TIMESTAMP WITH TIME ZONE '2026-11-18 11:00:00+00',
    'Av. Epitácio Pessoa', '2000', 'Tambaú', 'João Pessoa', 'PB', NULL,
    (SELECT id FROM cidadaos WHERE email = 'maria@saneatech.exemplo'),
    (SELECT id FROM equipes_responsaveis WHERE nome = 'Equipe de Redes'));

-- SAN-SEED-0003: CONCLUIDA (nenhuma transição permitida)
INSERT INTO ocorrencias (protocolo, tipo, descricao, status, criado_em, atualizado_em,
    endereco_logradouro, endereco_numero, endereco_bairro, endereco_cidade, endereco_uf, endereco_referencia,
    solicitante_id, equipe_id)
VALUES ('SAN-SEED-0003', 'FALTA_ABASTECIMENTO', 'Bairro sem água há dois dias', 'CONCLUIDA',
    TIMESTAMP WITH TIME ZONE '2026-11-17 08:00:00+00', TIMESTAMP WITH TIME ZONE '2026-11-17 18:00:00+00',
    'Rua Projetada', 'S/N', 'Mangabeira', 'João Pessoa', 'PB', NULL,
    (SELECT id FROM cidadaos WHERE email = 'maria@saneatech.exemplo'),
    (SELECT id FROM equipes_responsaveis WHERE nome = 'Equipe de Redes'));

-- SAN-SEED-0004: PENDENTE sem equipe (iniciar atendimento é bloqueado pelo domínio)
INSERT INTO ocorrencias (protocolo, tipo, descricao, status, criado_em, atualizado_em,
    endereco_logradouro, endereco_numero, endereco_bairro, endereco_cidade, endereco_uf, endereco_referencia,
    solicitante_id, equipe_id)
VALUES ('SAN-SEED-0004', 'OUTROS', 'Bueiro entupido', 'PENDENTE',
    TIMESTAMP WITH TIME ZONE '2026-11-19 07:30:00+00', TIMESTAMP WITH TIME ZONE '2026-11-19 07:30:00+00',
    'Rua das Acácias', '45', 'Bancários', 'João Pessoa', 'PB', NULL,
    (SELECT id FROM cidadaos WHERE email = 'maria@saneatech.exemplo'), NULL);

-- Histórico existente de cada ocorrência
INSERT INTO historicos_status (status_anterior, status_novo, observacao, registrado_em, ocorrencia_id, equipe_id)
SELECT NULL, 'PENDENTE', 'Ocorrência registrada pelo cidadão.', criado_em, id, NULL
FROM ocorrencias WHERE protocolo LIKE 'SAN-SEED-%';

INSERT INTO historicos_status (status_anterior, status_novo, observacao, registrado_em, ocorrencia_id, equipe_id)
SELECT 'PENDENTE', 'EM_ANDAMENTO', 'Equipe deslocada ao endereço.', TIMESTAMP WITH TIME ZONE '2026-11-18 11:00:00+00', id, equipe_id
FROM ocorrencias WHERE protocolo = 'SAN-SEED-0002';

INSERT INTO historicos_status (status_anterior, status_novo, observacao, registrado_em, ocorrencia_id, equipe_id)
SELECT 'PENDENTE', 'EM_ANDAMENTO', 'Equipe deslocada ao endereço.', TIMESTAMP WITH TIME ZONE '2026-11-17 09:00:00+00', id, equipe_id
FROM ocorrencias WHERE protocolo = 'SAN-SEED-0003';

INSERT INTO historicos_status (status_anterior, status_novo, observacao, registrado_em, ocorrencia_id, equipe_id)
SELECT 'EM_ANDAMENTO', 'CONCLUIDA', 'Abastecimento restabelecido.', TIMESTAMP WITH TIME ZONE '2026-11-17 18:00:00+00', id, equipe_id
FROM ocorrencias WHERE protocolo = 'SAN-SEED-0003';
