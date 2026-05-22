package com.example.backend.fi.fluxoCaixa;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FluxoCaixaService {

    private final FluxoCaixaRepository repository;

    public FluxoCaixaService(FluxoCaixaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FluxoCaixa criar(FluxoCaixaRequestDTO data) {
        validar(data);

        FluxoCaixa entity = new FluxoCaixa();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public FluxoCaixa atualizar(Integer id, FluxoCaixaRequestDTO data) {
        validar(data);

        FluxoCaixa entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fluxo de caixa nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        FluxoCaixa entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fluxo de caixa nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            FluxoCaixa entity,
            FluxoCaixaRequestDTO data,
            LocalDateTime createdAt
    ) {
        BigDecimal saldoInicial = nvl(data.saldoInicial());
        BigDecimal entradasPrevistas = nvl(data.entradasPrevistas());
        BigDecimal saidasPrevistas = nvl(data.saidasPrevistas());
        BigDecimal entradasRealizadas = nvl(data.entradasRealizadas());
        BigDecimal saidasRealizadas = nvl(data.saidasRealizadas());

        entity.setDataReferencia(data.dataReferencia());
        entity.setSaldoInicial(saldoInicial);
        entity.setEntradasPrevistas(entradasPrevistas);
        entity.setSaidasPrevistas(saidasPrevistas);
        entity.setEntradasRealizadas(entradasRealizadas);
        entity.setSaidasRealizadas(saidasRealizadas);
        entity.setSaldoFinalPrevisto(saldoInicial.add(entradasPrevistas).subtract(saidasPrevistas));
        entity.setSaldoFinalReal(saldoInicial.add(entradasRealizadas).subtract(saidasRealizadas));
        entity.setCreatedAt(createdAt);
    }

    private void validar(FluxoCaixaRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do fluxo de caixa sao obrigatorios");
        }

        if (data.dataReferencia() == null) {
            throw new ValidacaoException("Data de referencia e obrigatoria");
        }

        validarNaoNegativo(data.saldoInicial(), "Saldo inicial nao pode ser negativo");
        validarNaoNegativo(data.entradasPrevistas(), "Entradas previstas nao podem ser negativas");
        validarNaoNegativo(data.saidasPrevistas(), "Saidas previstas nao podem ser negativas");
        validarNaoNegativo(data.entradasRealizadas(), "Entradas realizadas nao podem ser negativas");
        validarNaoNegativo(data.saidasRealizadas(), "Saidas realizadas nao podem ser negativas");
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}