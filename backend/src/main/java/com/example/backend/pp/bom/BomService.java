package com.example.backend.pp.bom;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class BomService {

    private final BomRepository repository;
    private final ProdutosRepository produtosRepository;

    public BomService(
            BomRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public Bom criar(BomRequestDTO data) {
        validar(data);

        Produtos produtoPai = buscarProduto(data.produtoPai(), "Produto pai nao encontrado");
        Produtos componente = buscarProduto(data.componente(), "Componente nao encontrado");

        validarRelacionamento(produtoPai, componente, null);

        Bom entity = new Bom();
        preencher(entity, data, produtoPai, componente, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Bom atualizar(Integer id, BomRequestDTO data) {
        validar(data);

        Bom entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("BOM nao encontrado"));

        Produtos produtoPai = buscarProduto(data.produtoPai(), "Produto pai nao encontrado");
        Produtos componente = buscarProduto(data.componente(), "Componente nao encontrado");

        validarRelacionamento(produtoPai, componente, id);

        preencher(entity, data, produtoPai, componente, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Bom entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("BOM nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            Bom entity,
            BomRequestDTO data,
            Produtos produtoPai,
            Produtos componente,
            LocalDateTime createdAt
    ) {
        entity.setProdutoPai(produtoPai);
        entity.setComponente(componente);
        entity.setQuantidade(data.quantidade());
        entity.setUnidadeMedida(normalizarOpcional(data.unidadeMedida()));
        entity.setNivel(data.nivel());
        entity.setTempoPreparacao(data.tempoPreparacao());
        entity.setTempoProducao(data.tempoProducao());
        entity.setRoteiro(data.roteiro());
        entity.setCreatedAt(createdAt);
    }

    private void validar(BomRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da BOM sao obrigatorios");
        }

        if (data.produtoPai() == null) {
            throw new ValidacaoException("Produto pai e obrigatorio");
        }

        if (data.componente() == null) {
            throw new ValidacaoException("Componente e obrigatorio");
        }

        if (data.quantidade() == null || data.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Quantidade deve ser maior que zero");
        }

        if (data.nivel() != null && data.nivel() < 0) {
            throw new ValidacaoException("Nivel nao pode ser negativo");
        }

        if (data.tempoPreparacao() != null && data.tempoPreparacao().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Tempo de preparacao nao pode ser negativo");
        }

        if (data.tempoProducao() != null && data.tempoProducao().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Tempo de producao nao pode ser negativo");
        }
    }

    private Produtos buscarProduto(Integer produtoId, String mensagemNaoEncontrado) {
        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(mensagemNaoEncontrado));
    }

    private void validarRelacionamento(Produtos produtoPai, Produtos componente, Integer bomId) {
        if (produtoPai.getId().equals(componente.getId())) {
            throw new ValidacaoException("Produto pai e componente nao podem ser o mesmo produto");
        }

        validarDuplicidade(produtoPai.getId(), componente.getId(), bomId);
        validarCicloHierarquico(produtoPai.getId(), componente.getId());
    }

    private void validarDuplicidade(Integer produtoPaiId, Integer componenteId, Integer bomId) {
        boolean duplicado = bomId == null
                ? repository.existsByProdutoPaiIdAndComponenteId(produtoPaiId, componenteId)
                : repository.existsByProdutoPaiIdAndComponenteIdAndIdNot(produtoPaiId, componenteId, bomId);

        if (duplicado) {
            throw new ValidacaoException("Ja existe composicao BOM para o produto pai e componente informados");
        }
    }

    private void validarCicloHierarquico(Integer produtoPaiId, Integer componenteId) {
        if (formaCiclo(produtoPaiId, componenteId, new HashSet<>())) {
            throw new ValidacaoException("Nao e permitido criar ciclo na estrutura da BOM");
        }
    }

    private boolean formaCiclo(Integer produtoPaiId, Integer componenteId, Set<Integer> visitados) {
        if (!visitados.add(componenteId)) {
            return false;
        }

        for (Bom estruturaFilha : repository.findByProdutoPaiId(componenteId)) {
            Produtos proximoComponente = estruturaFilha.getComponente();
            if (proximoComponente == null) {
                continue;
            }

            Integer proximoComponenteId = proximoComponente.getId();
            if (produtoPaiId.equals(proximoComponenteId)) {
                return true;
            }

            if (formaCiclo(produtoPaiId, proximoComponenteId, visitados)) {
                return true;
            }
        }

        return false;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
