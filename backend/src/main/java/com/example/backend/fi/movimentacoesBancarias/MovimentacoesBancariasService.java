package com.example.backend.fi.movimentacoesBancarias;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MovimentacoesBancariasService {

    private final MovimentacoesBancariasRepository repository;

    public MovimentacoesBancariasService(MovimentacoesBancariasRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MovimentacoesBancarias criar(MovimentacoesBancariasRequestDTO data) {
        validar(data);

        MovimentacoesBancarias entity = new MovimentacoesBancarias();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public MovimentacoesBancarias atualizar(Integer id, MovimentacoesBancariasRequestDTO data) {
        validar(data);

        MovimentacoesBancarias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao bancaria nao encontrada"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        MovimentacoesBancarias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao bancaria nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            MovimentacoesBancarias entity,
            MovimentacoesBancariasRequestDTO data,
            LocalDateTime createdAt
    ) {
        boolean conciliado = Boolean.TRUE.equals(data.conciliado());

        entity.setContaBancariaId(data.contaBancariaId());
        entity.setTipoMovimento(normalizarTipoMovimento(data.tipoMovimento()));
        entity.setValor(data.valor());
        entity.setDataMovimento(data.dataMovimento());
        entity.setHistorico(data.historico());
        entity.setDocumentoVinculado(data.documentoVinculado());
        entity.setConciliado(conciliado);
        entity.setDataConciliacao(conciliado ? data.dataConciliacao() : null);
        entity.setCreatedAt(createdAt);
    }

    private void validar(MovimentacoesBancariasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da movimentacao bancaria sao obrigatorios");
        }

        if (data.contaBancariaId() == null) {
            throw new ValidacaoException("Conta bancaria e obrigatoria");
        }

        if (data.tipoMovimento() == null || data.tipoMovimento().isBlank()) {
            throw new ValidacaoException("Tipo de movimento e obrigatorio");
        }

        String tipo = normalizarTipoMovimento(data.tipoMovimento());
        if (!tipo.equals("credito") && !tipo.equals("debito") && !tipo.equals("transferencia")) {
            throw new ValidacaoException("Tipo de movimento invalido");
        }

        if (data.valor() == null || data.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor deve ser maior que zero");
        }

        if (data.dataMovimento() == null) {
            throw new ValidacaoException("Data do movimento e obrigatoria");
        }

        if (Boolean.TRUE.equals(data.conciliado()) && data.dataConciliacao() == null) {
            throw new ValidacaoException("Data de conciliacao e obrigatoria quando a movimentacao estiver conciliada");
        }

        if (!Boolean.TRUE.equals(data.conciliado()) && data.dataConciliacao() != null) {
            throw new ValidacaoException("Data de conciliacao nao deve ser informada para movimentacao nao conciliada");
        }
    }

    private String normalizarTipoMovimento(String tipoMovimento) {
        return tipoMovimento == null ? "" : tipoMovimento.trim().toLowerCase();
    }
}