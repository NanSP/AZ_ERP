package com.example.backend.sd.clientes;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.oportunidades.OportunidadesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ClientesService {

    private final ClientesRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final OportunidadesRepository oportunidadesRepository;

    public ClientesService(
            ClientesRepository repository,
            ParceirosRepository parceirosRepository,
            OportunidadesRepository oportunidadesRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.oportunidadesRepository = oportunidadesRepository;
    }

    @Transactional
    public Clientes criar(ClientesRequestDTO data) {
        validar(data);
        validarParceiroDuplicadoParaCriacao(data.parceiro());

        Parceiros parceiro = buscarParceiro(data.parceiro());

        Clientes entity = new Clientes();
        preencher(entity, data, parceiro, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Clientes atualizar(Integer id, ClientesRequestDTO data) {
        validar(data);
        validarParceiroDuplicadoParaAtualizacao(data.parceiro(), id);

        Clientes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        Parceiros parceiro = buscarParceiro(data.parceiro());
        preencher(entity, data, parceiro, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Clientes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        if (oportunidadesRepository.existsByClienteId(id)) {
            throw new ValidacaoException("Nao e permitido excluir cliente que possui oportunidades vinculadas");
        }

        repository.delete(entity);
    }

    private void preencher(
            Clientes entity,
            ClientesRequestDTO data,
            Parceiros parceiro,
            LocalDateTime createdAt
    ) {
        entity.setParceiro(parceiro);
        entity.setClassificacao(normalizarClassificacao(data.classificacao()));
        entity.setOrigem(normalizarOpcional(data.origem()));
        entity.setWebsite(normalizarOpcional(data.website()));
        entity.setFaturamentoAnual(data.faturamentoAnual());
        entity.setNumeroFuncionarios(data.numeroFuncionarios());
        entity.setCreatedAt(createdAt);
    }

    private void validar(ClientesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do cliente sao obrigatorios");
        }

        if (data.parceiro() == null) {
            throw new ValidacaoException("Parceiro e obrigatorio");
        }

        if (data.faturamentoAnual() != null && data.faturamentoAnual().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Faturamento anual nao pode ser negativo");
        }

        if (data.numeroFuncionarios() != null && data.numeroFuncionarios() < 0) {
            throw new ValidacaoException("Numero de funcionarios nao pode ser negativo");
        }

        validarClassificacao(normalizarClassificacao(data.classificacao()));
    }

    private void validarClassificacao(String classificacao) {
        if (classificacao == null) {
            return;
        }

        if (!classificacao.equals("lead")
                && !classificacao.equals("prospect")
                && !classificacao.equals("cliente")) {
            throw new ValidacaoException("Classificacao invalida");
        }
    }

    private void validarParceiroDuplicadoParaCriacao(Integer parceiroId) {
        if (repository.existsByParceiroId(parceiroId)) {
            throw new ValidacaoException("Ja existe um cliente CRM vinculado ao parceiro informado");
        }
    }

    private void validarParceiroDuplicadoParaAtualizacao(Integer parceiroId, Integer id) {
        if (repository.existsByParceiroIdAndIdNot(parceiroId, id)) {
            throw new ValidacaoException("Ja existe um cliente CRM vinculado ao parceiro informado");
        }
    }

    private Parceiros buscarParceiro(Integer parceiroId) {
        return parceirosRepository.findById(parceiroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parceiro nao encontrado"));
    }

    private String normalizarClassificacao(String classificacao) {
        String valor = normalizarOpcional(classificacao);
        return valor == null ? "lead" : valor.toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
