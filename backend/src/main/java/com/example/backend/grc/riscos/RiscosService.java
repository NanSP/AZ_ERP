package com.example.backend.grc.riscos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RiscosService {

    private final RiscosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public RiscosService(
            RiscosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Riscos criar(RiscosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarOpcional(data.codigo()));

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        Riscos entity = new Riscos();
        preencher(entity, data, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Riscos atualizar(Integer id, RiscosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarOpcional(data.codigo()), id);

        Riscos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Risco nao encontrado"));

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        preencher(entity, data, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Riscos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Risco nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Riscos entity,
            RiscosRequestDTO data,
            Usuarios responsavel,
            LocalDateTime createdAt
    ) {
        Integer probabilidade = data.probabilidade();
        Integer impacto = data.impacto();

        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo do risco e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setCategoria(normalizarOpcional(data.categoria()));
        entity.setProbabilidade(probabilidade);
        entity.setImpacto(impacto);
        entity.setNivelRisco(calcularNivelRisco(probabilidade, impacto));
        entity.setResponsavel(responsavel);
        entity.setPlanoMitigacao(normalizarOpcional(data.planoMitigacao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(RiscosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do risco sao obrigatorios");
        }

        normalizarObrigatorio(data.titulo(), "Titulo do risco e obrigatorio");

        if (data.probabilidade() == null || data.impacto() == null) {
            throw new ValidacaoException("Probabilidade e impacto sao obrigatorios");
        }

        validarEscala(data.probabilidade(), "Probabilidade deve estar entre 1 e 5");
        validarEscala(data.impacto(), "Impacto deve estar entre 1 e 5");
        validarResponsabilizacao(data);
    }

    private void validarEscala(Integer valor, String mensagem) {
        if (valor == null) {
            return;
        }

        if (valor < 1 || valor > 5) {
            throw new ValidacaoException(mensagem);
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe risco com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe risco com o codigo informado");
        }
    }

    private Usuarios buscarResponsavelOpcional(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String calcularNivelRisco(Integer probabilidade, Integer impacto) {
        if (probabilidade == null || impacto == null) {
            return null;
        }

        int score = probabilidade * impacto;

        if (score <= 5) {
            return "baixo";
        }

        if (score <= 14) {
            return "medio";
        }

        return "alto";
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }

    private void validarExclusao(Riscos entity) {
        if (entity.getNivelRisco() != null
                || entity.getProbabilidade() != null
                || entity.getImpacto() != null
                || normalizarOpcional(entity.getPlanoMitigacao()) != null) {
            throw new ValidacaoException("Nao e permitido excluir risco ja avaliado ou com plano de mitigacao");
        }
    }

    private void validarResponsabilizacao(RiscosRequestDTO data) {
        String nivelRisco = calcularNivelRisco(data.probabilidade(), data.impacto());
        String planoMitigacao = normalizarOpcional(data.planoMitigacao());

        if ("alto".equals(nivelRisco)) {
            if (data.responsavel() == null) {
                throw new ValidacaoException("Risco alto deve possuir responsavel");
            }

            if (planoMitigacao == null) {
                throw new ValidacaoException("Risco alto deve possuir plano de mitigacao");
            }
        }

        if ("medio".equals(nivelRisco) && planoMitigacao != null && data.responsavel() == null) {
            throw new ValidacaoException("Plano de mitigacao exige responsavel para o risco");
        }
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
