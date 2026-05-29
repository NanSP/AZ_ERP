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

        validarAtualizacaoComMovimentacoes(entity, data);

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
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        String situacao = produto.getSituacao() == null ? null : produto.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Produto precisa estar ativo para uso em estoque");
        }

        String tipoItem = produto.getTipoItem() == null ? null : produto.getTipoItem().trim().toLowerCase();
        if ("servico".equals(tipoItem)) {
            throw new ValidacaoException("Produto do tipo servico nao pode gerar estoque");
        }

        return produto;
    }

    private Empresas buscarEmpresa(Integer empresaId) {
        Empresas empresa = empresasRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));

        String situacao = empresa.getSituacao() == null ? null : empresa.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Empresa precisa estar ativa para uso em estoque");
        }

        return empresa;
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

    private void validarAtualizacaoComMovimentacoes(Estoques entity, EstoquesRequestDTO data) {
        if (entity.getId() == null || !movimentacoesRepository.existsByEstoqueId(entity.getId())) {
            return;
        }

        if (!entity.getProduto().getId().equals(data.produto())
                || !entity.getEmpresa().getId().equals(data.empresa())
                || !normalizarOpcional(entity.getLocalizacao()).equals(normalizarOpcional(data.localizacao()))
                || !normalizarOpcional(entity.getLote()).equals(normalizarOpcional(data.lote()))) {
            throw new ValidacaoException("Nao e permitido alterar identificacao do estoque apos existir movimentacao");
        }

        BigDecimal quantidadeAtual = entity.getQuantidade();
        BigDecimal novaQuantidade = data.quantidade();
        if (quantidadeAtual != null || novaQuantidade != null) {
            if (quantidadeAtual == null || novaQuantidade == null || quantidadeAtual.compareTo(novaQuantidade) != 0) {
                throw new ValidacaoException("Nao e permitido alterar quantidade diretamente apos existir movimentacao");
            }
        }

        BigDecimal valorUnitarioAtual = entity.getValorUnitario();
        BigDecimal novoValorUnitario = data.valorUnitario();
        if (valorUnitarioAtual != null || novoValorUnitario != null) {
            if (valorUnitarioAtual == null
                    || novoValorUnitario == null
                    || valorUnitarioAtual.compareTo(novoValorUnitario) != 0) {
                throw new ValidacaoException("Nao e permitido alterar valor unitario diretamente apos existir movimentacao");
            }
        }
    }
}
