package com.example.backend.mm.estoques;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.movimentacoes.MovimentacoesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EstoquesService {

    private final EstoquesRepository repository;
    private final ProdutosRepository produtosRepository;
    private final EmpresasRepository empresasRepository;
    private final MovimentacoesRepository movimentacoesRepository;

    public EstoquesService(
            EstoquesRepository repository,
            ProdutosRepository produtosRepository,
            EmpresasRepository empresasRepository,
            MovimentacoesRepository movimentacoesRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.empresasRepository = empresasRepository;
        this.movimentacoesRepository = movimentacoesRepository;
    }

    @Transactional
    public Estoques criar(EstoquesRequestDTO data) {
        validar(data);
        validarDuplicidadeParaCriacao(data);

        Produtos produto = buscarProduto(data.produto());
        Empresas empresa = buscarEmpresa(data.empresa());

        Estoques entity = new Estoques();
        preencher(entity, data, produto, empresa, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Estoques atualizar(Integer id, EstoquesRequestDTO data) {
        validar(data);
        validarDuplicidadeParaAtualizacao(data, id);

        Estoques entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque nao encontrado"));

        Produtos produto = buscarProduto(data.produto());
        Empresas empresa = buscarEmpresa(data.empresa());

        preencher(entity, data, produto, empresa, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Estoques entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque nao encontrado"));

        if (movimentacoesRepository.existsByEstoqueId(id)) {
            throw new ValidacaoException("Nao e permitido excluir estoque que possui movimentacoes");
        }

        repository.delete(entity);
    }

    private void preencher(
            Estoques entity,
            EstoquesRequestDTO data,
            Produtos produto,
            Empresas empresa,
            LocalDateTime createdAt
    ) {
        entity.setProduto(produto);
        entity.setEmpresa(empresa);
        entity.setLocalizacao(normalizarOpcional(data.localizacao()));
        entity.setLote(normalizarOpcional(data.lote()));
        entity.setQuantidade(data.quantidade());
        entity.setQuantidadeMinima(data.quantidadeMinima());
        entity.setQuantidadeMaxima(data.quantidadeMaxima());
        entity.setValorUnitario(data.valorUnitario());
        entity.setDataValidade(data.dataValidade());
        entity.setCreatedAt(createdAt);
    }

    private void validar(EstoquesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do estoque sao obrigatorios");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.empresa() == null) {
            throw new ValidacaoException("Empresa e obrigatoria");
        }

        validarNaoNegativo(data.quantidade(), "Quantidade nao pode ser negativa");
        validarNaoNegativo(data.quantidadeMinima(), "Quantidade minima nao pode ser negativa");
        validarNaoNegativo(data.quantidadeMaxima(), "Quantidade maxima nao pode ser negativa");
        validarNaoNegativo(data.valorUnitario(), "Valor unitario nao pode ser negativo");

        BigDecimal quantidadeMinima = zeroSeNulo(data.quantidadeMinima());
        BigDecimal quantidadeMaxima = zeroSeNulo(data.quantidadeMaxima());

        if (quantidadeMaxima.compareTo(BigDecimal.ZERO) > 0
                && quantidadeMinima.compareTo(quantidadeMaxima) > 0) {
            throw new ValidacaoException("Quantidade minima nao pode ser maior que a quantidade maxima");
        }
    }

    private void validarDuplicidadeParaCriacao(EstoquesRequestDTO data) {
        if (repository.existsByProdutoIdAndEmpresaIdAndLocalizacaoAndLote(
                data.produto(),
                data.empresa(),
                normalizarOpcional(data.localizacao()),
                normalizarOpcional(data.lote())
        )) {
            throw new ValidacaoException("Ja existe estoque para a combinacao de produto, empresa, localizacao e lote informados");
        }
    }

    private void validarDuplicidadeParaAtualizacao(EstoquesRequestDTO data, Integer id) {
        if (repository.existsByProdutoIdAndEmpresaIdAndLocalizacaoAndLoteAndIdNot(
                data.produto(),
                data.empresa(),
                normalizarOpcional(data.localizacao()),
                normalizarOpcional(data.lote()),
                id
        )) {
            throw new ValidacaoException("Ja existe estoque para a combinacao de produto, empresa, localizacao e lote informados");
        }
    }

    private Produtos buscarProduto(Integer produtoId) {
        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    private Empresas buscarEmpresa(Integer empresaId) {
        return empresasRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
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
}
