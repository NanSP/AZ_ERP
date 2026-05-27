package com.example.backend.rh.controleDePonto;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class ControleDePontoService {

    private static final LocalTime HORARIO_PADRAO_ENTRADA = LocalTime.of(8, 0);
    private static final BigDecimal JORNADA_DIARIA_PADRAO = new BigDecimal("8.00");
    private static final BigDecimal DIAS_UTEIS_SEMANA = new BigDecimal("5");

    private final ControleDePontoRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ControleDePontoService(
            ControleDePontoRepository repository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public ControleDePonto criar(ControleDePontoRequestDTO data) {
        validar(data);
        validarDuplicidadeParaCriacao(data.colaborador(), data.data());

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        validarColaboradorParaPonto(colaborador, data.data());

        ControleDePonto entity = new ControleDePonto();
        preencher(entity, data, colaborador, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public ControleDePonto atualizar(Integer id, ControleDePontoRequestDTO data) {
        validar(data);

        ControleDePonto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle de ponto nao encontrado"));

        validarDuplicidadeParaAtualizacao(data.colaborador(), data.data(), id);

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        validarColaboradorParaPonto(colaborador, data.data());

        preencher(entity, data, colaborador, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        ControleDePonto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle de ponto nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            ControleDePonto entity,
            ControleDePontoRequestDTO data,
            Colaboradores colaborador,
            LocalDateTime createdAt
    ) {
        BigDecimal horasTrabalhadas = calcularHorasTrabalhadas(
                data.horaEntrada(),
                data.horaSaidaAlmoco(),
                data.horaRetornoAlmoco(),
                data.horaSaida()
        );

        BigDecimal horasExtras = calcularHorasExtras(horasTrabalhadas, colaborador);
        Integer atrasos = calcularAtrasos(data.horaEntrada());

        entity.setColaborador(colaborador);
        entity.setData(data.data());
        entity.setHoraEntrada(data.horaEntrada());
        entity.setHoraSaidaAlmoco(data.horaSaidaAlmoco());
        entity.setHoraRetornoAlmoco(data.horaRetornoAlmoco());
        entity.setHoraSaida(data.horaSaida());
        entity.setHorasTrabalhadas(horasTrabalhadas);
        entity.setHorasExtras(horasExtras);
        entity.setAtrasos(atrasos);
        entity.setCreatedAt(createdAt);
    }

    private void validar(ControleDePontoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do controle de ponto sao obrigatorios");
        }

        if (data.colaborador() == null) {
            throw new ValidacaoException("Colaborador e obrigatorio");
        }

        if (data.data() == null) {
            throw new ValidacaoException("Data e obrigatoria");
        }

        validarSequenciaHorarios(
                data.horaEntrada(),
                data.horaSaidaAlmoco(),
                data.horaRetornoAlmoco(),
                data.horaSaida()
        );
    }

    private void validarSequenciaHorarios(
            LocalTime horaEntrada,
            LocalTime horaSaidaAlmoco,
            LocalTime horaRetornoAlmoco,
            LocalTime horaSaida
    ) {
        if (horaEntrada == null) {
            throw new ValidacaoException("Hora de entrada e obrigatoria");
        }

        if (horaSaidaAlmoco != null && horaSaidaAlmoco.isBefore(horaEntrada)) {
            throw new ValidacaoException("Hora de saida para almoco nao pode ser anterior a hora de entrada");
        }

        if (horaRetornoAlmoco != null && horaSaidaAlmoco == null) {
            throw new ValidacaoException("Hora de saida para almoco deve ser informada antes do retorno");
        }

        if (horaRetornoAlmoco != null && horaRetornoAlmoco.isBefore(horaSaidaAlmoco)) {
            throw new ValidacaoException("Hora de retorno do almoco nao pode ser anterior a saida para almoco");
        }

        LocalTime referenciaSaida = horaRetornoAlmoco != null ? horaRetornoAlmoco : horaEntrada;
        if (horaSaida != null && horaSaida.isBefore(referenciaSaida)) {
            throw new ValidacaoException("Hora de saida nao pode ser anterior ao ultimo horario registrado");
        }
    }

    private void validarColaboradorParaPonto(Colaboradores colaborador, LocalDate data) {
        String situacao = colaborador.getSituacao() != null
                ? colaborador.getSituacao().trim().toLowerCase()
                : null;

        if ("desligado".equals(situacao) || "inativo".equals(situacao)) {
            throw new ValidacaoException("Nao e permitido registrar ponto para colaborador inativo");
        }

        if (colaborador.getDataDemissao() != null && data.isAfter(colaborador.getDataDemissao())) {
            throw new ValidacaoException("Nao e permitido registrar ponto apos a data de demissao");
        }
    }

    private void validarDuplicidadeParaCriacao(Integer colaboradorId, LocalDate data) {
        if (repository.existsByColaboradorIdAndData(colaboradorId, data)) {
            throw new ValidacaoException("Ja existe controle de ponto para este colaborador nesta data");
        }
    }

    private void validarDuplicidadeParaAtualizacao(Integer colaboradorId, LocalDate data, Integer id) {
        if (repository.existsByColaboradorIdAndDataAndIdNot(colaboradorId, data, id)) {
            throw new ValidacaoException("Ja existe controle de ponto para este colaborador nesta data");
        }
    }

    private Colaboradores buscarColaborador(Integer colaboradorId) {
        return colaboradoresRepository.findById(colaboradorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));
    }

    private BigDecimal calcularHorasTrabalhadas(
            LocalTime horaEntrada,
            LocalTime horaSaidaAlmoco,
            LocalTime horaRetornoAlmoco,
            LocalTime horaSaida
    ) {
        if (horaEntrada == null || horaSaida == null) {
            return BigDecimal.ZERO;
        }

        long minutosTrabalhados = Duration.between(horaEntrada, horaSaida).toMinutes();

        if (horaSaidaAlmoco != null && horaRetornoAlmoco != null) {
            minutosTrabalhados -= Duration.between(horaSaidaAlmoco, horaRetornoAlmoco).toMinutes();
        }

        return BigDecimal.valueOf(minutosTrabalhados)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
                .max(BigDecimal.ZERO);
    }

    private BigDecimal calcularHorasExtras(BigDecimal horasTrabalhadas, Colaboradores colaborador) {
        BigDecimal jornadaDiaria = calcularJornadaDiaria(colaborador);

        if (horasTrabalhadas.compareTo(jornadaDiaria) <= 0) {
            return BigDecimal.ZERO;
        }

        return horasTrabalhadas.subtract(jornadaDiaria).setScale(2, RoundingMode.HALF_UP);
    }

    private Integer calcularAtrasos(LocalTime horaEntrada) {
        if (horaEntrada == null || !horaEntrada.isAfter(HORARIO_PADRAO_ENTRADA)) {
            return 0;
        }

        return (int) Duration.between(HORARIO_PADRAO_ENTRADA, horaEntrada).toMinutes();
    }

    private BigDecimal calcularJornadaDiaria(Colaboradores colaborador) {
        if (colaborador == null || colaborador.getJornadaSemanal() == null || colaborador.getJornadaSemanal() <= 0) {
            return JORNADA_DIARIA_PADRAO;
        }

        return BigDecimal.valueOf(colaborador.getJornadaSemanal())
                .divide(DIAS_UTEIS_SEMANA, 2, RoundingMode.HALF_UP);
    }
}
