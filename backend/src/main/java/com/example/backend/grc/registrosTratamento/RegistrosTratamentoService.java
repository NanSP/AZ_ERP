package com.example.backend.grc.registrosTratamento;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrosTratamentoService {

    private final RegistrosTratamentoRepository repository;
    private final UsuariosRepository usuariosRepository;

    public RegistrosTratamentoService(
            RegistrosTratamentoRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public RegistrosTratamento criar(RegistrosTratamentoRequestDTO data) {
        validar(data);
        validarDuplicidadeCriacao(data);

        RegistrosTratamento entity = new RegistrosTratamento();
        preencher(entity, data, LocalDateTime.now());
        return repository.save(entity);
    }

    @Transactional
    public RegistrosTratamento atualizar(Integer id, RegistrosTratamentoRequestDTO data) {
        validar(data);
        validarDuplicidadeAtualizacao(data, id);

        RegistrosTratamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de tratamento nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        RegistrosTratamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de tratamento nao encontrado"));

        if (Boolean.TRUE.equals(entity.getAtivo())) {
            throw new ValidacaoException("Nao e permitido excluir registro de tratamento ativo");
        }

        repository.delete(entity);
    }

    private void preencher(RegistrosTratamento entity, RegistrosTratamentoRequestDTO data, LocalDateTime createdAt) {
        entity.setModulo(normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio"));
        entity.setEntidade(normalizarObrigatorio(data.entidade(), "Entidade e obrigatoria"));
        entity.setFinalidade(normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria"));
        entity.setBaseLegal(normalizarBaseLegal(data.baseLegal()));
        entity.setCategoriaTitular(normalizarCategoriaTitular(data.categoriaTitular()));
        entity.setCategoriaDados(normalizarCategoriaDados(data.categoriaDados()));
        entity.setRetencaoDias(normalizarRetencaoDias(data.retencaoDias()));
        entity.setCompartilhamento(normalizarOpcional(data.compartilhamento()));
        entity.setRequerConsentimento(Boolean.TRUE.equals(data.requerConsentimento()));
        entity.setAtivo(data.ativo() == null || data.ativo());
        entity.setResponsavel(buscarResponsavelOpcional(data.responsavel()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(RegistrosTratamentoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do registro de tratamento sao obrigatorios");
        }

        String baseLegal = normalizarBaseLegal(data.baseLegal());

        normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio");
        normalizarObrigatorio(data.entidade(), "Entidade e obrigatoria");
        normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria");
        normalizarCategoriaTitular(data.categoriaTitular());
        normalizarCategoriaDados(data.categoriaDados());
        normalizarRetencaoDias(data.retencaoDias());

        if ("consentimento".equals(baseLegal) && !Boolean.TRUE.equals(data.requerConsentimento())) {
            throw new ValidacaoException("Base legal consentimento exige marcador de consentimento");
        }
    }

    private void validarDuplicidadeCriacao(RegistrosTratamentoRequestDTO data) {
        if (repository.existsByModuloAndEntidadeAndFinalidade(
                normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio"),
                normalizarObrigatorio(data.entidade(), "Entidade e obrigatoria"),
                normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria")
        )) {
            throw new ValidacaoException("Ja existe registro de tratamento para modulo, entidade e finalidade informados");
        }
    }

    private void validarDuplicidadeAtualizacao(RegistrosTratamentoRequestDTO data, Integer id) {
        if (repository.existsByModuloAndEntidadeAndFinalidadeAndIdNot(
                normalizarObrigatorio(data.modulo(), "Modulo e obrigatorio"),
                normalizarObrigatorio(data.entidade(), "Entidade e obrigatoria"),
                normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria"),
                id
        )) {
            throw new ValidacaoException("Ja existe registro de tratamento para modulo, entidade e finalidade informados");
        }
    }

    private Usuarios buscarResponsavelOpcional(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String normalizarBaseLegal(String value) {
        String normalized = normalizarObrigatorio(value, "Base legal e obrigatoria").toLowerCase();

        if (!normalized.equals("consentimento")
                && !normalized.equals("execucao_contrato")
                && !normalized.equals("obrigacao_legal")
                && !normalized.equals("legitimo_interesse")
                && !normalized.equals("exercicio_regular_direitos")
                && !normalized.equals("protecao_credito")) {
            throw new ValidacaoException("Base legal invalida");
        }

        return normalized;
    }

    private String normalizarCategoriaTitular(String value) {
        String normalized = normalizarObrigatorio(value, "Categoria do titular e obrigatoria").toLowerCase();

        if (!normalized.equals("cliente")
                && !normalized.equals("colaborador")
                && !normalized.equals("fornecedor")
                && !normalized.equals("usuario")
                && !normalized.equals("visitante")
                && !normalized.equals("outro")) {
            throw new ValidacaoException("Categoria do titular invalida");
        }

        return normalized;
    }

    private String normalizarCategoriaDados(String value) {
        String normalized = normalizarObrigatorio(value, "Categoria dos dados e obrigatoria").toLowerCase();

        if (!normalized.equals("cadastral")
                && !normalized.equals("contato")
                && !normalized.equals("financeiro")
                && !normalized.equals("acesso")
                && !normalized.equals("trabalhista")
                && !normalized.equals("fiscal")
                && !normalized.equals("sensivel")
                && !normalized.equals("operacional")) {
            throw new ValidacaoException("Categoria dos dados invalida");
        }

        return normalized;
    }

    private Integer normalizarRetencaoDias(Integer value) {
        if (value == null) {
            return null;
        }

        if (value < 0 || value > 36500) {
            throw new ValidacaoException("Retencao em dias invalida");
        }

        return value;
    }

    private String normalizarObrigatorio(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidacaoException(message);
        }

        return value.trim();
    }

    private String normalizarOpcional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
