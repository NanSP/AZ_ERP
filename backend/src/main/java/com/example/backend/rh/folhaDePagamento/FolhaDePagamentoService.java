package com.example.backend.rh.folhaDePagamento;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FolhaDePagamentoService {

    private static final BigDecimal HORAS_MES_PADRAO = new BigDecimal("220");
    private static final BigDecimal FATOR_HORA_EXTRA = new BigDecimal("1.50");
    private static final int SCALE_INTERNA = 10;
    private static final int SCALE_FINAL = 2;

    public FolhaCalculadaDTO calcular(FolhaDePagamentoRequestDTO data) {
        BigDecimal salarioBase = nvl(data.salarioBase());
        BigDecimal horasNormais = nvl(data.horasNormais());
        BigDecimal horasExtras = nvl(data.horasExtras());
        BigDecimal adicionais = nvl(data.adicionais());
        BigDecimal descontos = nvl(data.descontos());

        BigDecimal valorHoraCalculado = BigDecimal.ZERO;
        if (HORAS_MES_PADRAO.compareTo(BigDecimal.ZERO) > 0) {
            valorHoraCalculado = salarioBase.divide(HORAS_MES_PADRAO, SCALE_INTERNA, RoundingMode.HALF_UP);
        }

        BigDecimal valorHorasNormais = salarioBase;

        BigDecimal valorHorasExtras = valorHoraCalculado
                .multiply(FATOR_HORA_EXTRA)
                .multiply(horasExtras);

        BigDecimal valorBruto = valorHorasNormais
                .add(valorHorasExtras)
                .add(adicionais);

        BigDecimal valorLiquido = valorBruto.subtract(descontos);

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