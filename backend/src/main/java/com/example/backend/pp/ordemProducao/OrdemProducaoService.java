package com.example.backend.pp.ordemProducao;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.pp.apontamentos.ApontamentosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrdemProducaoService {

    private final OrdemProducaoRepository repository;
    private final ProdutosRepository produtosRepository;
    private final ApontamentosRepository apontamentosRepository;

    public OrdemProducaoService(
            OrdemProducaoRepository repository,
            ProdutosRepository produtosRepository,
            ApontamentosRepository apontamentosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.apontamentosRepository = apontamentosRepository;
    }

    @Transactional
    public OrdemProducao criar(OrdemProducaoRequestDTO data) {
        validar(data);
        validarNumeroOpDuplicadoParaCriacao(normalizarOpcional(data.numeroOp()));

        Produtos produto = buscarProduto(data.produto());

        OrdemProducao entity = new OrdemProducao();
        preencher(entity, data, produto, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public OrdemProducao atualizar(Integer id, OrdemProducaoRequestDTO data) {
        validar(data);
        validarNumeroOpDuplicadoParaAtualizacao(normalizarOpcional(data.numeroOp()), id);

        OrdemProducao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de producao nao encontrada"));

        Produtos produto = buscarProduto(data.produto());
        preencher(entity, data, produto, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        OrdemProducao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de producao nao encontrada"));

        if (apontamentosRepository.existsByOpId(id)) {
            throw new ValidacaoException("Nao e permitido excluir ordem de producao que possui apontamentos");
        }

        repository.delete(entity);
    }

    private void preencher(
            OrdemProducao entity,
            OrdemProducaoRequestDTO data,
            Produtos produto,
            LocalDateTime createdAt
    ) {
        entity.setNumeroOp(normalizarOpcional(data.numeroOp()));
        entity.setProduto(produto);
        entity.setQuantidadePlanejada(data.quantidadePlanejada());
        entity.setQuantidadeProduzida(zeroSeNulo(data.quantidadeProduzida()));
        entity.setDataEmissao(data.dataEmissao());
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setDataPrevista(data.dataPrevista());
        entity.setStatus(normalizarStatus(data.status()));
        entity.setPrioridade(normalizarPrioridade(data.prioridade()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(OrdemProducaoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da ordem de producao sao obrigatorios");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.quantidadePlanejada() == null || data.quantidadePlanejada().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Quantidade planejada deve ser maior que zero");
        }

        if (data.quantidadeProduzida() != null && data.quantidadeProduzida().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade produzida nao pode ser negativa");
        }

        if (data.quantidadeProduzida() != null
                && data.quantidadeProduzida().compareTo(data.quantidadePlanejada()) > 0) {
            throw new ValidacaoException("Quantidade produzida nao pode ser maior que a quantidade planejada");
        }

        if (data.dataEmissao() == null) {
            throw new ValidacaoException("Data de emissao e obrigatoria");
        }

        if (data.dataInicio() != null && data.dataInicio().isBefore(data.dataEmissao())) {
            throw new ValidacaoException("Data de inicio nao pode ser anterior a data de emissao");
        }

        if (data.dataFim() != null && data.dataInicio() != null && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        if (data.dataPrevista() != null && data.dataPrevista().isBefore(data.dataEmissao())) {
            throw new ValidacaoException("Data prevista nao pode ser anterior a data de emissao");
        }

        validarStatusComQuantidades(
                normalizarStatus(data.status()),
                data.quantidadePlanejada(),
                zeroSeNulo(data.quantidadeProduzida())
        );
        validarPrioridade(normalizarPrioridade(data.prioridade()));
    }

    private void validarStatusComQuantidades(
            String status,
            BigDecimal quantidadePlanejada,
            BigDecimal quantidadeProduzida
    ) {
        if (!status.equals("planejada")
                && !status.equals("em_producao")
                && !status.equals("concluida")
                && !status.equals("cancelada")) {
            throw new ValidacaoException("Status invalido");
        }

        if (status.equals("planejada") && quantidadeProduzida.compareTo(BigDecimal.ZERO) > 0) {
            throw new ValidacaoException("Ordem planejada nao pode ter quantidade produzida informada");
        }

        if (status.equals("concluida") && quantidadeProduzida.compareTo(quantidadePlanejada) != 0) {
            throw new ValidacaoException("Ordem concluida deve ter quantidade produzida igual a quantidade planejada");
        }

        if (status.equals("em_producao")) {
            if (quantidadeProduzida.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidacaoException("Ordem em producao deve ter quantidade produzida maior que zero");
            }

            if (quantidadeProduzida.compareTo(quantidadePlanejada) >= 0) {
                throw new ValidacaoException(
                        "Ordem em producao deve ter quantidade produzida menor que a quantidade planejada"
                );
            }
        }
    }

    private void validarPrioridade(Integer prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new ValidacaoException("Prioridade invalida");
        }
    }

    private Produtos buscarProduto(Integer produtoId) {
        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    private void validarNumeroOpDuplicadoParaCriacao(String numeroOp) {
        if (numeroOp != null && repository.existsByNumeroOp(numeroOp)) {
            throw new ValidacaoException("Ja existe uma ordem de producao com o numero informado");
        }
    }

    private void validarNumeroOpDuplicadoParaAtualizacao(String numeroOp, Integer id) {
        if (numeroOp != null && repository.existsByNumeroOpAndIdNot(numeroOp, id)) {
            throw new ValidacaoException("Ja existe uma ordem de producao com o numero informado");
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
        return valor == null ? "planejada" : valor.toLowerCase();
    }

    private Integer normalizarPrioridade(Integer prioridade) {
        return prioridade == null ? 1 : prioridade;
    }
}
