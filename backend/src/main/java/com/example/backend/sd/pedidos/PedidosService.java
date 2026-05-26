package com.example.backend.sd.pedidos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.sd.faturas.FaturasRepository;
import com.example.backend.sd.pedidoItens.PedidoItensRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PedidosService {

    private final PedidosRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ClientesRepository clientesRepository;
    private final PedidoItensRepository pedidoItensRepository;
    private final FaturasRepository faturasRepository;

    public PedidosService(
            PedidosRepository repository,
            ParceirosRepository parceirosRepository,
            ClientesRepository clientesRepository,
            PedidoItensRepository pedidoItensRepository,
            FaturasRepository faturasRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.clientesRepository = clientesRepository;
        this.pedidoItensRepository = pedidoItensRepository;
        this.faturasRepository = faturasRepository;
    }

    @Transactional
    public Pedidos criar(PedidosRequestDTO data) {
        validar(data, null);
        validarNumeroPedidoDuplicadoParaCriacao(normalizarOpcional(data.numeroPedido()));

        Parceiros cliente = buscarCliente(data.cliente());

        Pedidos entity = new Pedidos();
        preencher(entity, data, cliente, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Pedidos atualizar(Integer id, PedidosRequestDTO data) {
        Pedidos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));

        validar(data, entity);
        validarNumeroPedidoDuplicadoParaAtualizacao(normalizarOpcional(data.numeroPedido()), id);

        Parceiros cliente = buscarCliente(data.cliente());
        preencher(entity, data, cliente, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Pedidos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));

        if (pedidoItensRepository.existsByPedidoId(id)) {
            throw new ValidacaoException("Nao e permitido excluir pedido que possui itens");
        }

        if (faturasRepository.existsByPedidoId(id)) {
            throw new ValidacaoException("Nao e permitido excluir pedido que possui faturas");
        }

        repository.delete(entity);
    }

    private void preencher(
            Pedidos entity,
            PedidosRequestDTO data,
            Parceiros cliente,
            LocalDateTime createdAt
    ) {
        entity.setCliente(cliente);
        entity.setNumeroPedido(normalizarOpcional(data.numeroPedido()));
        entity.setDataPedido(data.dataPedido());
        entity.setDataEntrega(data.dataEntrega());
        entity.setValorTotal(calcularValorTotalPedido(entity));
        entity.setDescontoTotal(calcularDescontoTotalPedido(entity));
        entity.setCondicoesPagamento(normalizarOpcional(data.condicoesPagamento()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(PedidosRequestDTO data, Pedidos pedidoAtual) {
        if (data == null) {
            throw new ValidacaoException("Dados do pedido sao obrigatorios");
        }

        if (data.cliente() == null) {
            throw new ValidacaoException("Cliente e obrigatorio");
        }

        if (data.dataPedido() == null) {
            throw new ValidacaoException("Data do pedido e obrigatoria");
        }

        validarClienteComercial(data.cliente());
        validarNaoNegativo(data.valorTotal(), "Valor total nao pode ser negativo");
        validarNaoNegativo(data.descontoTotal(), "Desconto total nao pode ser negativo");

        if (data.dataEntrega() != null && data.dataEntrega().isBefore(data.dataPedido())) {
            throw new ValidacaoException("Data de entrega nao pode ser anterior a data do pedido");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComWorkflow(status, pedidoAtual);
    }

    private void validarStatus(String status) {
        if (!status.equals("aberto")
                && !status.equals("em_andamento")
                && !status.equals("faturado")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComWorkflow(String status, Pedidos pedidoAtual) {
        Integer pedidoId = pedidoAtual != null ? pedidoAtual.getId() : null;
        boolean possuiFaturas = pedidoId != null && faturasRepository.existsByPedidoId(pedidoId);

        if (status.equals("faturado") && !possuiFaturas) {
            throw new ValidacaoException("Pedido so pode ser faturado quando possuir faturas");
        }

        if (status.equals("cancelado") && possuiFaturas) {
            throw new ValidacaoException("Pedido com faturas nao pode ser cancelado");
        }
    }

    private void validarNumeroPedidoDuplicadoParaCriacao(String numeroPedido) {
        if (numeroPedido != null && repository.existsByNumeroPedido(numeroPedido)) {
            throw new ValidacaoException("Ja existe um pedido com o numero informado");
        }
    }

    private void validarNumeroPedidoDuplicadoParaAtualizacao(String numeroPedido, Integer id) {
        if (numeroPedido != null && repository.existsByNumeroPedidoAndIdNot(numeroPedido, id)) {
            throw new ValidacaoException("Ja existe um pedido com o numero informado");
        }
    }

    private Parceiros buscarCliente(Integer clienteId) {
        return parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private void validarClienteComercial(Integer clienteId) {
        if (!clientesRepository.existsByParceiroId(clienteId)) {
            throw new ValidacaoException("Cliente informado precisa estar cadastrado no CRM");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal calcularValorTotalPedido(Pedidos pedido) {
        if (pedido.getId() == null) {
            return BigDecimal.ZERO;
        }

        return pedidoItensRepository.sumValorTotalByPedidoId(pedido.getId());
    }

    private BigDecimal calcularDescontoTotalPedido(Pedidos pedido) {
        if (pedido.getId() == null) {
            return BigDecimal.ZERO;
        }

        return pedidoItensRepository.sumDescontoByPedidoId(pedido.getId());
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
