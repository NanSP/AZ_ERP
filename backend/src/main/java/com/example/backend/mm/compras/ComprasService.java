package com.example.backend.mm.compras;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.mm.compraItens.CompraItensRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ComprasService {

    private final ComprasRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final CompraItensRepository compraItensRepository;

    public ComprasService(
            ComprasRepository repository,
            ParceirosRepository parceirosRepository,
            CompraItensRepository compraItensRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.compraItensRepository = compraItensRepository;
    }

    @Transactional
    public Compras criar(ComprasRequestDTO data) {
        validar(data);

        Parceiros fornecedor = buscarFornecedor(data.fornecedor());

        Compras entity = new Compras();
        preencher(entity, data, fornecedor, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Compras atualizar(Integer id, ComprasRequestDTO data) {
        validar(data);

        Compras entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra nao encontrada"));

        Parceiros fornecedor = buscarFornecedor(data.fornecedor());
        preencher(entity, data, fornecedor, entity.getCreatedAt());
        sincronizarCompraComItens(entity);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Compras entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra nao encontrada"));

        if (compraItensRepository.existsByComprasId(id)) {
            throw new ValidacaoException("Nao e permitido excluir compra que possui itens");
        }

        repository.delete(entity);
    }

    private void preencher(
            Compras entity,
            ComprasRequestDTO data,
            Parceiros fornecedor,
            LocalDateTime createdAt
    ) {
        entity.setFornecedor(fornecedor);
        entity.setDataPedido(data.dataPedido());
        entity.setDataPrevistaEntrega(data.dataPrevistaEntrega());
        entity.setDataEntrega(data.dataEntrega());
        entity.setValorTotal(data.valorTotal());
        entity.setCondicoesPagamento(normalizarOpcional(data.condicoesPagamento()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ComprasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da compra sao obrigatorios");
        }

        if (data.fornecedor() == null) {
            throw new ValidacaoException("Fornecedor e obrigatorio");
        }

        if (data.dataPedido() == null) {
            throw new ValidacaoException("Data do pedido e obrigatoria");
        }

        validarNaoNegativo(data.valorTotal(), "Valor total nao pode ser negativo");

        if (data.dataPrevistaEntrega() != null && data.dataPrevistaEntrega().isBefore(data.dataPedido())) {
            throw new ValidacaoException("Data prevista de entrega nao pode ser anterior a data do pedido");
        }

        if (data.dataEntrega() != null && data.dataEntrega().isBefore(data.dataPedido())) {
            throw new ValidacaoException("Data de entrega nao pode ser anterior a data do pedido");
        }

        validarStatus(normalizarStatus(data.status()), data.dataEntrega());
    }

    private void sincronizarCompraComItens(Compras compra) {
        if (compra == null || compra.getId() == null || !compraItensRepository.existsByComprasId(compra.getId())) {
            return;
        }

        compra.setValorTotal(compraItensRepository.sumValorTotalByCompraId(compra.getId()));

        if (!compraItensRepository.existsByComprasIdAndQuantidadeRecebidaGreaterThan(compra.getId(), BigDecimal.ZERO)) {
            compra.setStatus("aberto");
            compra.setDataEntrega(null);
            return;
        }

        if (compraItensRepository.existsByComprasIdAndQuantidadeRecebidaLessThanQuantidade(compra.getId())) {
            compra.setStatus("parcial");
            return;
        }

        compra.setStatus("recebido");
        if (compra.getDataEntrega() == null) {
            compra.setDataEntrega(LocalDate.now());
        }
    }

    private void validarStatus(String status, LocalDate dataEntrega) {
        if (!status.equals("aberto")
                && !status.equals("parcial")
                && !status.equals("recebido")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }

        if (status.equals("recebido") && dataEntrega == null) {
            throw new ValidacaoException("Data de entrega e obrigatoria para compra recebida");
        }
    }

    private Parceiros buscarFornecedor(Integer fornecedorId) {
        return parceirosRepository.findById(fornecedorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "aberto" : valor.toLowerCase();
    }
}
