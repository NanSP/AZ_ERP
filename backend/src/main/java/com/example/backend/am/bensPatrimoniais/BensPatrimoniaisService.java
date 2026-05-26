package com.example.backend.am.bensPatrimoniais;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BensPatrimoniaisService {

    private final BensPatrimoniaisRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public BensPatrimoniaisService(
            BensPatrimoniaisRepository repository,
            ParceirosRepository parceirosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public BensPatrimoniais criar(BensPatrimoniaisRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarObrigatorio(data.codigoPatrimonio(), "Codigo patrimonial e obrigatorio"));

        Parceiros fornecedor = buscarFornecedor(data.fornecedor());
        Colaboradores responsavel = buscarResponsavel(data.responsavel());

        BensPatrimoniais entity = new BensPatrimoniais();
        preencher(entity, data, fornecedor, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public BensPatrimoniais atualizar(Integer id, BensPatrimoniaisRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(
                normalizarObrigatorio(data.codigoPatrimonio(), "Codigo patrimonial e obrigatorio"),
                id
        );

        BensPatrimoniais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bem patrimonial nao encontrado"));

        Parceiros fornecedor = buscarFornecedor(data.fornecedor());
        Colaboradores responsavel = buscarResponsavel(data.responsavel());

        preencher(entity, data, fornecedor, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        BensPatrimoniais entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bem patrimonial nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            BensPatrimoniais entity,
            BensPatrimoniaisRequestDTO data,
            Parceiros fornecedor,
            Colaboradores responsavel,
            LocalDateTime createdAt
    ) {
        entity.setCodigoPatrimonio(normalizarObrigatorio(data.codigoPatrimonio(), "Codigo patrimonial e obrigatorio"));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do bem patrimonial e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setTipoAtivo(normalizarOpcional(data.tipoAtivo()));
        entity.setLocalizacao(normalizarOpcional(data.localizacao()));
        entity.setDataAquisicao(data.dataAquisicao());
        entity.setValorAquisicao(zeroSeNulo(data.valorAquisicao()));
        entity.setValorAtual(zeroSeNulo(data.valorAtual()));
        entity.setVidaUtilAnos(data.vidaUtilAnos());
        entity.setTaxaDepreciacao(zeroSeNulo(data.taxaDepreciacao()));
        entity.setDataDepreciacao(data.dataDepreciacao());
        entity.setFornecedor(fornecedor);
        entity.setResponsavel(responsavel);
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(BensPatrimoniaisRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do bem patrimonial sao obrigatorios");
        }

        normalizarObrigatorio(data.codigoPatrimonio(), "Codigo patrimonial e obrigatorio");
        normalizarObrigatorio(data.nome(), "Nome do bem patrimonial e obrigatorio");

        validarNaoNegativo(data.valorAquisicao(), "Valor de aquisicao nao pode ser negativo");
        validarNaoNegativo(data.valorAtual(), "Valor atual nao pode ser negativo");
        validarNaoNegativo(data.taxaDepreciacao(), "Taxa de depreciacao nao pode ser negativa");

        if (data.vidaUtilAnos() != null && data.vidaUtilAnos() < 0) {
            throw new ValidacaoException("Vida util nao pode ser negativa");
        }

        if (data.valorAquisicao() != null
                && data.valorAtual() != null
                && data.valorAtual().compareTo(data.valorAquisicao()) > 0) {
            throw new ValidacaoException("Valor atual nao pode ser maior que o valor de aquisicao");
        }

        if (data.dataAquisicao() != null
                && data.dataDepreciacao() != null
                && data.dataDepreciacao().isBefore(data.dataAquisicao())) {
            throw new ValidacaoException("Data de depreciacao nao pode ser anterior a data de aquisicao");
        }

        validarStatus(normalizarStatus(data.status()));
    }

    private void validarStatus(String status) {
        if (!status.equals("ativo")
                && !status.equals("inativo")
                && !status.equals("baixado")
                && !status.equals("manutencao")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigoPatrimonio) {
        if (repository.existsByCodigoPatrimonio(codigoPatrimonio)) {
            throw new ValidacaoException("Ja existe um bem patrimonial com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigoPatrimonio, Integer id) {
        if (repository.existsByCodigoPatrimonioAndIdNot(codigoPatrimonio, id)) {
            throw new ValidacaoException("Ja existe um bem patrimonial com o codigo informado");
        }
    }

    private Parceiros buscarFornecedor(Integer fornecedorId) {
        if (fornecedorId == null) {
            return null;
        }

        return parceirosRepository.findById(fornecedorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor nao encontrado"));
    }

    private Colaboradores buscarResponsavel(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return colaboradoresRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
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

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "ativo" : valor.toLowerCase();
    }
}