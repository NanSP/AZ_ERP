package com.example.backend.grc.controles;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ControlesService {

    private final ControlesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public ControlesService(
            ControlesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Controles criar(ControlesRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarOpcional(data.codigo()));

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        Controles entity = new Controles();
        preencher(entity, data, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Controles atualizar(Integer id, ControlesRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarOpcional(data.codigo()), id);

        Controles entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle nao encontrado"));

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        preencher(entity, data, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Controles entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Controles entity,
            ControlesRequestDTO data,
            Usuarios responsavel,
            LocalDateTime createdAt
    ) {
        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setDescricao(normalizarObrigatorio(data.descricao(), "Descricao do controle e obrigatoria"));
        entity.setTipoControle(normalizarTipoControle(data.tipoControle()));
        entity.setFrequencia(normalizarFrequencia(data.frequencia()));
        entity.setResponsavel(responsavel);
        entity.setEfetivo(data.efetivo());
        entity.setCreatedAt(createdAt);
    }

    private void validar(ControlesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do controle sao obrigatorios");
        }

        normalizarObrigatorio(data.descricao(), "Descricao do controle e obrigatoria");

        if (normalizarTipoControle(data.tipoControle()) == null) {
            throw new ValidacaoException("Tipo de controle e obrigatorio");
        }

        if (normalizarFrequencia(data.frequencia()) == null) {
            throw new ValidacaoException("Frequencia e obrigatoria");
        }

        validarTipoControle(normalizarTipoControle(data.tipoControle()));
        validarFrequencia(normalizarFrequencia(data.frequencia()));
        validarResponsabilizacao(data);
    }

    private void validarTipoControle(String tipoControle) {
        if (tipoControle == null) {
            return;
        }

        if (!tipoControle.equals("preventivo")
                && !tipoControle.equals("detectivo")
                && !tipoControle.equals("corretivo")) {
            throw new ValidacaoException("Tipo de controle invalido");
        }
    }

    private void validarFrequencia(String frequencia) {
        if (frequencia == null) {
            return;
        }

        if (!frequencia.equals("diaria")
                && !frequencia.equals("semanal")
                && !frequencia.equals("mensal")
                && !frequencia.equals("trimestral")
                && !frequencia.equals("anual")
                && !frequencia.equals("sob_demanda")) {
            throw new ValidacaoException("Frequencia invalida");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe controle com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe controle com o codigo informado");
        }
    }

    private Usuarios buscarResponsavelOpcional(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String normalizarTipoControle(String tipoControle) {
        String valor = normalizarOpcional(tipoControle);
        return valor == null ? null : valor.toLowerCase();
    }

    private String normalizarFrequencia(String frequencia) {
        String valor = normalizarOpcional(frequencia);
        return valor == null ? null : valor.toLowerCase();
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

    private void validarExclusao(Controles entity) {
        if (normalizarOpcional(entity.getCodigo()) != null) {
            throw new ValidacaoException("Nao e permitido excluir controle formalizado com codigo");
        }
    }

    private void validarResponsabilizacao(ControlesRequestDTO data) {
        String codigo = normalizarOpcional(data.codigo());

        if (codigo != null && data.responsavel() == null) {
            throw new ValidacaoException("Controle formalizado com codigo deve possuir responsavel");
        }

        if (Boolean.TRUE.equals(data.efetivo()) && data.responsavel() == null) {
            throw new ValidacaoException("Controle efetivo deve possuir responsavel");
        }
    }
}
