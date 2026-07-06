ALTER TABLE grc.consentimentos
    ADD COLUMN IF NOT EXISTS registro_tratamento_id INTEGER REFERENCES grc.registros_tratamento(id);

ALTER TABLE grc.solicitacoes_titular
    ADD COLUMN IF NOT EXISTS registro_tratamento_id INTEGER REFERENCES grc.registros_tratamento(id),
    ADD COLUMN IF NOT EXISTS consentimento_id INTEGER REFERENCES grc.consentimentos(id);

CREATE TABLE IF NOT EXISTS grc.solicitacao_titular_eventos (
    id SERIAL PRIMARY KEY,
    solicitacao_id INTEGER NOT NULL REFERENCES grc.solicitacoes_titular(id) ON DELETE CASCADE,
    tipo_evento VARCHAR(40) NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    descricao TEXT,
    detalhes_json JSONB,
    criado_por_id INTEGER REFERENCES sys.usuarios(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_consentimentos_registro_tratamento
    ON grc.consentimentos(registro_tratamento_id);

CREATE INDEX IF NOT EXISTS idx_solicitacoes_titular_registro_tratamento
    ON grc.solicitacoes_titular(registro_tratamento_id);

CREATE INDEX IF NOT EXISTS idx_solicitacoes_titular_consentimento
    ON grc.solicitacoes_titular(consentimento_id);

CREATE INDEX IF NOT EXISTS idx_solicitacao_titular_eventos_solicitacao
    ON grc.solicitacao_titular_eventos(solicitacao_id, created_at DESC);
