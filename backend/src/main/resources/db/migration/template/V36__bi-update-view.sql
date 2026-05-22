CREATE OR REPLACE VIEW bi.vw_fluxo_caixa AS
SELECT
    COALESCE(data, CURRENT_DATE) AS data,
    SUM(entradas) AS total_entradas,
    SUM(saidas) AS total_saidas,
    SUM(entradas) - SUM(saidas) AS saldo
FROM (
    SELECT
        data_recebimento AS data,
        valor_recebido AS entradas,
        0::DECIMAL(15,2) AS saidas
    FROM financeiro.contas_receber
    WHERE status IN ('parcial', 'pago')
      AND valor_recebido IS NOT NULL
      AND valor_recebido > 0
      AND data_recebimento IS NOT NULL

    UNION ALL

    SELECT
        data_pagamento AS data,
        0::DECIMAL(15,2) AS entradas,
        valor_pago AS saidas
    FROM financeiro.contas_pagar
    WHERE status IN ('parcial', 'pago')
      AND valor_pago IS NOT NULL
      AND valor_pago > 0
      AND data_pagamento IS NOT NULL
) AS movimentos
GROUP BY data
ORDER BY data;