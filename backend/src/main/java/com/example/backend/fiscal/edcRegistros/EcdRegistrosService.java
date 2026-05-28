package com.example.backend.fiscal.edcRegistros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EcdRegistrosService {

    private final EcdRegistrosRepository repository;

    public EcdRegistrosService(EcdRegistrosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EcdRegistros criar(EcdRegistrosRequestDTO data) {
        validar(data);

        EcdRegistros entity = new EcdRegistros();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public EcdRegistros atualizar(Long id, EcdRegistrosRequestDTO data) {
        validar(data);

        EcdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro ECD nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        EcdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro ECD nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            EcdRegistros entity,
            EcdRegistrosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setPeriodo(data.periodo());
        entity.setRegistro(normalizarRegistro(data.registro()));
        entity.setConteudo(data.conteudo());
        entity.setCreatedAt(createdAt);
    }

    private void validar(EcdRegistrosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do registro ECD sao obrigatorios");
        }

        if (data.periodo() == null) {
            throw new ValidacaoException("Periodo e obrigatorio");
        }

        validarRegistro(normalizarRegistro(data.registro()));
        validarConteudo(data.conteudo());
    }

    private void validarRegistro(String registro) {
        if (!registro.matches("[A-Z0-9]{4}")) {
            throw new ValidacaoException("Registro deve conter 4 caracteres alfanumericos em maiusculo");
        }
    }

    private void validarConteudo(Map<String, Object> conteudo) {
        if (conteudo == null || conteudo.isEmpty()) {
            throw new ValidacaoException("Conteudo do registro e obrigatorio");
        }
    }

    private void validarExclusao(EcdRegistros entity) {
        LocalDate inicioMesAtual = LocalDate.now().withDayOfMonth(1);

        if (entity.getPeriodo() != null && entity.getPeriodo().isBefore(inicioMesAtual)) {
            throw new ValidacaoException("Nao e permitido excluir registro ECD de periodo passado");
        }
    }

    private String normalizarRegistro(String registro) {
        if (registro == null || registro.isBlank()) {
            throw new ValidacaoException("Codigo do registro e obrigatorio");
        }

        return registro.trim().toUpperCase();
    }
}
