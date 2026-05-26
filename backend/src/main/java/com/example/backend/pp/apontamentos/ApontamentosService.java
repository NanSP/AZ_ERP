package com.example.backend.pp.apontamentos;

import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.pp.ordemProducao.OrdemProducaoRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ApontamentosService {

    private final ApontamentosRepository repository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ApontamentosService(
            ApontamentosRepository repository,
            OrdemProducaoRepository ordemProducaoRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Apontamentos criar(ApontamentosRequestDTO data) {
        validar(data);

        OrdemProducao op = buscarOp(data.op());
        Colaboradores operador = buscarOperador(data.operador());

        validarOpParaApontamento(op);
        validarDataHoraApontamento(op, data.dataHoraInicio(), data.dataHoraFim());
        validarImpactoQuantidade(
                op,
                zeroSeNulo(data.quantidadeProduzida()),
                BigDecimal.ZERO,
                zeroSeNulo(data.quantidadeRefugo()),
                BigDecimal.ZERO
        );

        Apontamentos entity = new Apontamentos();
        preencher(entity, data, op, operador, LocalDateTime.now());
        Apontamentos saved = repository.save(entity);

        atualizarQuantidadeProduzidaOp(op, zeroSeNulo(data.quantidadeProduzida()), BigDecimal.ZERO);
        recalcularStatusOp(op);
        return saved;
    }

    @Transactional
    public Apontamentos atualizar(Integer id, ApontamentosRequestDTO data) {
        validar(data);

        Apontamentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Apontamento nao encontrado"));

        OrdemProducao opAtual = entity.getOp();
        BigDecimal quantidadeAnterior = zeroSeNulo(entity.getQuantidadeProduzida());
        BigDecimal refugoAnterior = zeroSeNulo(entity.getQuantidadeRefugo());

        OrdemProducao novaOp = buscarOp(data.op());
        Colaboradores operador = buscarOperador(data.operador());
        boolean mesmaOp = opAtual != null && opAtual.getId().equals(novaOp.getId());

        validarOpParaApontamento(novaOp);
        validarDataHoraApontamento(novaOp, data.dataHoraInicio(), data.dataHoraFim());
        validarImpactoQuantidade(
                novaOp,
                zeroSeNulo(data.quantidadeProduzida()),
                mesmaOp ? quantidadeAnterior : BigDecimal.ZERO,
                zeroSeNulo(data.quantidadeRefugo()),
                mesmaOp ? refugoAnterior : BigDecimal.ZERO
        );

        preencher(entity, data, novaOp, operador, entity.getCreatedAt());
        Apontamentos updated = repository.save(entity);

        if (mesmaOp) {
            atualizarQuantidadeProduzidaOp(novaOp, zeroSeNulo(data.quantidadeProduzida()), quantidadeAnterior);
            recalcularStatusOp(novaOp);
            return updated;
        }

        reverterQuantidadeProduzidaOp(opAtual, quantidadeAnterior);
        recalcularStatusOp(opAtual);

        atualizarQuantidadeProduzidaOp(novaOp, zeroSeNulo(data.quantidadeProduzida()), BigDecimal.ZERO);
        recalcularStatusOp(novaOp);

        return updated;
    }

    @Transactional
    public void excluir(Integer id) {
        Apontamentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Apontamento nao encontrado"));

        repository.delete(entity);
        reverterQuantidadeProduzidaOp(entity.getOp(), zeroSeNulo(entity.getQuantidadeProduzida()));
        recalcularStatusOp(entity.getOp());
    }

    private void preencher(
            Apontamentos entity,
            ApontamentosRequestDTO data,
            OrdemProducao op,
            Colaboradores operador,
            LocalDateTime createdAt
    ) {
        entity.setOp(op);
        entity.setMaquinaId(data.maquinaId());
        entity.setOperador(operador);
        entity.setDataHoraInicio(data.dataHoraInicio());
        entity.setDataHoraFim(data.dataHoraFim());
        entity.setQuantidadeProduzida(zeroSeNulo(data.quantidadeProduzida()));
        entity.setQuantidadeRefugo(zeroSeNulo(data.quantidadeRefugo()));
        entity.setTempoParado(zeroSeNulo(data.tempoParado()));
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ApontamentosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do apontamento sao obrigatorios");
        }

        if (data.op() == null) {
            throw new ValidacaoException("Ordem de producao e obrigatoria");
        }

        if (data.operador() == null) {
            throw new ValidacaoException("Operador e obrigatorio");
        }

        if (data.dataHoraInicio() != null && data.dataHoraFim() != null
                && data.dataHoraFim().isBefore(data.dataHoraInicio())) {
            throw new ValidacaoException("Data e hora fim nao podem ser anteriores a data e hora inicio");
        }

        if (data.quantidadeProduzida() != null && data.quantidadeProduzida().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade produzida nao pode ser negativa");
        }

        if (data.quantidadeRefugo() != null && data.quantidadeRefugo().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade de refugo nao pode ser negativa");
        }

        if (data.tempoParado() != null && data.tempoParado().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Tempo parado nao pode ser negativo");
        }
    }

    private OrdemProducao buscarOp(Integer opId) {
        return ordemProducaoRepository.findById(opId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de producao nao encontrada"));
    }

    private Colaboradores buscarOperador(Integer operadorId) {
        return colaboradoresRepository.findById(operadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador nao encontrado"));
    }

    private void validarOpParaApontamento(OrdemProducao op) {
        String status = normalizarOpcional(op.getStatus());

        if (status == null) {
            return;
        }

        if (status.equals("cancelada")) {
            throw new ValidacaoException("Nao e permitido registrar apontamento para ordem cancelada");
        }

        if (status.equals("concluida")) {
            throw new ValidacaoException("Nao e permitido registrar apontamento para ordem concluida");
        }
    }

    private void validarDataHoraApontamento(
            OrdemProducao op,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim
    ) {
        LocalDateTime dataMinima = op.getDataInicio() != null
                ? op.getDataInicio().atStartOfDay()
                : op.getDataEmissao().atStartOfDay();
        LocalDateTime dataMaxima = op.getDataFim() != null
                ? op.getDataFim().atTime(23, 59, 59)
                : null;

        if (dataHoraInicio != null && dataHoraInicio.isBefore(dataMinima)) {
            throw new ValidacaoException(
                    "Data e hora inicio do apontamento nao podem ser anteriores ao inicio da ordem de producao"
            );
        }

        if (dataHoraFim != null && dataHoraFim.isBefore(dataMinima)) {
            throw new ValidacaoException(
                    "Data e hora fim do apontamento nao podem ser anteriores ao inicio da ordem de producao"
            );
        }

        if (dataMaxima != null && dataHoraInicio != null && dataHoraInicio.isAfter(dataMaxima)) {
            throw new ValidacaoException(
                    "Data e hora inicio do apontamento nao podem ser posteriores ao encerramento da ordem de producao"
            );
        }

        if (dataMaxima != null && dataHoraFim != null && dataHoraFim.isAfter(dataMaxima)) {
            throw new ValidacaoException(
                    "Data e hora fim do apontamento nao podem ser posteriores ao encerramento da ordem de producao"
            );
        }
    }

    private void validarImpactoQuantidade(
            OrdemProducao op,
            BigDecimal novaQuantidade,
            BigDecimal quantidadeAnterior,
            BigDecimal novoRefugo,
            BigDecimal refugoAnterior
    ) {
        BigDecimal produzidaAtual = zeroSeNulo(op.getQuantidadeProduzida());
        BigDecimal refugoAtual = zeroSeNulo(repository.sumQuantidadeRefugoByOpId(op.getId()));
        BigDecimal planejada = zeroSeNulo(op.getQuantidadePlanejada());
        BigDecimal produzidaResultante = produzidaAtual.subtract(quantidadeAnterior).add(novaQuantidade);
        BigDecimal refugoResultante = refugoAtual.subtract(refugoAnterior).add(novoRefugo);
        BigDecimal totalConsumidoResultante = produzidaResultante.add(refugoResultante);

        if (produzidaResultante.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade produzida resultante nao pode ser negativa");
        }

        if (refugoResultante.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Quantidade de refugo resultante nao pode ser negativa");
        }

        if (totalConsumidoResultante.compareTo(planejada) > 0) {
            throw new ValidacaoException("Apontamento excede a quantidade planejada da ordem considerando producao e refugo");
        }
    }

    private void atualizarQuantidadeProduzidaOp(
            OrdemProducao op,
            BigDecimal novaQuantidade,
            BigDecimal quantidadeAnterior
    ) {
        BigDecimal produzidaAtual = zeroSeNulo(op.getQuantidadeProduzida());
        BigDecimal produzidaResultante = produzidaAtual.subtract(quantidadeAnterior).add(novaQuantidade);

        op.setQuantidadeProduzida(produzidaResultante);
    }

    private void recalcularStatusOp(OrdemProducao op) {
        if (op == null) {
            return;
        }

        BigDecimal produzidaResultante = zeroSeNulo(op.getQuantidadeProduzida());
        BigDecimal refugoResultante = zeroSeNulo(repository.sumQuantidadeRefugoByOpId(op.getId()));
        BigDecimal planejada = zeroSeNulo(op.getQuantidadePlanejada());
        BigDecimal totalConsumidoResultante = produzidaResultante.add(refugoResultante);

        if (totalConsumidoResultante.compareTo(BigDecimal.ZERO) == 0) {
            op.setStatus("planejada");
            return;
        }

        if (totalConsumidoResultante.compareTo(planejada) < 0) {
            op.setStatus("em_producao");
            return;
        }

        op.setStatus("concluida");
    }

    private void reverterQuantidadeProduzidaOp(OrdemProducao op, BigDecimal quantidadeAnterior) {
        if (op == null) {
            return;
        }

        atualizarQuantidadeProduzidaOp(op, BigDecimal.ZERO, quantidadeAnterior);
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim().toLowerCase();
        return normalizado.isBlank() ? null : normalizado;
    }
}
