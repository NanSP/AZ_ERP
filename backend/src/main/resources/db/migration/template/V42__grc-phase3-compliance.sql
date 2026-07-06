CREATE TABLE grc.relatorios_impacto_privacidade (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(180) NOT NULL,
    escopo_critico VARCHAR(50) NOT NULL,
    prioridade_risco VARCHAR(10) NOT NULL,
    modulo VARCHAR(40),
    recurso VARCHAR(60),
    finalidade TEXT NOT NULL,
    dados_pessoais_envolvidos TEXT NOT NULL,
    dados_sensiveis BOOLEAN NOT NULL DEFAULT false,
    base_legal VARCHAR(50) NOT NULL,
    volume_titulares INTEGER,
    compartilhamento_externo BOOLEAN NOT NULL DEFAULT false,
    medidas_tecnicas TEXT NOT NULL,
    medidas_organizacionais TEXT NOT NULL,
    risco_residual VARCHAR(10) NOT NULL,
    decisao VARCHAR(20) NOT NULL,
    aprovado_por_id INTEGER REFERENCES sys.usuarios(id),
    revisado_em TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grc.incidentes_seguranca (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(40) UNIQUE,
    titulo VARCHAR(180) NOT NULL,
    escopo_critico VARCHAR(50) NOT NULL,
    severidade VARCHAR(10) NOT NULL,
    etapa_atual VARCHAR(20) NOT NULL,
    origem_deteccao VARCHAR(30) NOT NULL,
    resumo_incidente TEXT NOT NULL,
    dados_afetados TEXT,
    titulares_estimados INTEGER,
    segredo_tecnico_exposto BOOLEAN NOT NULL DEFAULT false,
    requer_comunicacao_anpd BOOLEAN NOT NULL DEFAULT false,
    requer_comunicacao_titulares BOOLEAN NOT NULL DEFAULT false,
    data_deteccao TIMESTAMP NOT NULL,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_avaliacao TIMESTAMP,
    data_resposta TIMESTAMP,
    data_comunicacao TIMESTAMP,
    data_encerramento TIMESTAMP,
    causa_raiz TEXT,
    acoes_contencao TEXT,
    acoes_corretivas TEXT,
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grc.governanca_privacidade (
    id SERIAL PRIMARY KEY,
    nome_referencia VARCHAR(160) NOT NULL,
    papel_privacidade VARCHAR(25) NOT NULL,
    encarregado_nome VARCHAR(160) NOT NULL,
    encarregado_email VARCHAR(160) NOT NULL,
    encarregado_canal VARCHAR(200) NOT NULL,
    base_contratual TEXT NOT NULL,
    clausulas_contratuais TEXT NOT NULL,
    suboperadores_declarados BOOLEAN NOT NULL DEFAULT false,
    transferencia_internacional BOOLEAN NOT NULL DEFAULT false,
    procedimento_incidente TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    vigente_desde TIMESTAMP,
    revisao_programada_em TIMESTAMP,
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_relatorios_impacto_escopo
    ON grc.relatorios_impacto_privacidade(escopo_critico, prioridade_risco);

CREATE INDEX idx_incidentes_seguranca_etapa
    ON grc.incidentes_seguranca(etapa_atual, severidade);

CREATE INDEX idx_governanca_privacidade_ativo
    ON grc.governanca_privacidade(ativo);
