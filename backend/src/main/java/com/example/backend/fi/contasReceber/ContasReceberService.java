package com.example.backend.fi.contasReceber;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.fi.centrosCusto.CentrosCusto;
import com.example.backend.fi.centrosCusto.CentrosCustoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.RegraNegocioException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ContasReceberService {

    private final ContasReceberRepository repository;
    private final EmpresasRepository empresasRepository;
    private final ParceirosRepository parceirosRepository;
    private final CentrosCustoRepository centrosCustoRepository;

    public ContasReceberService(
            ContasReceberRepository repository,
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
    public ContasReceber criar(ContasReceberRequestDTO data) {
        validar(data);

        Empresas empresa = buscarEmpresa(data.empresa());
        Parceiros cliente = buscarCliente(data.cliente());
        CentrosCusto centroCusto = buscarCentroCusto(data.centroCusto());

        ContasReceber entity = new ContasReceber();
        preencher(entity, data, empresa, cliente, centroCusto, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public ContasReceber atualizar(Integer id, ContasReceberRequestDTO data) {
        validar(data);

        ContasReceber entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a receber nao encontrada"));
        validarContaNaoPaga(entity);

        Empresas empresa = buscarEmpresa(data.empresa());
        Parceiros cliente = buscarCliente(data.cliente());
        CentrosCusto centroCusto = buscarCentroCusto(data.centroCusto());

        preencher(entity, data, empresa, cliente, centroCusto, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        ContasReceber entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a receber nao encontrada"));
        validarContaNaoPaga(entity);

        repository.delete(entity);
    }

    private void validarContaNaoPaga(ContasReceber entity) {
        if ("pago".equalsIgnoreCase(entity.getStatus())) {
            throw new RegraNegocioException("Nao e permitido editar ou excluir conta a receber com status pago");
        }
    }

    private void preencher(
            ContasReceber entity,
            ContasReceberRequestDTO data,
            Empresas empresa,
            Parceiros cliente,
            CentrosCusto centroCusto,
            LocalDateTime createdAt
    ) {
        BigDecimal valorOriginal = data.valorOriginal();
        BigDecimal valorRecebido = nvl(data.valorRecebido());

        entity.setEmpresa(empresa);
        entity.setCliente(cliente);
        entity.setCentroCusto(centroCusto);
        entity.setNumeroDocumento(data.numeroDocumento());
        entity.setDescricao(data.descricao());
        entity.setValorOriginal(valorOriginal);
        entity.setValorRecebido(valorRecebido);
        entity.setDataEmissao(data.dataEmissao());
        entity.setDataVencimento(data.dataVencimento());
        entity.setDataRecebimento(data.dataRecebimento());
        entity.setStatus(definirStatus(valorOriginal, valorRecebido));
        entity.setFormaPagamento(data.formaPagamento());
        entity.setCreatedAt(createdAt);
    }

    private void validar(ContasReceberRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da conta a receber sao obrigatorios");
        }

        if (data.empresa() == null) {
            throw new ValidacaoException("Empresa e obrigatoria");
        }

        if (data.cliente() == null) {
            throw new ValidacaoException("Cliente e obrigatorio");
        }

        if (data.valorOriginal() == null || data.valorOriginal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Valor original deve ser maior que zero");
        }

        if (data.valorRecebido() != null && data.valorRecebido().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Valor recebido nao pode ser negativo");
        }

        if (data.valorRecebido() != null && data.valorRecebido().compareTo(data.valorOriginal()) > 0) {
            throw new ValidacaoException("Valor recebido nao pode ser maior que o valor original");
        }

        if (data.dataEmissao() != null && data.dataVencimento() != null
                && data.dataVencimento().isBefore(data.dataEmissao())) {
            throw new ValidacaoException("Data de vencimento nao pode ser anterior a data de emissao");
        }

        if (nvl(data.valorRecebido()).compareTo(BigDecimal.ZERO) > 0 && data.dataRecebimento() == null) {
            throw new ValidacaoException("Data de recebimento e obrigatoria quando houver valor recebido");
        }

        if (nvl(data.valorRecebido()).compareTo(BigDecimal.ZERO) == 0 && data.dataRecebimento() != null) {
            throw new ValidacaoException("Data de recebimento nao deve ser informada sem valor recebido");
        }
    }

    private Empresas buscarEmpresa(Integer empresaId) {
        Empresas empresa = empresasRepository.findById(empresaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));

        String situacao = empresa.getSituacao() == null ? null : empresa.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Empresa precisa estar ativa para uso financeiro");
        }

        return empresa;
    }

    private Parceiros buscarCliente(Integer clienteId) {
        Parceiros cliente = parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        String situacao = cliente.getSituacao() == null ? null : cliente.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Cliente precisa estar ativo para contas a receber");
        }

        String tipoParceiro = cliente.getTipoParceiro() == null ? null : cliente.getTipoParceiro().trim().toLowerCase();
        if (!"cliente".equals(tipoParceiro)) {
            throw new ValidacaoException("Parceiro informado precisa ser do tipo cliente");
        }

        return cliente;
    }

    private CentrosCusto buscarCentroCusto(Integer centroCustoId) {
        if (centroCustoId == null) {
            return null;
        }

        return centrosCustoRepository.findById(centroCustoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Centro de custo nao encontrado"));
    }

    private String definirStatus(BigDecimal valorOriginal, BigDecimal valorRecebido) {
        BigDecimal recebido = nvl(valorRecebido);

        if (recebido.compareTo(BigDecimal.ZERO) == 0) {
            return "pendente";
        }

        if (recebido.compareTo(valorOriginal) < 0) {
            return "parcial";
        }

        return "pago";
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
