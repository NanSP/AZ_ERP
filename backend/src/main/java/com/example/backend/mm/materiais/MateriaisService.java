package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MateriaisService {

    private final MateriaisRepository repository;
    private final ProdutosRepository produtosRepository;
    private final EstoquesRepository estoquesRepository;

    public MateriaisService(
            MateriaisRepository repository,
            ProdutosRepository produtosRepository,
            EstoquesRepository estoquesRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.estoquesRepository = estoquesRepository;
    }

    @Transactional
    public Materiais criar(MateriaisRequestDTO data) {
        validar(data);
        validarDuplicidadeProdutoParaCriacao(data.produto());

        Produtos produto = buscarProduto(data.produto());

        Materiais entity = new Materiais();
        preencher(entity, data, produto, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Materiais atualizar(Integer id, MateriaisRequestDTO data) {
        validar(data);
        validarDuplicidadeProdutoParaAtualizacao(data.produto(), id);

        Materiais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Material nao encontrado"));

        Produtos produto = buscarProduto(data.produto());
        preencher(entity, data, produto, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Materiais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Material nao encontrado"));

        if (entity.getProduto() != null
                && entity.getProduto().getId() != null
                && estoquesRepository.existsByProdutoId(entity.getProduto().getId())) {
            throw new ValidacaoException("Nao e permitido excluir material com estoque vinculado ao produto");
        }

        repository.delete(entity);
    }

    private void preencher(
            Materiais entity,
            MateriaisRequestDTO data,
            Produtos produto,
            LocalDateTime createdAt
    ) {
        entity.setProduto(produto);
        entity.setTipoMaterial(normalizarTipoMaterial(data.tipoMaterial()));
        entity.setCategoria(normalizarOpcional(data.categoria()));
        entity.setSubcategoria(normalizarOpcional(data.subcategoria()));
        entity.setMarca(normalizarOpcional(data.marca()));
        entity.setModelo(normalizarOpcional(data.modelo()));
        entity.setEspecificacoesTecnicas(normalizarOpcional(data.especificacoesTecnicas()));
        entity.setCondicaoArmazenamento(normalizarOpcional(data.condicaoArmazenamento()));
        entity.setClassePerigo(normalizarOpcional(data.classePerigo()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(MateriaisRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do material sao obrigatorios");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        validarTipoMaterial(normalizarTipoMaterial(data.tipoMaterial()));
    }

    private void validarTipoMaterial(String tipoMaterial) {
        if (tipoMaterial == null) {
            throw new ValidacaoException("Tipo de material e obrigatorio");
        }

        if (!tipoMaterial.equals("materia_prima")
                && !tipoMaterial.equals("embalagem")
                && !tipoMaterial.equals("consumo")
                && !tipoMaterial.equals("componente")
                && !tipoMaterial.equals("outro")) {
            throw new ValidacaoException("Tipo de material invalido");
        }
    }

    private Produtos buscarProduto(Integer produtoId) {
        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    private void validarDuplicidadeProdutoParaCriacao(Integer produtoId) {
        if (repository.existsByProdutoId(produtoId)) {
            throw new ValidacaoException("Ja existe cadastro de material para o produto informado");
        }
    }

    private void validarDuplicidadeProdutoParaAtualizacao(Integer produtoId, Integer id) {
        if (repository.existsByProdutoIdAndIdNot(produtoId, id)) {
            throw new ValidacaoException("Ja existe cadastro de material para o produto informado");
        }
    }

    private String normalizarTipoMaterial(String tipoMaterial) {
        String valor = normalizarOpcional(tipoMaterial);
        return valor == null ? null : valor.toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
