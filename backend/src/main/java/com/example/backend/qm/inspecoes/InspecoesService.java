package com.example.backend.qm.inspecoes;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InspecoesService {

    private final InspecoesRepository repository;
    private final ProdutosRepository produtosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public InspecoesService(
            InspecoesRepository repository,
            ProdutosRepository produtosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Inspecoes criar(InspecoesRequestDTO data) {
        validar(data);

        Produtos produto = buscarProduto(data.produto());
        Colaboradores inspetor = buscarInspetor(data.inspetor());

        Inspecoes entity = new Inspecoes();
        preencher(entity, data, produto, inspetor, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Inspecoes atualizar(Integer id, InspecoesRequestDTO data) {
        validar(data);

        Inspecoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inspecao nao encontrada"));

        Produtos produto = buscarProduto(data.produto());
        Colaboradores inspetor = buscarInspetor(data.inspetor());

        preencher(entity, data, produto, inspetor, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Inspecoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inspecao nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            Inspecoes entity,
            InspecoesRequestDTO data,
            Produtos produto,
            Colaboradores inspetor,
            LocalDateTime createdAt
    ) {
        entity.setTipoInspecao(normalizar(data.tipoInspecao()));
        entity.setProduto(produto);
        entity.setLote(normalizarOpcional(data.lote()));
        entity.setQuantidadeInspecionada(data.quantidadeInspecionada());
        entity.setQuantidadeAprovada(zeroSeNulo(data.quantidadeAprovada()));
        entity.setQuantidadeReprovada(zeroSeNulo(data.quantidadeReprovada()));
        entity.setDataInspecao(data.dataInspecao());
        entity.setInspetor(inspetor);
        entity.setResultado(normalizar(data.resultado()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(InspecoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da inspecao sao obrigatorios");
        }

        if (data.tipoInspecao() == null || data.tipoInspecao().isBlank()) {
            throw new ValidacaoException("Tipo de inspecao e obrigatorio");
        }

        String tipoInspecao = normalizar(data.tipoInspecao());
        if (!tipoInspecao.equals("recebimento")
                && !tipoInspecao.equals("processo")
                && !tipoInspecao.equals("final")
                && !tipoInspecao.equals("expedicao")) {
            throw new ValidacaoException("Tipo de inspecao invalido");
        }

        if (data.dataInspecao() == null) {
            throw new ValidacaoException("Data da inspecao e obrigatoria");
        }

        if (data.resultado() == null || data.resultado().isBlank()) {
            throw new ValidacaoException("Resultado e obrigatorio");
        }

        String resultado = normalizar(data.resultado());
        if (!resultado.equals("aprovado")
                && !resultado.equals("reprovado")
                && !resultado.equals("em_analise")) {
            throw new ValidacaoException("Resultado invalido");
        }

        if (data.quantidadeInspecionada() == null
                || data.quantidadeInspecionada().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade inspecionada deve ser informada e nao pode ser negativa");
        }

        BigDecimal quantidadeAprovada = zeroSeNulo(data.quantidadeAprovada());
        BigDecimal quantidadeReprovada = zeroSeNulo(data.quantidadeReprovada());

        if (quantidadeAprovada.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade aprovada nao pode ser negativa");
        }

        if (quantidadeReprovada.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade reprovada nao pode ser negativa");
        }

        validarQuantidadesPorResultado(
                resultado,
                data.quantidadeInspecionada(),
                quantidadeAprovada,
                quantidadeReprovada
        );
    }

    private void validarQuantidadesPorResultado(
            String resultado,
            BigDecimal quantidadeInspecionada,
            BigDecimal quantidadeAprovada,
            BigDecimal quantidadeReprovada
    ) {
        BigDecimal quantidadeAnalisada = quantidadeAprovada.add(quantidadeReprovada);

        if (resultado.equals("em_analise")) {
            validarResultadoEmAnalise(quantidadeInspecionada, quantidadeAnalisada);
            return;
        }

        if (quantidadeAnalisada.compareTo(quantidadeInspecionada) != 0) {
            throw new ValidacaoException("A soma de aprovadas e reprovadas deve ser igual a quantidade inspecionada");
        }
    }

    private void validarResultadoEmAnalise(
            BigDecimal quantidadeInspecionada,
            BigDecimal quantidadeAnalisada
    ) {
        if (quantidadeAnalisada.compareTo(quantidadeInspecionada) > 0) {
            throw new ValidacaoException(
                    "Em analise, a soma de aprovadas e reprovadas nao pode ser maior que a quantidade inspecionada"
            );
        }

        if (quantidadeAnalisada.compareTo(quantidadeInspecionada) == 0) {
            throw new ValidacaoException(
                    "Em analise, a soma de aprovadas e reprovadas deve ser menor que a quantidade inspecionada"
            );
        }
    }

    private Produtos buscarProduto(Integer produtoId) {
        if (produtoId == null) {
            return null;
        }

        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    private Colaboradores buscarInspetor(Integer inspetorId) {
        if (inspetorId == null) {
            return null;
        }

        return colaboradoresRepository.findById(inspetorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inspetor nao encontrado"));
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
