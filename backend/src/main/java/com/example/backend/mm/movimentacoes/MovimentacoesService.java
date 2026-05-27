package com.example.backend.mm.movimentacoes;

import com.example.backend.mm.estoques.Estoques;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.RegraNegocioException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MovimentacoesService {

    private final MovimentacoesRepository repository;
    private final EstoquesRepository estoquesRepository;
    private final UsuariosRepository usuariosRepository;

    public MovimentacoesService(
            MovimentacoesRepository repository,
            EstoquesRepository estoquesRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.estoquesRepository = estoquesRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Movimentacoes criar(MovimentacoesRequestDTO data) {
        validar(data);

        Estoques estoque = buscarEstoque(data.estoque());
        Usuarios usuario = buscarUsuario(data.usuario());

        aplicarMovimento(estoque, data.tipoMovimento(), data.quantidade());

        Movimentacoes entity = new Movimentacoes();
        preencher(entity, data, estoque, usuario, LocalDateTime.now());
        estoquesRepository.save(estoque);

        return repository.save(entity);
    }

    @Transactional
    public Movimentacoes atualizar(Integer id, MovimentacoesRequestDTO data) {
        validar(data);

        Movimentacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao nao encontrada"));

        Estoques estoqueAnterior = entity.getEstoque();
        desfazerMovimento(estoqueAnterior, entity);

        Estoques novoEstoque = buscarEstoque(data.estoque());
        Usuarios usuario = buscarUsuario(data.usuario());

        aplicarMovimento(novoEstoque, data.tipoMovimento(), data.quantidade());
        preencher(entity, data, novoEstoque, usuario, entity.getCreatedAt());

        estoquesRepository.save(estoqueAnterior);

        if (!estoqueAnterior.getId().equals(novoEstoque.getId())) {
            estoquesRepository.save(novoEstoque);
        }

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Movimentacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentacao nao encontrada"));

        Estoques estoque = entity.getEstoque();
        desfazerMovimento(estoque, entity);

        estoquesRepository.save(estoque);
        repository.delete(entity);
    }

    private void preencher(
            Movimentacoes entity,
            MovimentacoesRequestDTO data,
            Estoques estoque,
            Usuarios usuario,
            LocalDateTime createdAt
    ) {
        entity.setEstoque(estoque);
        entity.setTipoMovimento(normalizarTipoMovimento(data.tipoMovimento()));
        entity.setQuantidade(data.quantidade());
        entity.setValorUnitario(data.valorUnitario());
        entity.setValorTotal(calcularValorTotal(data.quantidade(), data.valorUnitario()));
        entity.setDocumentoReferencia(data.documentoReferencia());
        entity.setMotivo(data.motivo());
        entity.setUsuario(usuario);
        entity.setCreatedAt(createdAt);
    }

    private void validar(MovimentacoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da movimentacao sao obrigatorios");
        }

        if (data.estoque() == null) {
            throw new ValidacaoException("Estoque e obrigatorio");
        }

        if (data.usuario() == null) {
            throw new ValidacaoException("Usuario e obrigatorio");
        }

        if (data.tipoMovimento() == null || data.tipoMovimento().isBlank()) {
            throw new ValidacaoException("Tipo de movimento e obrigatorio");
        }

        String tipo = normalizarTipoMovimento(data.tipoMovimento());
        if (!tipo.equals("entrada")
                && !tipo.equals("saida")
                && !tipo.equals("transferencia")
                && !tipo.equals("ajuste")
                && !tipo.equals("inventario")) {
            throw new ValidacaoException("Tipo de movimento invalido");
        }

        if (data.quantidade() == null || data.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Quantidade deve ser maior que zero");
        }

        if (data.valorUnitario() != null && data.valorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Valor unitario nao pode ser negativo");
        }
    }

    private Estoques buscarEstoque(Integer estoqueId) {
        return estoquesRepository.findById(estoqueId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque nao encontrado"));
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }

        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private void aplicarMovimento(Estoques estoque, String tipoMovimento, BigDecimal quantidade) {
        BigDecimal saldoAtual = nvl(estoque.getQuantidade());
        String tipo = normalizarTipoMovimento(tipoMovimento);

        if (tipo.equals("entrada") || tipo.equals("ajuste") || tipo.equals("inventario")) {
            estoque.setQuantidade(saldoAtual.add(quantidade));
            return;
        }

        if (saldoAtual.compareTo(quantidade) < 0) {
            throw new RegraNegocioException("Saldo insuficiente em estoque");
        }

        estoque.setQuantidade(saldoAtual.subtract(quantidade));
    }

    private void desfazerMovimento(Estoques estoque, Movimentacoes movimentacao) {
        BigDecimal saldoAtual = nvl(estoque.getQuantidade());
        BigDecimal quantidade = nvl(movimentacao.getQuantidade());
        String tipo = normalizarTipoMovimento(movimentacao.getTipoMovimento());

        if (tipo.equals("entrada") || tipo.equals("ajuste") || tipo.equals("inventario")) {
            if (saldoAtual.compareTo(quantidade) < 0) {
                throw new RegraNegocioException("Nao foi possivel desfazer movimentacao de entrada");
            }
            estoque.setQuantidade(saldoAtual.subtract(quantidade));
            return;
        }

        estoque.setQuantidade(saldoAtual.add(quantidade));
    }

    private BigDecimal calcularValorTotal(
            BigDecimal quantidade,
            BigDecimal valorUnitario
    ) {
        if (valorUnitario == null) {
            return null;
        }

        return quantidade.multiply(valorUnitario);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String normalizarTipoMovimento(String tipoMovimento) {
        return tipoMovimento == null ? "" : tipoMovimento.trim().toLowerCase();
    }
}
