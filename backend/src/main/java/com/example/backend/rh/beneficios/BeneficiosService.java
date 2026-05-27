package com.example.backend.rh.beneficios;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BeneficiosService {

    private final BeneficiosRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;

    public BeneficiosService(
            BeneficiosRepository repository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Beneficios criar(BeneficiosRequestDTO data) {
        validar(data);
        validarDuplicidadeBeneficioAtivoParaCriacao(data.colaborador(), normalizarTipoBeneficio(data.tipoBeneficio()), normalizarAtivo(data.ativo()));

        Colaboradores colaborador = buscarColaborador(data.colaborador());

        Beneficios entity = new Beneficios();
        preencher(entity, data, colaborador);

        return repository.save(entity);
    }

    @Transactional
    public Beneficios atualizar(Integer id, BeneficiosRequestDTO data) {
        validar(data);
        validarDuplicidadeBeneficioAtivoParaAtualizacao(
                data.colaborador(),
                normalizarTipoBeneficio(data.tipoBeneficio()),
                normalizarAtivo(data.ativo()),
                id
        );

        Beneficios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficio nao encontrado"));

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        preencher(entity, data, colaborador);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Beneficios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficio nao encontrado"));

        if (Boolean.TRUE.equals(entity.getAtivo())) {
            throw new ValidacaoException("Nao e permitido excluir beneficio ativo; desative-o antes");
        }

        repository.delete(entity);
    }

    private void preencher(
            Beneficios entity,
            BeneficiosRequestDTO data,
            Colaboradores colaborador
    ) {
        entity.setColaborador(colaborador);
        entity.setTipoBeneficio(normalizarTipoBeneficio(data.tipoBeneficio()));
        entity.setValor(data.valor());
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setAtivo(normalizarAtivo(data.ativo()));
    }

    private void validar(BeneficiosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do beneficio sao obrigatorios");
        }

        if (data.colaborador() == null) {
            throw new ValidacaoException("Colaborador e obrigatorio");
        }

        String tipoBeneficio = normalizarTipoBeneficio(data.tipoBeneficio());
        validarTipoBeneficio(tipoBeneficio);

        validarNaoNegativo(data.valor(), "Valor do beneficio nao pode ser negativo");

        if (data.dataInicio() != null
                && data.dataFim() != null
                && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        validarAtivoComDatas(normalizarAtivo(data.ativo()), data.dataInicio(), data.dataFim());
    }

    private void validarTipoBeneficio(String tipoBeneficio) {
        if (!tipoBeneficio.equals("vale_transporte")
                && !tipoBeneficio.equals("vale_refeicao")
                && !tipoBeneficio.equals("plano_saude")
                && !tipoBeneficio.equals("plano_odontologico")) {
            throw new ValidacaoException("Tipo de beneficio invalido");
        }
    }

    private void validarAtivoComDatas(Boolean ativo, LocalDate dataInicio, LocalDate dataFim) {
        if (Boolean.TRUE.equals(ativo) && dataInicio == null) {
            throw new ValidacaoException("Data de inicio e obrigatoria para beneficio ativo");
        }

        if (Boolean.TRUE.equals(ativo) && dataFim != null && dataFim.isBefore(LocalDate.now())) {
            throw new ValidacaoException("Beneficio ativo nao pode ter data fim no passado");
        }
    }

    private Colaboradores buscarColaborador(Integer colaboradorId) {
        return colaboradoresRepository.findById(colaboradorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));
    }

    private void validarDuplicidadeBeneficioAtivoParaCriacao(Integer colaboradorId, String tipoBeneficio, Boolean ativo) {
        if (Boolean.TRUE.equals(ativo)
                && repository.existsByColaboradorIdAndTipoBeneficioAndAtivoTrue(colaboradorId, tipoBeneficio)) {
            throw new ValidacaoException("Ja existe beneficio ativo deste tipo para o colaborador informado");
        }
    }

    private void validarDuplicidadeBeneficioAtivoParaAtualizacao(
            Integer colaboradorId,
            String tipoBeneficio,
            Boolean ativo,
            Integer id
    ) {
        if (Boolean.TRUE.equals(ativo)
                && repository.existsByColaboradorIdAndTipoBeneficioAndAtivoTrueAndIdNot(colaboradorId, tipoBeneficio, id)) {
            throw new ValidacaoException("Ja existe beneficio ativo deste tipo para o colaborador informado");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarTipoBeneficio(String tipoBeneficio) {
        if (tipoBeneficio == null || tipoBeneficio.isBlank()) {
            throw new ValidacaoException("Tipo de beneficio e obrigatorio");
        }

        return tipoBeneficio.trim().toLowerCase();
    }

    private Boolean normalizarAtivo(Boolean ativo) {
        return ativo == null || ativo;
    }
}
