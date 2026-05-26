package com.example.backend.am.manutencoes;

import com.example.backend.am.bensPatrimoniais.BensPatrimoniais;
import com.example.backend.am.bensPatrimoniais.BensPatrimoniaisRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ManutencoesService {

    private final ManutencoesRepository repository;
    private final BensPatrimoniaisRepository bensPatrimoniaisRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ManutencoesService(
            ManutencoesRepository repository,
            BensPatrimoniaisRepository bensPatrimoniaisRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.bensPatrimoniaisRepository = bensPatrimoniaisRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Manutencoes criar(ManutencoesRequestDTO data) {
        validar(data);

        BensPatrimoniais ativo = buscarAtivo(data.ativo());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        Manutencoes entity = new Manutencoes();
        preencher(entity, data, ativo, tecnico, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Manutencoes atualizar(Integer id, ManutencoesRequestDTO data) {
        validar(data);

        Manutencoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Manutencao nao encontrada"));

        BensPatrimoniais ativo = buscarAtivo(data.ativo());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        preencher(entity, data, ativo, tecnico, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Manutencoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Manutencao nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            Manutencoes entity,
            ManutencoesRequestDTO data,
            BensPatrimoniais ativo,
            Colaboradores tecnico,
            LocalDateTime createdAt
    ) {
        BigDecimal custoMaoObra = zeroSeNulo(data.custoMaoObra());
        BigDecimal custoMaterial = zeroSeNulo(data.custoMaterial());

        entity.setAtivo(ativo);
        entity.setTipoManutencao(normalizarTipoManutencao(data.tipoManutencao()));
        entity.setDataSolicitacao(data.dataSolicitacao());
        entity.setDataExecucao(data.dataExecucao());
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setCustoMaoObra(custoMaoObra);
        entity.setCustoMaterial(custoMaterial);
        entity.setCustoTotal(calcularCustoTotal(custoMaoObra, custoMaterial));
        entity.setTecnico(tecnico);
        entity.setCreatedAt(createdAt);
    }

    private void validar(ManutencoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da manutencao sao obrigatorios");
        }

        if (data.ativo() == null) {
            throw new ValidacaoException("Ativo e obrigatorio");
        }

        String tipoManutencao = normalizarTipoManutencao(data.tipoManutencao());
        validarTipoManutencao(tipoManutencao);

        validarNaoNegativo(data.custoMaoObra(), "Custo de mao de obra nao pode ser negativo");
        validarNaoNegativo(data.custoMaterial(), "Custo de material nao pode ser negativo");

        if (data.dataSolicitacao() != null
                && data.dataExecucao() != null
                && data.dataExecucao().isBefore(data.dataSolicitacao())) {
            throw new ValidacaoException("Data de execucao nao pode ser anterior a data de solicitacao");
        }

        if (data.dataExecucao() != null && data.tecnico() == null) {
            throw new ValidacaoException("Tecnico e obrigatorio quando a manutencao estiver executada");
        }
    }

    private void validarTipoManutencao(String tipoManutencao) {
        if (!tipoManutencao.equals("preventiva")
                && !tipoManutencao.equals("corretiva")
                && !tipoManutencao.equals("preditiva")) {
            throw new ValidacaoException("Tipo de manutencao invalido");
        }
    }

    private BensPatrimoniais buscarAtivo(Integer ativoId) {
        return bensPatrimoniaisRepository.findById(ativoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ativo nao encontrado"));
    }

    private Colaboradores buscarTecnico(Integer tecnicoId) {
        if (tecnicoId == null) {
            return null;
        }

        return colaboradoresRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tecnico nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal calcularCustoTotal(BigDecimal custoMaoObra, BigDecimal custoMaterial) {
        return custoMaoObra.add(custoMaterial);
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String normalizarTipoManutencao(String tipoManutencao) {
        if (tipoManutencao == null || tipoManutencao.isBlank()) {
            return "";
        }

        return tipoManutencao.trim().toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
