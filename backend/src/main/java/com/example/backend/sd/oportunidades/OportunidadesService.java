package com.example.backend.sd.oportunidades;

import com.example.backend.sd.clientes.Clientes;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OportunidadesService {

    private final OportunidadesRepository repository;
    private final ClientesRepository clientesRepository;
    private final UsuariosRepository usuariosRepository;

    public OportunidadesService(
            OportunidadesRepository repository,
            ClientesRepository clientesRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.clientesRepository = clientesRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Oportunidades criar(OportunidadesRequestDTO data) {
        validar(data);

        Clientes cliente = buscarCliente(data.cliente());
        Usuarios responsavel = buscarResponsavel(data.responsavel());

        Oportunidades entity = new Oportunidades();
        preencher(entity, data, cliente, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Oportunidades atualizar(Integer id, OportunidadesRequestDTO data) {
        validar(data);

        Oportunidades entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade nao encontrada"));

        Clientes cliente = buscarCliente(data.cliente());
        Usuarios responsavel = buscarResponsavel(data.responsavel());

        preencher(entity, data, cliente, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Oportunidades entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            Oportunidades entity,
            OportunidadesRequestDTO data,
            Clientes cliente,
            Usuarios responsavel,
            LocalDateTime createdAt
    ) {
        entity.setCliente(cliente);
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo da oportunidade e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setValorEstimado(data.valorEstimado());
        entity.setProbabilidade(normalizarProbabilidade(data.probabilidade()));
        entity.setEstagio(normalizarEstagio(data.estagio()));
        entity.setDataPrevistaFechamento(data.dataPrevistaFechamento());
        entity.setMotivoPerda(normalizarOpcional(data.motivoPerda()));
        entity.setResponsavel(responsavel);
        entity.setCreatedAt(createdAt);
    }

    private void validar(OportunidadesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da oportunidade sao obrigatorios");
        }

        if (data.cliente() == null) {
            throw new ValidacaoException("Cliente e obrigatorio");
        }

        if (data.responsavel() == null) {
            throw new ValidacaoException("Responsavel e obrigatorio");
        }

        normalizarObrigatorio(data.titulo(), "Titulo da oportunidade e obrigatorio");

        validarNaoNegativo(data.valorEstimado(), "Valor estimado nao pode ser negativo");

        Integer probabilidade = normalizarProbabilidade(data.probabilidade());
        if (probabilidade < 0 || probabilidade > 100) {
            throw new ValidacaoException("Probabilidade deve estar entre 0 e 100");
        }

        String estagio = normalizarEstagio(data.estagio());
        validarEstagio(estagio);
        validarEstagioComDataPrevistaFechamento(estagio, data.dataPrevistaFechamento());
        validarEstagioComMotivoPerda(estagio, data.motivoPerda());
    }

    private void validarEstagio(String estagio) {
        if (!estagio.equals("prospeccao")
                && !estagio.equals("qualificacao")
                && !estagio.equals("proposta")
                && !estagio.equals("negociacao")
                && !estagio.equals("fechado_ganho")
                && !estagio.equals("fechado_perdido")) {
            throw new ValidacaoException("Estagio invalido");
        }
    }

    private void validarEstagioComMotivoPerda(String estagio, String motivoPerda) {
        String motivo = normalizarOpcional(motivoPerda);

        if (estagio.equals("fechado_perdido") && motivo == null) {
            throw new ValidacaoException("Motivo da perda e obrigatorio quando a oportunidade estiver perdida");
        }

        if (!estagio.equals("fechado_perdido") && motivo != null) {
            throw new ValidacaoException("Motivo da perda so deve ser informado para oportunidade perdida");
        }
    }

    private void validarEstagioComDataPrevistaFechamento(String estagio, java.time.LocalDate dataPrevistaFechamento) {
        boolean exigeDataPrevista = estagio.equals("proposta")
                || estagio.equals("negociacao")
                || estagio.equals("fechado_ganho")
                || estagio.equals("fechado_perdido");

        if (exigeDataPrevista && dataPrevistaFechamento == null) {
            throw new ValidacaoException("Data prevista de fechamento e obrigatoria para o estagio informado");
        }
    }

    private Clientes buscarCliente(Integer clienteId) {
        return clientesRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private Usuarios buscarResponsavel(Integer responsavelId) {
        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private Integer normalizarProbabilidade(Integer probabilidade) {
        return probabilidade == null ? 50 : probabilidade;
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

    private String normalizarEstagio(String estagio) {
        String valor = normalizarOpcional(estagio);
        return valor == null ? "prospeccao" : valor.toLowerCase();
    }
}
