package com.example.backend.fi.centrosCusto;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CentrosCustoService {

    private final CentrosCustoRepository repository;

    public CentrosCustoService(CentrosCustoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CentrosCusto criar(CentrosCustoRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(data.codigo().trim());

        CentrosCusto entity = new CentrosCusto();
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public CentrosCusto atualizar(Integer id, CentrosCustoRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(data.codigo().trim(), id);

        CentrosCusto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo nao encontrado"));

        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        CentrosCusto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(CentrosCusto entity, CentrosCustoRequestDTO data) {
        entity.setCodigo(data.codigo().trim());
        entity.setNome(data.nome().trim());
        entity.setTipo(normalizarOpcional(data.tipo()));
        entity.setResponsavel(normalizarOpcional(data.responsavel()));
        entity.setAtivo(data.ativo() != null ? data.ativo() : true);
    }

    private void validar(CentrosCustoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do centro de custo sao obrigatorios");
        }

        if (data.codigo() == null || data.codigo().isBlank()) {
            throw new ValidacaoException("Codigo e obrigatorio");
        }

        if (data.nome() == null || data.nome().isBlank()) {
            throw new ValidacaoException("Nome e obrigatorio");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe um centro de custo com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe um centro de custo com o codigo informado");
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