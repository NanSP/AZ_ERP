package com.example.backend.sd.faturas;

import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidoItens.PedidoItensRepository;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FaturasService {

    private final FaturasRepository repository;
    private final PedidosRepository pedidosRepository;
    private final PedidoItensRepository pedidoItensRepository;

    public FaturasService(
            FaturasRepository repository,
            PedidosRepository pedidosRepository,
            PedidoItensRepository pedidoItensRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
        this.pedidoItensRepository = pedidoItensRepository;
    }

    @Transactional
    public Faturas criar(FaturasRequestDTO data) {
        validar(data);
        validarNumeroFaturaDuplicadoParaCriacao(normalizarOpcional(data.numeroFatura()));

        Pedidos pedido = buscarPedido(data.pedido());

        Faturas entity = new Faturas();
        preencher(entity, data, pedido, LocalDateTime.now());

        Faturas saved = repository.save(entity);
        atualizarStatusPedidoParaFaturado(pedido);

        return saved;
    }

    @Transactional
    public Faturas atualizar(Integer id, FaturasRequestDTO data) {
        Faturas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fatura nao encontrada"));

        validar(data, entity);
        validarNumeroFaturaDuplicadoParaAtualizacao(normalizarOpcional(data.numeroFatura()), id);

        Pedidos pedidoAnterior = entity.getPedido();
        Pedidos pedido = buscarPedido(data.pedido());

        preencher(entity, data, pedido, entity.getCreatedAt());

        Faturas updated = repository.save(entity);

        if (pedidoAnterior != null && !pedidoAnterior.getId().equals(pedido.getId())) {
            recalcularStatusPedidoAposMudancaDeFatura(pedidoAnterior);
        }

        atualizarStatusPedidoParaFaturado(pedido);
        return updated;
    }

    @Transactional
    public void excluir(Integer id) {
        Faturas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fatura nao encontrada"));

        Pedidos pedido = entity.getPedido();

        repository.delete(entity);
        recalcularStatusPedidoAposMudancaDeFatura(pedido);
    }

    private void preencher(
            Faturas entity,
            FaturasRequestDTO data,
            Pedidos pedido,
            LocalDateTime createdAt
    ) {
        entity.setPedido(pedido);
        entity.setNumeroFatura(normalizarOpcional(data.numeroFatura()));
        entity.setDataEmissao(data.dataEmissao());
        entity.setValorTotal(zeroSeNulo(data.valorTotal()));
        entity.setDataVencimento(data.dataVencimento());
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(FaturasRequestDTO data) {
        validar(data, null);
    }

    private void validar(FaturasRequestDTO data, Faturas faturaAtual) {
        if (data == null) {
            throw new ValidacaoException("Dados da fatura sao obrigatorios");
        }

        if (data.numeroFatura() == null || data.numeroFatura().isBlank()) {
            throw new ValidacaoException("Numero da fatura e obrigatorio");
        }

        if (data.pedido() == null) {
            throw new ValidacaoException("Pedido e obrigatorio");
        }

        if (data.dataEmissao() == null) {
            throw new ValidacaoException("Data de emissao e obrigatoria");
        }

        if (data.valorTotal() == null || data.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor total da fatura deve ser maior que zero");
        }

        if (data.dataVencimento() != null && data.dataVencimento().isBefore(data.dataEmissao())) {
            throw new ValidacaoException("Data de vencimento nao pode ser anterior a data de emissao");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComDatas(status, data.dataVencimento());

        Pedidos pedido = buscarPedido(data.pedido());
        validarPedidoParaFaturamento(pedido, faturaAtual);
    }

    private void validarStatus(String status) {
        if (!status.equals("emitida")
                && !status.equals("paga")
                && !status.equals("vencida")
                && !status.equals("cancelada")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComDatas(String status, java.time.LocalDate dataVencimento) {
        if ((status.equals("paga") || status.equals("vencida")) && dataVencimento == null) {
            throw new ValidacaoException("Data de vencimento e obrigatoria quando a fatura estiver paga ou vencida");
        }
    }

    private void validarPedidoParaFaturamento(Pedidos pedido, Faturas faturaAtual) {
        if (pedido == null) {
            return;
        }

        if ("cancelado".equalsIgnoreCase(pedido.getStatus())) {
            throw new ValidacaoException("Nao e permitido faturar pedido cancelado");
        }

        if (faturaAtual == null && repository.existsByPedidoId(pedido.getId())) {
            throw new ValidacaoException("Pedido ja possui fatura vinculada");
        }
    }

    private void validarNumeroFaturaDuplicadoParaCriacao(String numeroFatura) {
        if (numeroFatura != null && repository.existsByNumeroFatura(numeroFatura)) {
            throw new ValidacaoException("Ja existe uma fatura com o numero informado");
        }
    }

    private void validarNumeroFaturaDuplicadoParaAtualizacao(String numeroFatura, Integer id) {
        if (numeroFatura != null && repository.existsByNumeroFaturaAndIdNot(numeroFatura, id)) {
            throw new ValidacaoException("Ja existe uma fatura com o numero informado");
        }
    }

    private Pedidos buscarPedido(Integer pedidoId) {
        return pedidosRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));
    }

    private void atualizarStatusPedidoParaFaturado(Pedidos pedido) {
        if (pedido != null && repository.existsByPedidoIdAndStatusNot(pedido.getId(), "cancelada")) {
            pedido.setStatus("faturado");
        }
    }

    private void recalcularStatusPedidoAposMudancaDeFatura(Pedidos pedido) {
        if (pedido == null || pedido.getId() == null) {
            return;
        }

        if (!repository.existsByPedidoIdAndStatusNot(pedido.getId(), "cancelada")) {
            if (pedidoItensRepository.existsByPedidoId(pedido.getId())) {
                pedido.setStatus("em_andamento");
            } else {
                pedido.setStatus("aberto");
            }
        }
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
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
        return valor == null ? "emitida" : valor.toLowerCase();
    }
}
