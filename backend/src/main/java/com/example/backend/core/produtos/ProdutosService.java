package com.example.backend.core.produtos;

import com.example.backend.mm.compraItens.CompraItensRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.mm.materiais.MateriaisRepository;
import com.example.backend.pp.bom.BomRepository;
import com.example.backend.qm.inspecoes.InspecoesRepository;
import com.example.backend.sd.pedidoItens.PedidoItensRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ProdutosService {

    private final ProdutosRepository repository;
    private final EstoquesRepository estoquesRepository;
    private final CompraItensRepository compraItensRepository;
    private final PedidoItensRepository pedidoItensRepository;
    private final OrdensServicoRepository ordensServicoRepository;
    private final InspecoesRepository inspecoesRepository;
    private final MateriaisRepository materiaisRepository;
    private final BomRepository bomRepository;

    public ProdutosService(
            ProdutosRepository repository,
            EstoquesRepository estoquesRepository,
            CompraItensRepository compraItensRepository,
            PedidoItensRepository pedidoItensRepository,
            OrdensServicoRepository ordensServicoRepository,
            InspecoesRepository inspecoesRepository,
            MateriaisRepository materiaisRepository,
            BomRepository bomRepository
    ) {
        this.repository = repository;
        this.estoquesRepository = estoquesRepository;
        this.compraItensRepository = compraItensRepository;
        this.pedidoItensRepository = pedidoItensRepository;
        this.ordensServicoRepository = ordensServicoRepository;
        this.inspecoesRepository = inspecoesRepository;
        this.materiaisRepository = materiaisRepository;
        this.bomRepository = bomRepository;
    }

    @Transactional
    public Produtos criar(ProdutosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarObrigatorio(data.codigo(), "Codigo do produto e obrigatorio"));

        Produtos entity = new Produtos();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Produtos atualizar(Integer id, ProdutosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarObrigatorio(data.codigo(), "Codigo do produto e obrigatorio"), id);

        Produtos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        validarAlteracoesSensveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Produtos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Produtos entity,
            ProdutosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setCodigo(normalizarObrigatorio(data.codigo(), "Codigo do produto e obrigatorio"));
        entity.setCodigoBarras(normalizarOpcional(data.codigoBarras()));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do produto e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setTipoItem(normalizarTipoItem(data.tipoItem()));
        entity.setUnidadeMedida(normalizarOpcional(data.unidadeMedida()));
        entity.setNcm(normalizarOpcional(data.ncm()));
        entity.setCest(normalizarOpcional(data.cest()));
        entity.setPesoBruto(data.pesoBruto());
        entity.setPesoLiquido(data.pesoLiquido());
        entity.setOrigem(normalizarOrigem(data.origem()));
        entity.setSituacao(normalizarSituacao(data.situacao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ProdutosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do produto sao obrigatorios");
        }

        normalizarObrigatorio(data.codigo(), "Codigo do produto e obrigatorio");
        normalizarObrigatorio(data.nome(), "Nome do produto e obrigatorio");

        validarTipoItem(normalizarTipoItem(data.tipoItem()));
        validarOrigem(normalizarOrigem(data.origem()));
        validarSituacao(normalizarSituacao(data.situacao()));
        validarNaoNegativo(data.pesoBruto(), "Peso bruto nao pode ser negativo");
        validarNaoNegativo(data.pesoLiquido(), "Peso liquido nao pode ser negativo");

        if (data.ncm() != null && !data.ncm().isBlank() && !data.ncm().trim().matches("\\d{8}")) {
            throw new ValidacaoException("NCM deve conter 8 digitos numericos");
        }

        if (data.cest() != null && !data.cest().isBlank() && !data.cest().trim().matches("\\d{7}")) {
            throw new ValidacaoException("CEST deve conter 7 digitos numericos");
        }
    }

    private void validarTipoItem(String tipoItem) {
        if (!tipoItem.equals("produto")
                && !tipoItem.equals("servico")
                && !tipoItem.equals("insumo")
                && !tipoItem.equals("embalagem")) {
            throw new ValidacaoException("Tipo de item invalido");
        }
    }

    private void validarOrigem(Integer origem) {
        if (origem < 0 || origem > 8) {
            throw new ValidacaoException("Origem invalida");
        }
    }

    private void validarSituacao(String situacao) {
        if (!situacao.equals("ativo")
                && !situacao.equals("inativo")
                && !situacao.equals("bloqueado")) {
            throw new ValidacaoException("Situacao invalida");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe produto com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe produto com o codigo informado");
        }
    }

    private void validarExclusao(Produtos entity) {
        if (produtoEmUso(entity.getId())) {
            throw new ValidacaoException("Nao e permitido excluir produto com uso operacional");
        }
    }

    private void validarAlteracoesSensveis(Produtos entity, ProdutosRequestDTO data) {
        if (!produtoEmUso(entity.getId())) {
            return;
        }

        String novoTipoItem = normalizarTipoItem(data.tipoItem());
        String novaUnidadeMedida = normalizarOpcional(data.unidadeMedida());
        Integer novaOrigem = normalizarOrigem(data.origem());

        if (!novoTipoItem.equals(normalizarTipoItem(entity.getTipoItem()))) {
            throw new ValidacaoException("Nao e permitido alterar o tipo do produto que ja possui uso operacional");
        }

        if (!mesmoTexto(novaUnidadeMedida, normalizarOpcional(entity.getUnidadeMedida()))) {
            throw new ValidacaoException("Nao e permitido alterar a unidade de medida do produto que ja possui uso operacional");
        }

        if (!novaOrigem.equals(normalizarOrigem(entity.getOrigem()))) {
            throw new ValidacaoException("Nao e permitido alterar a origem do produto que ja possui uso operacional");
        }
    }

    private boolean produtoEmUso(Integer produtoId) {
        return estoquesRepository.existsByProdutoId(produtoId)
                || compraItensRepository.existsByProdutosId(produtoId)
                || pedidoItensRepository.existsByProdutoId(produtoId)
                || ordensServicoRepository.existsByProdutoId(produtoId)
                || inspecoesRepository.existsByProdutoId(produtoId)
                || materiaisRepository.existsByProdutoId(produtoId)
                || bomRepository.existsByProdutoPaiId(produtoId)
                || bomRepository.existsByComponenteId(produtoId);
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private Integer normalizarOrigem(Integer origem) {
        return origem == null ? 0 : origem;
    }

    private String normalizarTipoItem(String tipoItem) {
        String valor = normalizarOpcional(tipoItem);
        return valor == null ? "produto" : valor.toLowerCase();
    }

    private String normalizarSituacao(String situacao) {
        String valor = normalizarOpcional(situacao);
        return valor == null ? "ativo" : valor.toLowerCase();
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private boolean mesmoTexto(String valor1, String valor2) {
        if (valor1 == null) {
            return valor2 == null;
        }

        return valor1.equals(valor2);
    }
}
