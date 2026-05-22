package com.example.backend.fi.contasPagar;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.fi.centrosCusto.CentrosCusto;
import com.example.backend.fi.centrosCusto.CentrosCustoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ContasPagarService {

    private final ContasPagarRepository repository;
    private final EmpresasRepository empresasRepository;
    private final ParceirosRepository parceirosRepository;
    private final CentrosCustoRepository centrosCustoRepository;

    public ContasPagarService(
            ContasPagarRepository repository,
            EmpresasRepository empresasRepository,
            ParceirosRepository parceirosRepository,
            CentrosCustoRepository centrosCustoRepository
    ) {
        this.repository = repository;
        this.empresasRepository = empresasRepository;
        this.parceirosRepository = parceirosRepository;
        this.centrosCustoRepository = centrosCustoRepository;
    }

    @Transactional
    public ContasPagar criar(ContasPagarRequestDTO data) {
        validar(data);

        Empresas empresa = buscarEmpresa(data.empresa());
        Parceiros fornecedor = buscarFornecedor(data.fornecedor());
        CentrosCusto centroCusto = buscarCentroCusto(data.centroCusto());

        ContasPagar entity = new ContasPagar();
        preencher(entity, data, empresa, fornecedor, centroCusto, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public ContasPagar atualizar(Integer id, ContasPagarRequestDTO data) {
        validar(data);

        ContasPagar entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a pagar nao encontrada"));

        Empresas empresa = buscarEmpresa(data.empresa());
        Parceiros fornecedor = buscarFornecedor(data.fornecedor());
        CentrosCusto centroCusto = buscarCentroCusto(data.centroCusto());

        preencher(entity, data, empresa, fornecedor, centroCusto, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        ContasPagar entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a pagar nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            ContasPagar entity,
            ContasPagarRequestDTO data,
            Empresas empresa,
            Parceiros fornecedor,
            CentrosCusto centroCusto,
            LocalDateTime createdAt
    ) {
        BigDecimal valorOriginal = data.valorOriginal();
        BigDecimal valorPago = nvl(data.valorPago());

        entity.setEmpresa(empresa);
        entity.setFornecedor(fornecedor);
        entity.setCentroCusto(centroCusto);
        entity.setNumeroDocumento(data.numeroDocumento());
        entity.setDescricao(data.descricao());
        entity.setValorOriginal(valorOriginal);
        entity.setValorPago(valorPago);
        entity.setDataEmissao(data.dataEmissao());
        entity.setDataVencimento(data.dataVencimento());
        entity.setDataPagamento(data.dataPagamento());
        entity.setStatus(definirStatus(valorOriginal, valorPago));
        entity.setFormaPagamento(data.formaPagamento());
        entity.setCreatedAt(createdAt);
    }

    private void validar(ContasPagarRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da conta a pagar sao obrigatorios");
        }

        if (data.empresa() == null) {
            throw new ValidacaoException("Empresa e obrigatoria");
        }

        if (data.fornecedor() == null) {
            throw new ValidacaoException("Fornecedor e obrigatorio");
        }

        if (data.valorOriginal() == null || data.valorOriginal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor original deve ser maior que zero");
        }

        if (data.valorPago() != null && data.valorPago().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Valor pago nao pode ser negativo");
        }

        if (data.valorPago() != null && data.valorPago().compareTo(data.valorOriginal()) > 0) {
            throw new ValidacaoException("Valor pago nao pode ser maior que o valor original");
        }

        if (data.dataEmissao() != null && data.dataVencimento() != null
                && data.dataVencimento().isBefore(data.dataEmissao())) {
            throw new ValidacaoException("Data de vencimento nao pode ser anterior a data de emissao");
        }

        if (nvl(data.valorPago()).compareTo(BigDecimal.ZERO) > 0 && data.dataPagamento() == null) {
            throw new ValidacaoException("Data de pagamento e obrigatoria quando houver valor pago");
        }

        if (nvl(data.valorPago()).compareTo(BigDecimal.ZERO) == 0 && data.dataPagamento() != null) {
            throw new ValidacaoException("Data de pagamento nao deve ser informada sem valor pago");
        }
    }

    private Empresas buscarEmpresa(Integer empresaId) {
        return empresasRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));
    }

    private Parceiros buscarFornecedor(Integer fornecedorId) {
        return parceirosRepository.findById(fornecedorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor nao encontrado"));
    }

    private CentrosCusto buscarCentroCusto(Integer centroCustoId) {
        if (centroCustoId == null) {
            return null;
        }

        return centrosCustoRepository.findById(centroCustoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo nao encontrado"));
    }

    private String definirStatus(BigDecimal valorOriginal, BigDecimal valorPago) {
        BigDecimal pago = nvl(valorPago);

        if (pago.compareTo(BigDecimal.ZERO) == 0) {
            return "pendente";
        }

        if (pago.compareTo(valorOriginal) < 0) {
            return "parcial";
        }

        return "pago";
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}