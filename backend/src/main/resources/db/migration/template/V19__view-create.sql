-- View de fluxo de caixa consolidado
CREATE VIEW bi.vw_fluxo_caixa AS
SELECT
    COALESCE(data, CURRENT_DATE) as data,
    SUM(entradas) as total_entradas,
    SUM(saidas) as total_saidas,
    SUM(entradas) - SUM(saidas) as saldo
FROM (
    SELECT data_recebimento as data, valor_recebido as entradas, 0 as saidas
    FROM financeiro.contas_receber WHERE status = 'pago'
    UNION ALL
    SELECT data_pagamento as data, 0 as entradas, valor_pago as saidas
    FROM financeiro.contas_pagar WHERE status = 'pago'
) AS movimentos
GROUP BY data
ORDER BY data;

-- View de performance de vendas
CREATE VIEW bi.vw_performance_vendas AS
SELECT
    p.data_pedido,
    c.nome as cliente,
    COUNT(DISTINCT p.id) as total_pedidos,
    SUM(p.valor_total) as valor_total,
    AVG(p.valor_total) as ticket_medio
FROM vendas.pedidos p
JOIN core.parceiros c ON p.cliente_id = c.id
GROUP BY p.data_pedido, c.nome;