package com.example.backend.core.parceiros;

import com.example.backend.fi.contasPagar.ContasPagarRepository;
import com.example.backend.fi.contasReceber.ContasReceberRepository;
import com.example.backend.fiscal.documentos.DocumentosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.sd.contratos.ContratosRepository;
import com.example.backend.sd.oportunidades.OportunidadesRepository;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ParceirosService {

    private final ParceirosRepository repository;
    private final ContasPagarRepository contasPagarRepository;
    private final ContasReceberRepository contasReceberRepository;
    private final PedidosRepository pedidosRepository;
    private final ContratosRepository contratosRepository;
    private final OportunidadesRepository oportunidadesRepository;
    private final ClientesRepository clientesRepository;
    private final OrdensServicoRepository ordensServicoRepository;
    private final DocumentosRepository documentosRepository;

    public ParceirosService(
            ParceirosRepository repository,
            ContasPagarRepository contasPagarRepository,
            ContasReceberRepository contasReceberRepository,
            PedidosRepository pedidosRepository,
            ContratosRepository contratosRepository,
            OportunidadesRepository oportunidadesRepository,
            ClientesRepository clientesRepository,
            OrdensServicoRepository ordensServicoRepository,
            DocumentosRepository documentosRepository
    ) {
        this.repository = repository;
        this.contasPagarRepository = contasPagarRepository;
        this.contasReceberRepository = contasReceberRepository;
        this.pedidosRepository = pedidosRepository;
        this.contratosRepository = contratosRepository;
        this.oportunidadesRepository = oportunidadesRepository;
        this.clientesRepository = clientesRepository;
        this.ordensServicoRepository = ordensServicoRepository;
        this.documentosRepository = documentosRepository;
    }

    @Transactional
    public Parceiros criar(ParceirosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarOpcional(data.codigo()));
        validarDocumentoDuplicadoParaCriacao(normalizarOpcional(data.documento()));

        Parceiros entity = new Parceiros();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Parceiros atualizar(Integer id, ParceirosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarOpcional(data.codigo()), id);
        validarDocumentoDuplicadoParaAtualizacao(normalizarOpcional(data.documento()), id);

        Parceiros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parceiro nao encontrado"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Parceiros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parceiro nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Parceiros entity,
            ParceirosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setTipoParceiro(normalizarTipoParceiro(data.tipoParceiro()));
        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do parceiro e obrigatorio"));
        entity.setNomeFantasia(normalizarOpcional(data.nomeFantasia()));
        entity.setDocumento(normalizarOpcional(data.documento()));
        entity.setTipoPessoa(normalizarTipoPessoa(data.tipoPessoa()));
        entity.setSituacao(normalizarSituacao(data.situacao()));
        entity.setLimiteCredito(data.limiteCredito());
        entity.setDiasPrazo(data.diasPrazo());
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ParceirosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do parceiro sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome do parceiro e obrigatorio");

        validarTipoParceiro(normalizarTipoParceiro(data.tipoParceiro()));
        validarTipoPessoa(normalizarTipoPessoa(data.tipoPessoa()));
        validarSituacao(normalizarSituacao(data.situacao()));
        validarDocumento(normalizarOpcional(data.documento()), normalizarTipoPessoa(data.tipoPessoa()));
        validarNaoNegativo(data.limiteCredito(), "Limite de credito nao pode ser negativo");

        if (data.diasPrazo() != null && data.diasPrazo() < 0) {
            throw new ValidacaoException("Dias de prazo nao pode ser negativo");
        }
    }

    private void validarTipoParceiro(String tipoParceiro) {
        if (!tipoParceiro.equals("cliente")
                && !tipoParceiro.equals("fornecedor")
                && !tipoParceiro.equals("transportadora")
                && !tipoParceiro.equals("representante")) {
            throw new ValidacaoException("Tipo de parceiro invalido");
        }
    }

    private void validarTipoPessoa(String tipoPessoa) {
        if (tipoPessoa == null) {
            return;
        }

        if (!tipoPessoa.equals("F") && !tipoPessoa.equals("J")) {
            throw new ValidacaoException("Tipo de pessoa invalido");
        }
    }

    private void validarSituacao(String situacao) {
        if (!situacao.equals("ativo")
                && !situacao.equals("inativo")
                && !situacao.equals("bloqueado")) {
            throw new ValidacaoException("Situacao invalida");
        }
    }

    private void validarDocumento(String documento, String tipoPessoa) {
        if (documento == null) {
            return;
        }

        if (tipoPessoa == null) {
            throw new ValidacaoException("Tipo de pessoa e obrigatorio quando houver documento");
        }

        if (tipoPessoa.equals("F") && !documento.matches("\\d{11}")) {
            throw new ValidacaoException("CPF deve conter 11 digitos numericos");
        }

        if (tipoPessoa.equals("J") && !documento.matches("\\d{14}")) {
            throw new ValidacaoException("CNPJ deve conter 14 digitos numericos");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe parceiro com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe parceiro com o codigo informado");
        }
    }

    private void validarDocumentoDuplicadoParaCriacao(String documento) {
        if (documento != null && repository.existsByDocumento(documento)) {
            throw new ValidacaoException("Ja existe parceiro com o documento informado");
        }
    }

    private void validarDocumentoDuplicadoParaAtualizacao(String documento, Integer id) {
        if (documento != null && repository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ValidacaoException("Ja existe parceiro com o documento informado");
        }
    }

    private void validarExclusao(Parceiros entity) {
        if (parceiroEmUso(entity.getId())) {
            throw new ValidacaoException("Nao e permitido excluir parceiro com uso operacional");
        }
    }

    private void validarAlteracoesSensiveis(Parceiros entity, ParceirosRequestDTO data) {
        if (!parceiroEmUso(entity.getId())) {
            return;
        }

        String novoTipoParceiro = normalizarTipoParceiro(data.tipoParceiro());
        String novoTipoPessoa = normalizarTipoPessoa(data.tipoPessoa());
        String novoDocumento = normalizarOpcional(data.documento());

        if (!novoTipoParceiro.equals(normalizarTipoParceiro(entity.getTipoParceiro()))) {
            throw new ValidacaoException("Nao e permitido alterar o tipo do parceiro que ja possui uso operacional");
        }

        if (!mesmoTexto(novoTipoPessoa, normalizarTipoPessoa(entity.getTipoPessoa()))) {
            throw new ValidacaoException("Nao e permitido alterar o tipo de pessoa do parceiro que ja possui uso operacional");
        }

        if (!mesmoTexto(novoDocumento, normalizarOpcional(entity.getDocumento()))) {
            throw new ValidacaoException("Nao e permitido alterar o documento do parceiro que ja possui uso operacional");
        }
    }

    private boolean parceiroEmUso(Integer parceiroId) {
        return contasPagarRepository.existsByFornecedorId(parceiroId)
                || contasReceberRepository.existsByClienteId(parceiroId)
                || pedidosRepository.existsByClienteId(parceiroId)
                || contratosRepository.existsByClienteId(parceiroId)
                || oportunidadesRepository.existsByClienteId(parceiroId)
                || clientesRepository.existsByParceiroId(parceiroId)
                || ordensServicoRepository.existsByClienteId(parceiroId)
                || documentosRepository.existsByClienteId(parceiroId);
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarTipoParceiro(String tipoParceiro) {
        if (tipoParceiro == null || tipoParceiro.isBlank()) {
            throw new ValidacaoException("Tipo de parceiro e obrigatorio");
        }

        return tipoParceiro.trim().toLowerCase();
    }

    private String normalizarTipoPessoa(String tipoPessoa) {
        String valor = normalizarOpcional(tipoPessoa);
        return valor == null ? null : valor.toUpperCase();
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

    private boolean mesmoTexto(String valor1, String valor2) {
        if (valor1 == null) {
            return valor2 == null;
        }

        return valor1.equals(valor2);
    }
}
