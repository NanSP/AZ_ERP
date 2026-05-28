package com.example.backend.fiscal.efdRegistros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EfdRegistrosService {

    private final EfdRegistrosRepository repository;

    public EfdRegistrosService(EfdRegistrosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EfdRegistros criar(EfdRegistrosRequestDTO data) {
        validar(data);

        EfdRegistros entity = new EfdRegistros();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public EfdRegistros atualizar(Long id, EfdRegistrosRequestDTO data) {
        validar(data);

        EfdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro EFD nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        EfdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro EFD nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            EfdRegistros entity,
            EfdRegistrosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setPeriodo(data.periodo());
        entity.setRegistro(normalizarRegistro(data.registro()));
        entity.setConteudo(data.conteudo());
        entity.setCreatedAt(createdAt);
    }

    private void validar(EfdRegistrosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do registro EFD sao obrigatorios");
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

    private String normalizarRegistro(String registro) {
        if (registro == null || registro.isBlank()) {
            throw new ValidacaoException("Codigo do registro e obrigatorio");
        }

        return registro.trim().toUpperCase();
    }

    private void validarExclusao(EfdRegistros entity) {
        LocalDate inicioMesAtual = LocalDate.now().withDayOfMonth(1);

        if (entity.getPeriodo() != null && !entity.getPeriodo().isAfter(inicioMesAtual)) {
            throw new ValidacaoException("Nao e permitido excluir registro EFD de periodo atual ou passado");
        }
    }
}
