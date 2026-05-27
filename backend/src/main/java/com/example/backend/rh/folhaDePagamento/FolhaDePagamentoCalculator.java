package com.example.backend.rh.folhaDePagamento;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FolhaDePagamentoCalculator {

    private static final BigDecimal HORAS_MES_PADRAO = new BigDecimal("220");
    private static final BigDecimal FATOR_HORA_EXTRA = new BigDecimal("1.50");
    private static final int SCALE_INTERNA = 10;
    private static final int SCALE_FINAL = 2;

    public FolhaCalculadaDTO calcular(
            BigDecimal salarioBase,
            BigDecimal horasNormais,
            BigDecimal horasExtras,
            BigDecimal adicionais,
            BigDecimal descontos
    ) {
        BigDecimal salarioBaseCalculado = nvl(salarioBase);
        BigDecimal horasNormaisCalculadas = nvl(horasNormais);
        BigDecimal horasExtrasCalculadas = nvl(horasExtras);
        BigDecimal adicionaisCalculados = nvl(adicionais);
        BigDecimal descontosCalculados = nvl(descontos);

        BigDecimal valorHoraCalculado = BigDecimal.ZERO;
        if (HORAS_MES_PADRAO.compareTo(BigDecimal.ZERO) > 0) {
            valorHoraCalculado = salarioBaseCalculado.divide(HORAS_MES_PADRAO, SCALE_INTERNA, RoundingMode.HALF_UP);
        }

        BigDecimal valorHorasNormais = valorHoraCalculado.multiply(horasNormaisCalculadas);

        BigDecimal valorHorasExtras = valorHoraCalculado
                .multiply(FATOR_HORA_EXTRA)
                .multiply(horasExtrasCalculadas);

        BigDecimal valorBruto = valorHorasNormais
                .add(valorHorasExtras)
                .add(adicionaisCalculados);

        BigDecimal valorLiquido = valorBruto.subtract(descontosCalculados);

        return new FolhaCalculadaDTO(
                valorHoraCalculado.setScale(SCALE_FINAL, RoundingMode.HALF_UP),
                valorHorasNormais.setScale(SCALE_FINAL, RoundingMode.HALF_UP),
                valorHorasExtras.setScale(SCALE_FINAL, RoundingMode.HALF_UP),
                valorBruto.setScale(SCALE_FINAL, RoundingMode.HALF_UP),
                valorLiquido.setScale(SCALE_FINAL, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
