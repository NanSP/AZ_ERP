package com.example.backend.core.empresas;

import com.example.backend.fi.contasPagar.ContasPagarRepository;
import com.example.backend.fi.contasReceber.ContasReceberRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class EmpresasService {

    private final EmpresasRepository repository;
    private final ContasPagarRepository contasPagarRepository;
    private final ContasReceberRepository contasReceberRepository;
    private final EstoquesRepository estoquesRepository;

    public EmpresasService(
            EmpresasRepository repository,
            ContasPagarRepository contasPagarRepository,
            ContasReceberRepository contasReceberRepository,
            EstoquesRepository estoquesRepository
    ) {
        this.repository = repository;
        this.contasPagarRepository = contasPagarRepository;
        this.contasReceberRepository = contasReceberRepository;
        this.estoquesRepository = estoquesRepository;
    }

    @Transactional
    public Empresas criar(EmpresasRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarObrigatorio(data.codigo(), "Codigo da empresa e obrigatorio"));
        validarCnpjDuplicadoParaCriacao(normalizarCnpj(data.cnpj()));

        Empresas entity = new Empresas();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Empresas atualizar(Integer id, EmpresasRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarObrigatorio(data.codigo(), "Codigo da empresa e obrigatorio"), id);
        validarCnpjDuplicadoParaAtualizacao(normalizarCnpj(data.cnpj()), id);

        Empresas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Empresas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Empresas entity,
            EmpresasRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setCodigo(normalizarObrigatorio(data.codigo(), "Codigo da empresa e obrigatorio"));
        entity.setRazaoSocial(normalizarObrigatorio(data.razaoSocial(), "Razao social e obrigatoria"));
        entity.setNomeFantasia(normalizarOpcional(data.nomeFantasia()));
        entity.setCnpj(normalizarCnpj(data.cnpj()));
        entity.setInscricaoEstadual(normalizarOpcional(data.inscricaoEstadual()));
        entity.setInscricaoMunicipal(normalizarOpcional(data.inscricaoMunicipal()));
        entity.setRegimeTributario(normalizarOpcional(data.regimeTributario()));
        entity.setDataFundacao(data.dataFundacao());
        entity.setSituacao(normalizarSituacao(data.situacao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(EmpresasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da empresa sao obrigatorios");
        }

        normalizarObrigatorio(data.codigo(), "Codigo da empresa e obrigatorio");
        normalizarObrigatorio(data.razaoSocial(), "Razao social e obrigatoria");
        normalizarCnpj(data.cnpj());
        validarSituacao(normalizarSituacao(data.situacao()));

        if (data.dataFundacao() != null && data.dataFundacao().isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data de fundacao nao pode estar no futuro");
        }
    }

    private void validarSituacao(String situacao) {
        if (!situacao.equals("ativo")
                && !situacao.equals("inativo")
                && !situacao.equals("bloqueado")) {
            throw new ValidacaoException("Situacao invalida");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe empresa com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe empresa com o codigo informado");
        }
    }

    private void validarCnpjDuplicadoParaCriacao(String cnpj) {
        if (repository.existsByCnpj(cnpj)) {
            throw new ValidacaoException("Ja existe empresa com o CNPJ informado");
        }
    }

    private void validarCnpjDuplicadoParaAtualizacao(String cnpj, Integer id) {
        if (repository.existsByCnpjAndIdNot(cnpj, id)) {
            throw new ValidacaoException("Ja existe empresa com o CNPJ informado");
        }
    }

    private void validarAlteracoesSensiveis(Empresas entity, EmpresasRequestDTO data) {
        if (!empresaEmUso(entity.getId())) {
            return;
        }

        String novoCodigo = normalizarObrigatorio(data.codigo(), "Codigo da empresa e obrigatorio");
        String novoCnpj = normalizarCnpj(data.cnpj());
        String novaSituacao = normalizarSituacao(data.situacao());

        if (!novoCodigo.equals(entity.getCodigo())) {
            throw new ValidacaoException("Nao e permitido alterar o codigo da empresa que ja possui uso operacional");
        }

        if (!novoCnpj.equals(entity.getCnpj())) {
            throw new ValidacaoException("Nao e permitido alterar o CNPJ da empresa que ja possui uso operacional");
        }

        if (!novaSituacao.equals(normalizarSituacao(entity.getSituacao()))) {
            throw new ValidacaoException("Nao e permitido alterar a situacao da empresa que ja possui uso operacional");
        }
    }

    private void validarExclusao(Empresas entity) {
        if (empresaEmUso(entity.getId())) {
            throw new ValidacaoException("Nao e permitido excluir empresa com uso operacional");
        }
    }

    private boolean empresaEmUso(Integer empresaId) {
        return contasPagarRepository.existsByEmpresaId(empresaId)
                || contasReceberRepository.existsByEmpresaId(empresaId)
                || estoquesRepository.existsByEmpresaId(empresaId);
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new ValidacaoException("CNPJ e obrigatorio");
        }

        String normalizado = cnpj.replaceAll("\\D", "");

        if (!normalizado.matches("\\d{14}")) {
            throw new ValidacaoException("CNPJ deve conter 14 digitos numericos");
        }

        return normalizado;
    }

    private String normalizarSituacao(String situacao) {
        String valor = normalizarOpcional(situacao);
        return valor == null ? "ativo" : valor.toLowerCase();
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
