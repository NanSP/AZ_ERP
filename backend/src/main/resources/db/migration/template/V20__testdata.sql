-- Script de inserção de dados de teste para todas as tabelas do ERP AZ_ERP
-- Insere 2 linhas por tabela com dados fictícios
-- Ordem respeita dependências de FK

-- Core
INSERT INTO core.empresas (codigo, razao_social, nome_fantasia, cnpj, inscricao_estadual, inscricao_municipal, regime_tributario, data_fundacao, situacao) VALUES
('EMP001', 'Empresa A Ltda', 'Empresa A', '12345678000101', '123456789', '987654321', 'Simples Nacional', '2020-01-01', 'ativo'),
('EMP002', 'Empresa B S.A.', 'Empresa B', '98765432000102', '987654321', '123456789', 'Lucro Presumido', '2019-05-15', 'ativo');

INSERT INTO core.enderecos (entidade_tipo, entidade_id, tipo_endereco, logradouro, numero, bairro, cidade, uf, cep, principal) VALUES
('empresa', 1, 'comercial', 'Rua A', '100', 'Centro', 'São Paulo', 'SP', '01000000', true),
('empresa', 2, 'comercial', 'Rua B', '200', 'Centro', 'Rio de Janeiro', 'RJ', '20000000', true);

INSERT INTO core.contatos (entidade_tipo, entidade_id, tipo_contato, valor, principal) VALUES
('empresa', 1, 'email', 'contato@empresaa.com', true),
('empresa', 2, 'telefone', '11999999999', true);

INSERT INTO core.parceiros (tipo_parceiro, codigo, nome, documento, tipo_pessoa, situacao) VALUES
('cliente', 'PAR001', 'Cliente X', '11122233344', 'F', 'ativo'),
('fornecedor', 'PAR002', 'Fornecedor Y', '55666777000111', 'J', 'ativo');

INSERT INTO core.produtos (codigo, nome, tipo_item, unidade_medida, situacao) VALUES
('PROD001', 'Produto A', 'produto', 'UN', 'ativo'),
('PROD002', 'Serviço B', 'servico', 'HR', 'ativo');

-- Sys
INSERT INTO sys.perfis (nome, descricao, nivel_acesso) VALUES
('Admin', 'Administrador', 10),
('User', 'Usuário comum', 1);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao) VALUES
('read_empresas', 'Ler empresas', 'core', 'empresas', 'read'),
('write_empresas', 'Editar empresas', 'core', 'empresas', 'update');

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id) VALUES
(1, 1),
(1, 2);

INSERT INTO sys.usuarios (nome, email, login, senha_hash, status) VALUES
('Admin User', 'admin@empresa.com', 'admin', 'hashedpassword', 'ativo'),
('Normal User', 'user@empresa.com', 'user', 'hashedpassword', 'ativo');

INSERT INTO sys.usuario_perfil (usuario_id, perfil_id) VALUES
(1, 1),
(2, 2);

-- RH
INSERT INTO rh.colaboradores (codigo, nome, cpf, data_admissao, cargo, situacao) VALUES
('COL001', 'João Silva', '12345678901', '2023-01-01', 'Analista', 'ativo'),
('COL002', 'Maria Santos', '98765432100', '2023-02-01', 'Gerente', 'ativo');

INSERT INTO rh.dependentes (colaborador_id, nome, parentesco) VALUES
(1, 'Filho João', 'filho'),
(2, 'Esposa Maria', 'esposa');

INSERT INTO rh.folha_pagamento (colaborador_id, competencia, salario_base, valor_liquido) VALUES
(1, '2023-01-01', 3000.00, 2500.00),
(2, '2023-01-01', 5000.00, 4000.00);

INSERT INTO rh.beneficios (colaborador_id, tipo_beneficio, valor) VALUES
(1, 'vale_transporte', 200.00),
(2, 'plano_saude', 300.00);

INSERT INTO rh.ponto_eletronico (colaborador_id, data, hora_entrada, hora_saida) VALUES
(1, '2023-01-01', '08:00', '17:00'),
(2, '2023-01-01', '09:00', '18:00');

-- Financeiro
INSERT INTO financeiro.centros_custo (codigo, nome) VALUES
('CC001', 'Centro A'),
('CC002', 'Centro B');

INSERT INTO financeiro.contas_pagar (empresa_id, fornecedor_id, centro_custo_id, numero_documento, valor_original, data_vencimento, status) VALUES
(1, 2, 1, 'DOC001', 1000.00, '2023-12-01', 'pendente'),
(1, 2, 2, 'DOC002', 2000.00, '2023-12-15', 'pendente');

INSERT INTO financeiro.contas_receber (empresa_id, cliente_id, centro_custo_id, numero_documento, valor_original, data_vencimento, status) VALUES
(1, 1, 1, 'REC001', 1500.00, '2023-12-01', 'pendente'),
(1, 1, 2, 'REC002', 2500.00, '2023-12-15', 'pendente');

INSERT INTO financeiro.movimentacoes_bancarias (tipo_movimento, valor, data_movimento, historico) VALUES
('credito', 1000.00, '2023-01-01', 'Recebimento'),
('debito', 500.00, '2023-01-02', 'Pagamento');

INSERT INTO financeiro.fluxo_caixa (data_referencia, entradas_previstas, saidas_previstas) VALUES
('2023-01-01', 2000.00, 1000.00),
('2023-01-02', 1500.00, 800.00);

-- Contábil
INSERT INTO contabil.plano_contas (codigo, nome, tipo_conta, natureza) VALUES
('PC001', 'Conta Ativo', 'analitica', 'devedora'),
('PC002', 'Conta Passivo', 'analitica', 'credora');

-- Materiais
INSERT INTO materiais.materiais (produto_id, tipo_material) VALUES
(1, 'Material A'),
(2, 'Material B');

INSERT INTO materiais.estoques (produto_id, empresa_id, quantidade, quantidade_minima) VALUES
(1, 1, 100.00, 10.00),
(2, 1, 200.00, 20.00);

INSERT INTO materiais.movimentacoes (estoque_id, tipo_movimento, quantidade, documento_referencia) VALUES
(1, 'entrada', 50.00, 'DOC001'),
(2, 'saida', 25.00, 'DOC002');

INSERT INTO materiais.compras (fornecedor_id, data_pedido, valor_total, status) VALUES
(2, '2023-01-01', 1000.00, 'aberto'),
(2, '2023-01-02', 2000.00, 'aberto');

INSERT INTO materiais.compra_itens (compra_id, produto_id, quantidade, valor_unitario) VALUES
(1, 1, 10.00, 50.00),
(2, 2, 20.00, 100.00);

INSERT INTO materiais.inventarios (data_inicio, data_fim, tipo_inventario, status) VALUES
('2023-01-01', '2023-01-05', 'anual', 'planejado'),
('2023-02-01', '2023-02-05', 'rotativo', 'planejado');

-- CRM
INSERT INTO crm.clientes (parceiro_id, classificacao) VALUES
(1, 'VIP'),
(1, 'Regular');

-- Vendas
INSERT INTO vendas.pedidos (cliente_id, numero_pedido, data_pedido, valor_total, status) VALUES
(1, 'PED001', '2023-01-01', 1000.00, 'aberto'),
(1, 'PED002', '2023-01-02', 2000.00, 'aberto');

INSERT INTO vendas.pedido_itens (pedido_id, produto_id, quantidade, valor_unitario) VALUES
(1, 1, 5.00, 100.00),
(2, 2, 10.00, 200.00);

INSERT INTO vendas.faturas (pedido_id, numero_fatura, data_emissao, valor_total) VALUES
(1, 'FAT001', '2023-01-01', 1000.00),
(2, 'FAT002', '2023-01-02', 2000.00);

INSERT INTO vendas.contratos (cliente_id, numero_contrato, objeto, valor_total, data_inicio, data_fim) VALUES
(1, 'CON001', 'Contrato A', 5000.00, '2023-01-01', '2023-12-31'),
(1, 'CON002', 'Contrato B', 10000.00, '2023-02-01', '2024-01-31');

-- Produção
INSERT INTO producao.bom (produto_pai_id, componente_id, quantidade) VALUES
(1, 1, 2.00),
(2, 2, 1.00);

INSERT INTO producao.ordens_producao (numero_op, produto_id, quantidade_planejada, data_emissao, status) VALUES
('OP001', 1, 100.00, '2023-01-01', 'planejada'),
('OP002', 2, 200.00, '2023-01-02', 'planejada');

INSERT INTO producao.apontamentos (op_id, quantidade_produzida, data_hora_inicio, data_hora_fim) VALUES
(1, 50.00, '2023-01-01 08:00', '2023-01-01 17:00'),
(2, 100.00, '2023-01-02 09:00', '2023-01-02 18:00');

INSERT INTO producao.mrp (produto_id, periodo, demanda_prevista, estoque_atual) VALUES
(1, '2023-01-01', 100.00, 50.00),
(2, '2023-01-01', 200.00, 100.00);

-- Projetos
INSERT INTO projetos.projetos (codigo, nome, cliente_id, gerente_id, data_inicio, data_fim, orcamento_total, status) VALUES
('PROJ001', 'Projeto A', 1, 1, '2023-01-01', '2023-06-01', 10000.00, 'planejado'),
('PROJ002', 'Projeto B', 1, 2, '2023-02-01', '2023-07-01', 20000.00, 'planejado');

INSERT INTO projetos.tarefas (projeto_id, titulo, responsavel_id, data_inicio, data_fim, horas_estimadas) VALUES
(1, 'Tarefa 1', 1, '2023-01-01', '2023-01-15', 40.00),
(2, 'Tarefa 2', 2, '2023-02-01', '2023-02-15', 50.00);

INSERT INTO projetos.recursos_alocados (projeto_id, tipo_recurso, recurso_id, quantidade, valor_unitario) VALUES
(1, 'humano', 1, 1.00, 100.00),
(2, 'material', 1, 10.00, 50.00);

-- Serviços
INSERT INTO servicos.ordens_servico (numero_os, cliente_id, produto_id, tipo_servico, data_abertura, status) VALUES
('OS001', 1, 1, 'Manutenção', '2023-01-01', 'aberta'),
('OS002', 1, 2, 'Instalação', '2023-01-02', 'aberta');

INSERT INTO servicos.sla_config (tipo_servico, prioridade, tempo_atendimento_horas) VALUES
('Manutenção', 'normal', 24),
('Instalação', 'alta', 12);

INSERT INTO servicos.atendimentos (os_id, tecnico_id, descricao, horas_gastas) VALUES
(1, 1, 'Atendimento inicial', 2.00),
(2, 2, 'Instalação realizada', 4.00);

-- Qualidade
INSERT INTO qualidade.inspecoes (tipo_inspecao, produto_id, quantidade_inspecionada, quantidade_aprovada, data_inspecao, resultado) VALUES
('recebimento', 1, 100.00, 95.00, '2023-01-01', 'aprovado'),
('final', 2, 200.00, 190.00, '2023-01-02', 'aprovado');

INSERT INTO qualidade.nao_conformidades (inspecao_id, tipo_nao_conformidade, descricao, status) VALUES
(1, 'Defeito', 'Produto com defeito', 'aberta'),
(2, 'Irregularidade', 'Especificação fora do padrão', 'aberta');

-- Ativos
INSERT INTO ativos.bens_patrimoniais (codigo_patrimonio, nome, data_aquisicao, valor_aquisicao, status) VALUES
('PAT001', 'Computador', '2023-01-01', 2000.00, 'ativo'),
('PAT002', 'Impressora', '2023-01-02', 1000.00, 'ativo');

INSERT INTO ativos.manutencoes (ativo_id, tipo_manutencao, data_execucao, custo_total) VALUES
(1, 'preventiva', '2023-01-01', 100.00),
(2, 'corretiva', '2023-01-02', 200.00);

-- Fiscal
INSERT INTO fiscal.documentos (tipo_documento, numero, chave_acesso, data_emissao, cliente_id, valor_total) VALUES
('nfe', '001', 'chave123', '2023-01-01', 1, 1000.00),
('nfce', '002', 'chave456', '2023-01-02', 1, 500.00);

-- SPED
INSERT INTO sped.ecd_registros (periodo, registro, conteudo) VALUES
('2023-01-01', '0000', '{"campo": "valor"}'),
('2023-01-01', '0001', '{"campo": "valor2"}');

INSERT INTO sped.efd_registros (periodo, registro, conteudo) VALUES
('2023-01-01', '0000', '{"campo": "valor"}'),
('2023-01-01', '0001', '{"campo": "valor2"}');

INSERT INTO sped.esocial_eventos (periodo_apuracao, tipo_evento, evento_id, conteudo) VALUES
('2023-01-01', 'S-1000', 'evt001', '<xml>conteudo</xml>'),
('2023-01-01', 'S-1010', 'evt002', '<xml>conteudo2</xml>');

-- BI
INSERT INTO bi.dashboards (nome, descricao) VALUES
('Dashboard Vendas', 'Painel de vendas'),
('Dashboard Financeiro', 'Painel financeiro');

INSERT INTO bi.relatorios (nome, descricao, query_sql) VALUES
('Relatório Vendas', 'Vendas mensais', 'SELECT * FROM vendas.pedidos'),
('Relatório Estoque', 'Estoque atual', 'SELECT * FROM materiais.estoques');

INSERT INTO bi.metricas (nome, descricao, categoria, unidade_medida, meta) VALUES
('Vendas Totais', 'Total de vendas', 'vendas', 'R$', 10000.00),
('Estoque Médio', 'Estoque médio', 'estoque', 'UN', 500.00);

INSERT INTO bi.historico_metricas (metrica_id, periodo, valor_apurado) VALUES
(1, '2023-01-01', 5000.00),
(2, '2023-01-01', 300.00);

-- GRC
INSERT INTO grc.riscos (codigo, titulo, probabilidade, impacto, nivel_risco) VALUES
('RIS001', 'Risco A', 3, 4, 'medio'),
('RIS002', 'Risco B', 2, 5, 'alto');

INSERT INTO grc.auditorias (titulo, tipo_auditoria, escopo, data_inicio, data_fim, status) VALUES
('Auditoria Interna', 'interna', 'Processos', '2023-01-01', '2023-01-31', 'planejada'),
('Auditoria Externa', 'externa', 'Financeiro', '2023-02-01', '2023-02-28', 'planejada');

INSERT INTO grc.controles (codigo, descricao, tipo_controle) VALUES
('CTL001', 'Controle A', 'preventivo'),
('CTL002', 'Controle B', 'detectivo');

INSERT INTO grc.consentimentos (titular_id, tipo_titular, finalidade) VALUES
(1, 'cliente', 'Marketing'),
(2, 'cliente', 'Pesquisa');

-- Portal
INSERT INTO portal.sessoes (usuario_id, token_sessao, data_login) VALUES
(1, 'token123', '2023-01-01 08:00'),
(2, 'token456', '2023-01-01 09:00');

INSERT INTO portal.notificacoes (usuario_id, titulo, mensagem) VALUES
(1, 'Notificação 1', 'Mensagem 1'),
(2, 'Notificação 2', 'Mensagem 2');

-- Mobile
INSERT INTO mobile.dispositivos (usuario_id, device_id, device_platform) VALUES
(1, 'device123', 'Android'),
(2, 'device456', 'iOS');

-- Auditoria
INSERT INTO auditoria.log_acoes (usuario_id, modulo, acao, tabela, registro_id, dados_novos) VALUES
(1, 'core', 'create', 'empresas', 1, '{"codigo": "EMP001"}'),
(2, 'core', 'update', 'produtos', 1, '{"nome": "Produto A Atualizado"}');

INSERT INTO auditoria.log_erros (erro_mensagem, modulo, usuario_id) VALUES
('Erro de conexão', 'core', 1),
('Erro de validação', 'financeiro', 2);