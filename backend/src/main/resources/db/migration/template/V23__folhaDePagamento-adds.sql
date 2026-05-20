ALTER TABLE rh.folha_pagamento
    ADD COLUMN valor_hora DECIMAL(15,2),
    ADD COLUMN valor_horas_normais DECIMAL(15,2),
    ADD COLUMN valor_horas_extras DECIMAL(15,2),
    ADD COLUMN valor_bruto DECIMAL(15,2);