package com.example.backend.mm.movimentacoes;

import com.example.backend.mm.estoques.Estoques;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public Movimentacoes criar(MovimentacoesRequestDTO data) {
        validar(data);

        Estoques estoque = buscarEstoque(data.estoque());
        Usuarios usuario = buscarUsuario(data.usuario());

        Movimentacoes entity = new Movimentacoes();
        preencher(entity, data, estoque, usuario);

        return repository.save(entity);
    }

    public Movimentacoes atualizar(Integer id, MovimentacoesRequestDTO data) {
        validar(data);

        Movimentacoes entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentacao nao encontrada"));

        Estoques estoque = buscarEstoque(data.estoque());
        Usuarios usuario = buscarUsuario(data.usuario());

        preencher(entity, data, estoque, usuario);

        return repository.save(entity);
    }

    private Estoques buscarEstoque(Integer estoqueId) {
        return estoqueId != null
                ? estoquesRepository.findById(estoqueId)
                .orElseThrow(() -> new RuntimeException("Estoque nao encontrado"))
                : null;
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        return usuarioId != null
                ? usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                : null;
    }

    private void preencher(
            Movimentacoes entity,
            MovimentacoesRequestDTO data,
            Estoques estoque,
            Usuarios usuario
    ) {
        entity.setEstoque(estoque);
        entity.setTipoMovimento(data.tipoMovimento());
        entity.setQuantidade(data.quantidade());
        entity.setValorUnitario(data.valorUnitario());
        entity.setValorTotal(calcularValorTotal(data.quantidade(), data.valorUnitario(), data.valorTotal()));
        entity.setDocumentoReferencia(data.documentoReferencia());
        entity.setMotivo(data.motivo());
        entity.setUsuario(usuario);
        entity.setCreatedAt(data.createdAt());
    }

    private void validar(MovimentacoesRequestDTO data) {
        if (data.estoque() == null) {
            throw new RuntimeException("Estoque e obrigatorio");
        }

        if (data.tipoMovimento() == null || data.tipoMovimento().isBlank()) {
            throw new RuntimeException("Tipo de movimento e obrigatorio");
        }

        if (data.quantidade() == null || data.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        if (data.valorUnitario() != null && data.valorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Valor unitario nao pode ser negativo");
        }

        if (!data.tipoMovimento().equalsIgnoreCase("entrada")
                && !data.tipoMovimento().equalsIgnoreCase("saida")) {
            throw new RuntimeException("Tipo de movimento invalido");
        }
    }

    private BigDecimal calcularValorTotal(
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotalInformado
    ) {
        if (valorTotalInformado != null) {
            return valorTotalInformado;
        }

        if (quantidade != null && valorUnitario != null) {
            return quantidade.multiply(valorUnitario);
        }

        return null;
    }
}