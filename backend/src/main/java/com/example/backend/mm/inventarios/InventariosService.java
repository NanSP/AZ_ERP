package com.example.backend.mm.inventarios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class InventariosService {

    private final InventariosRepository repository;

    public InventariosService(InventariosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Inventarios criar(InventariosRequestDTO data) {
        validar(data);
        validarRelacionamentosParaCriacao(data);

        Inventarios entity = new Inventarios();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Inventarios atualizar(Integer id, InventariosRequestDTO data) {
        validar(data);
        validarRelacionamentosParaAtualizacao(data, id);

        Inventarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inventario nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Inventarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inventario nao encontrado"));

        if ("em_andamento".equalsIgnoreCase(entity.getStatus())) {
            throw new ValidacaoException("Nao e permitido excluir inventario em andamento");
        }

        repository.delete(entity);
    }

    private void preencher(
            Inventarios entity,
            InventariosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setTipoInventario(normalizarTipoInventario(data.tipoInventario()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(InventariosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do inventario sao obrigatorios");
        }

        if (data.dataInicio() == null) {
            throw new ValidacaoException("Data de inicio e obrigatoria");
        }

        if (data.dataFim() != null && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data de inicio");
        }

        String tipoInventario = normalizarTipoInventario(data.tipoInventario());
        validarTipoInventario(tipoInventario);

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComDatas(status, data.dataInicio(), data.dataFim());
    }

    private void validarTipoInventario(String tipoInventario) {
        if (!tipoInventario.equals("anual")
                && !tipoInventario.equals("rotativo")
                && !tipoInventario.equals("amostragem")) {
            throw new ValidacaoException("Tipo de inventario invalido");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("planejado")
                && !status.equals("em_andamento")
                && !status.equals("concluido")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComDatas(String status, LocalDate dataInicio, LocalDate dataFim) {
        if ((status.equals("concluido") || status.equals("cancelado")) && dataFim == null) {
            throw new ValidacaoException("Data fim e obrigatoria para inventario concluido ou cancelado");
        }

        if (status.equals("planejado") && dataFim != null) {
            throw new ValidacaoException("Inventario planejado nao deve ter data fim informada");
        }

        if (status.equals("em_andamento") && dataInicio != null && dataInicio.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Inventario em andamento nao pode iniciar no futuro");
        }

        if (status.equals("concluido") && dataFim != null && dataFim.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Inventario concluido nao pode ter data fim no futuro");
        }
    }

    private String normalizarTipoInventario(String tipoInventario) {
        if (tipoInventario == null || tipoInventario.isBlank()) {
            throw new ValidacaoException("Tipo de inventario e obrigatorio");
        }

        return tipoInventario.trim().toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "planejado" : valor.toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private void validarRelacionamentosParaCriacao(InventariosRequestDTO data) {
        String tipoInventario = normalizarTipoInventario(data.tipoInventario());
        String status = normalizarStatus(data.status());

        validarInventarioEmAndamentoPorTipo(tipoInventario, status);
        validarSobreposicaoInventarioEmAndamento(data, status);
    }

    private void validarRelacionamentosParaAtualizacao(InventariosRequestDTO data, Integer id) {
        String tipoInventario = normalizarTipoInventario(data.tipoInventario());
        String status = normalizarStatus(data.status());

        validarInventarioEmAndamentoPorTipo(tipoInventario, status, id);
        validarSobreposicaoInventarioEmAndamento(data, status, id);
    }

    private void validarInventarioEmAndamentoPorTipo(String tipoInventario, String status) {
        if (status.equals("em_andamento")
                && repository.existsByTipoInventarioAndStatus(tipoInventario, "em_andamento")) {
            throw new ValidacaoException("Ja existe inventario em andamento para este tipo");
        }
    }

    private void validarInventarioEmAndamentoPorTipo(String tipoInventario, String status, Integer id) {
        if (status.equals("em_andamento")
                && repository.existsByTipoInventarioAndStatusAndIdNot(tipoInventario, "em_andamento", id)) {
            throw new ValidacaoException("Ja existe inventario em andamento para este tipo");
        }
    }

    private void validarSobreposicaoInventarioEmAndamento(InventariosRequestDTO data, String status) {
        if (!status.equals("em_andamento") || data.dataFim() == null) {
            return;
        }

        if (repository.existsByStatusAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                "em_andamento",
                data.dataFim(),
                data.dataInicio()
        )) {
            throw new ValidacaoException("Ja existe inventario em andamento com periodo sobreposto");
        }
    }

    private void validarSobreposicaoInventarioEmAndamento(InventariosRequestDTO data, String status, Integer id) {
        if (!status.equals("em_andamento") || data.dataFim() == null) {
            return;
        }

        if (repository.existsByStatusAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
                "em_andamento",
                data.dataFim(),
                data.dataInicio(),
                id
        )) {
            throw new ValidacaoException("Ja existe inventario em andamento com periodo sobreposto");
        }
    }
}
