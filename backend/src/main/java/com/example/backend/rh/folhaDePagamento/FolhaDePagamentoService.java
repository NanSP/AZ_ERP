package com.example.backend.rh.folhaDePagamento;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.rh.controleDePonto.ControleDePontoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class FolhaDePagamentoService {

    private final FolhaDePagamentoRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;
    private final ControleDePontoRepository controleDePontoRepository;
    private final FolhaDePagamentoCalculator calculator;

    public FolhaDePagamentoService(
            FolhaDePagamentoRepository repository,
            ColaboradoresRepository colaboradoresRepository,
            ControleDePontoRepository controleDePontoRepository,
            FolhaDePagamentoCalculator calculator
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
        this.controleDePontoRepository = controleDePontoRepository;
        this.calculator = calculator;
    }

    @Transactional
    public FolhaDePagamento criar(FolhaDePagamentoRequestDTO data) {
        validar(data);
        validarDuplicidadeParaCriacao(data.colaborador(), data.competencia());

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        validarRelacionamentoComColaborador(colaborador, data.competencia());
        BigDecimal horasNormais = resolverHorasNormais(data, colaborador);
        BigDecimal horasExtras = resolverHorasExtras(data, colaborador);
        FolhaCalculadaDTO calculada = calculator.calcular(
                data.salarioBase(),
                horasNormais,
                horasExtras,
                data.adicionais(),
                data.descontos()
        );

        FolhaDePagamento entity = new FolhaDePagamento();
        preencher(entity, data, colaborador, calculada, horasNormais, horasExtras);

        return repository.save(entity);
    }

    @Transactional
    public FolhaDePagamento atualizar(Integer id, FolhaDePagamentoRequestDTO data) {
        validar(data);
        validarDuplicidadeParaAtualizacao(data.colaborador(), data.competencia(), id);

        FolhaDePagamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Folha de pagamento nao encontrada"));

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        validarRelacionamentoComColaborador(colaborador, data.competencia());
        BigDecimal horasNormais = resolverHorasNormais(data, colaborador);
        BigDecimal horasExtras = resolverHorasExtras(data, colaborador);
        FolhaCalculadaDTO calculada = calculator.calcular(
                data.salarioBase(),
                horasNormais,
                horasExtras,
                data.adicionais(),
                data.descontos()
        );

        preencher(entity, data, colaborador, calculada, horasNormais, horasExtras);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        FolhaDePagamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Folha de pagamento nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            FolhaDePagamento entity,
            FolhaDePagamentoRequestDTO data,
            Colaboradores colaborador,
            FolhaCalculadaDTO calculada,
            BigDecimal horasNormais,
            BigDecimal horasExtras
    ) {
        entity.setColaborador(colaborador);
        entity.setCompetencia(data.competencia());
        entity.setSalarioBase(data.salarioBase());
        entity.setHorasNormais(horasNormais);
        entity.setHorasExtras(horasExtras);
        entity.setAdicionais(data.adicionais());
        entity.setDescontos(data.descontos());
        entity.setValorHora(calculada.valorHora());
        entity.setValorHorasNormais(calculada.valorHorasNormais());
        entity.setValorHorasExtras(calculada.valorHorasExtras());
        entity.setValorBruto(calculada.valorBruto());
        entity.setValorLiquido(calculada.valorLiquido());
        entity.setDataPagamento(data.dataPagamento());
        entity.setStatus(normalizarStatus(data.status()));
    }

    private void validar(FolhaDePagamentoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da folha de pagamento sao obrigatorios");
        }

        if (data.colaborador() == null) {
            throw new ValidacaoException("Colaborador e obrigatorio");
        }

        if (data.competencia() == null) {
            throw new ValidacaoException("Competencia e obrigatoria");
        }

        validarNaoNegativo(data.salarioBase(), "Salario base nao pode ser negativo");
        validarNaoNegativo(data.horasNormais(), "Horas normais nao podem ser negativas");
        validarNaoNegativo(data.horasExtras(), "Horas extras nao podem ser negativas");
        validarNaoNegativo(data.adicionais(), "Adicionais nao podem ser negativos");
        validarNaoNegativo(data.descontos(), "Descontos nao podem ser negativos");

        if (data.dataPagamento() != null && data.dataPagamento().isBefore(data.competencia())) {
            throw new ValidacaoException("Data de pagamento nao pode ser anterior a competencia");
        }

        validarStatus(normalizarStatus(data.status()), data.dataPagamento());
    }

    private void validarStatus(String status, LocalDate dataPagamento) {
        if (!status.equals("calculado")
                && !status.equals("pago")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }

        if (status.equals("pago") && dataPagamento == null) {
            throw new ValidacaoException("Data de pagamento e obrigatoria para folha com status pago");
        }
    }

    private void validarDuplicidadeParaCriacao(Integer colaboradorId, LocalDate competencia) {
        if (repository.existsByColaboradorIdAndCompetencia(colaboradorId, competencia)) {
            throw new ValidacaoException("Ja existe folha de pagamento para este colaborador nesta competencia");
        }
    }

    private void validarDuplicidadeParaAtualizacao(Integer colaboradorId, LocalDate competencia, Integer id) {
        if (repository.existsByColaboradorIdAndCompetenciaAndIdNot(colaboradorId, competencia, id)) {
            throw new ValidacaoException("Ja existe folha de pagamento para este colaborador nesta competencia");
        }
    }

    private Colaboradores buscarColaborador(Integer colaboradorId) {
        return colaboradoresRepository.findById(colaboradorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));
    }

    private void validarRelacionamentoComColaborador(Colaboradores colaborador, LocalDate competencia) {
        YearMonth competenciaMes = YearMonth.from(competencia);

        if (colaborador.getDataAdmissao() != null
                && competenciaMes.isBefore(YearMonth.from(colaborador.getDataAdmissao()))) {
            throw new ValidacaoException("Nao e permitido gerar folha antes do mes de admissao do colaborador");
        }

        if (colaborador.getDataDemissao() != null
                && competenciaMes.isAfter(YearMonth.from(colaborador.getDataDemissao()))) {
            throw new ValidacaoException("Nao e permitido gerar folha apos o mes de demissao do colaborador");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) {
            return "calculado";
        }

        return status.trim().toLowerCase();
    }

    private BigDecimal resolverHorasNormais(FolhaDePagamentoRequestDTO data, Colaboradores colaborador) {
        if (data.horasNormais() != null) {
            return data.horasNormais();
        }

        BigDecimal horasTrabalhadas = buscarHorasTrabalhadasNoPonto(colaborador.getId(), data.competencia());
        BigDecimal horasExtras = resolverHorasExtras(data, colaborador);
        BigDecimal horasNormais = horasTrabalhadas.subtract(horasExtras);

        return horasNormais.max(BigDecimal.ZERO);
    }

    private BigDecimal resolverHorasExtras(FolhaDePagamentoRequestDTO data, Colaboradores colaborador) {
        if (data.horasExtras() != null) {
            return data.horasExtras();
        }

        return buscarHorasExtrasNoPonto(colaborador.getId(), data.competencia());
    }

    private BigDecimal buscarHorasTrabalhadasNoPonto(Integer colaboradorId, LocalDate competencia) {
        LocalDate dataInicio = competencia.withDayOfMonth(1);
        LocalDate dataFim = competencia.withDayOfMonth(competencia.lengthOfMonth());
        return controleDePontoRepository.sumHorasTrabalhadasByColaboradorIdAndPeriodo(colaboradorId, dataInicio, dataFim);
    }

    private BigDecimal buscarHorasExtrasNoPonto(Integer colaboradorId, LocalDate competencia) {
        LocalDate dataInicio = competencia.withDayOfMonth(1);
        LocalDate dataFim = competencia.withDayOfMonth(competencia.lengthOfMonth());
        return controleDePontoRepository.sumHorasExtrasByColaboradorIdAndPeriodo(colaboradorId, dataInicio, dataFim);
    }
}
