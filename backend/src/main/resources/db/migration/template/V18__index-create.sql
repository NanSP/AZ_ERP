-- Índices core
CREATE INDEX idx_parceiros_tipo ON core.parceiros(tipo_parceiro);
CREATE INDEX idx_produtos_codigo ON core.produtos(codigo);
CREATE INDEX idx_enderecos_entidade ON core.enderecos(entidade_tipo, entidade_id);

-- Índices financeiro
CREATE INDEX idx_contas_pagar_vencimento ON financeiro.contas_pagar(data_vencimento);
CREATE INDEX idx_contas_pagar_status ON financeiro.contas_pagar(status);
CREATE INDEX idx_contas_receber_vencimento ON financeiro.contas_receber(data_vencimento);

-- Índices vendas
CREATE INDEX idx_pedidos_cliente ON vendas.pedidos(cliente_id);
CREATE INDEX idx_pedidos_data ON vendas.pedidos(data_pedido);

-- Índices produção
CREATE INDEX idx_ordens_producao_status ON producao.ordens_producao(status);
CREATE INDEX idx_bom_produto ON producao.bom(produto_pai_id);

-- Índices BI e auditoria
CREATE INDEX idx_log_acoes_usuario ON auditoria.log_acoes(usuario_id);
CREATE INDEX idx_log_acoes_data ON auditoria.log_acoes(created_at);
CREATE INDEX idx_metricas_periodo ON bi.historico_metricas(periodo);
