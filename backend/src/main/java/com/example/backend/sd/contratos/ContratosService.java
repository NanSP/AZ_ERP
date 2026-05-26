package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ContratosService {

    private final ContratosRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ClientesRepository clientesRepository;

    public ContratosService(
            ContratosRepository repository,
            ParceirosRepository parceirosRepository,
            ClientesRepository clientesRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.clientesRepository = clientesRepository;
    }

    @Transactional
    public Contratos criar(ContratosRequestDTO data) {
        validar(data);
        validarNumeroContratoDuplicadoParaCriacao(normalizarObrigatorio(data.numeroContrato(), "Numero do contrato e obrigatorio"));

        Parceiros cliente = buscarCliente(data.cliente());

        Contratos entity = new Contratos();
        preencher(entity, data, cliente, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Contratos atualizar(Integer id, ContratosRequestDTO data) {
        validar(data);
        validarNumeroContratoDuplicadoParaAtualizacao(
                normalizarObrigatorio(data.numeroContrato(), "Numero do contrato e obrigatorio"),
                id
        );

        Contratos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contrato nao encontrado"));

        Parceiros cliente = buscarCliente(data.cliente());
        preencher(entity, data, cliente, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Contratos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contrato nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            Contratos entity,
            ContratosRequestDTO data,
            Parceiros cliente,
            LocalDateTime createdAt
    ) {
        entity.setCliente(cliente);
        entity.setNumeroContrato(normalizarObrigatorio(data.numeroContrato(), "Numero do contrato e obrigatorio"));
        entity.setObjeto(normalizarOpcional(data.objeto()));
        entity.setValorTotal(data.valorTotal());
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ContratosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do contrato sao obrigatorios");
        }

        normalizarObrigatorio(data.numeroContrato(), "Numero do contrato e obrigatorio");

        if (data.cliente() == null) {
            throw new ValidacaoException("Cliente e obrigatorio");
        }

        validarClienteComercial(data.cliente());
        validarNaoNegativo(data.valorTotal(), "Valor total nao pode ser negativo");

        if (data.dataInicio() != null
                && data.dataFim() != null
                && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComDatas(status, data.dataInicio(), data.dataFim());
    }

    private void validarStatus(String status) {
        if (!status.equals("vigente")
                && !status.equals("encerrado")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComDatas(String status, LocalDate dataInicio, LocalDate dataFim) {
        if ((status.equals("encerrado") || status.equals("cancelado")) && dataFim == null) {
            throw new ValidacaoException("Data fim e obrigatoria para contrato encerrado ou cancelado");
        }

        if (status.equals("vigente") && dataInicio == null) {
            throw new ValidacaoException("Data inicio e obrigatoria para contrato vigente");
        }
    }

    private void validarNumeroContratoDuplicadoParaCriacao(String numeroContrato) {
        if (numeroContrato != null && repository.existsByNumeroContrato(numeroContrato)) {
            throw new ValidacaoException("Ja existe um contrato com o numero informado");
        }
    }

    private void validarNumeroContratoDuplicadoParaAtualizacao(String numeroContrato, Integer id) {
        if (numeroContrato != null && repository.existsByNumeroContratoAndIdNot(numeroContrato, id)) {
            throw new ValidacaoException("Ja existe um contrato com o numero informado");
        }
    }

    private Parceiros buscarCliente(Integer clienteId) {
        return parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private void validarClienteComercial(Integer clienteId) {
        if (!clientesRepository.existsByParceiroId(clienteId)) {
            throw new ValidacaoException("Cliente informado precisa estar cadastrado no CRM");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "vigente" : valor.toLowerCase();
    }
}
