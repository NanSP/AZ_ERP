package com.example.backend.fiscal.documentos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DocumentosService {

    private final DocumentosRepository repository;
    private final PedidosRepository pedidosRepository;
    private final ParceirosRepository parceirosRepository;

    public DocumentosService(
            DocumentosRepository repository,
            PedidosRepository pedidosRepository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
        this.parceirosRepository = parceirosRepository;
    }

    @Transactional
    public Documentos criar(DocumentosRequestDTO data) {
        validar(data);
        validarChaveDuplicadaParaCriacao(normalizarOpcional(data.chaveAcesso()));

        Pedidos pedido = buscarPedidoOpcional(data.pedido());
        Parceiros cliente = buscarClienteOpcional(data.cliente());

        validarRelacionamentos(data, pedido, cliente);

        Documentos entity = new Documentos();
        preencher(entity, data, pedido, cliente, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Documentos atualizar(Integer id, DocumentosRequestDTO data) {
        validar(data);
        validarChaveDuplicadaParaAtualizacao(normalizarOpcional(data.chaveAcesso()), id);

        Documentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento nao encontrado"));

        Pedidos pedido = buscarPedidoOpcional(data.pedido());
        Parceiros cliente = buscarClienteOpcional(data.cliente());

        validarRelacionamentos(data, pedido, cliente);

        preencher(entity, data, pedido, cliente, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Documentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Documentos entity,
            DocumentosRequestDTO data,
            Pedidos pedido,
            Parceiros cliente,
            LocalDateTime createdAt
    ) {
        entity.setTipoDocumento(normalizarTipoDocumento(data.tipoDocumento()));
        entity.setNumero(normalizarObrigatorio(data.numero(), "Numero do documento e obrigatorio"));
        entity.setSerie(normalizarOpcional(data.serie()));
        entity.setChaveAcesso(normalizarOpcional(data.chaveAcesso()));
        entity.setDataEmissao(data.dataEmissao());
        entity.setPedido(pedido);
        entity.setCliente(cliente);
        entity.setValorTotal(data.valorTotal());
        entity.setStatus(normalizarStatus(data.status()));
        entity.setXml_file(normalizarOpcional(data.xml_file()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(DocumentosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do documento sao obrigatorios");
        }

        validarTipoDocumento(normalizarTipoDocumento(data.tipoDocumento()));
        normalizarObrigatorio(data.numero(), "Numero do documento e obrigatorio");

        if (data.dataEmissao() == null) {
            throw new ValidacaoException("Data de emissao e obrigatoria");
        }

        validarNaoNegativo(data.valorTotal(), "Valor total nao pode ser negativo");
        validarStatus(normalizarStatus(data.status()));
        validarChaveAcesso(normalizarOpcional(data.chaveAcesso()));
    }

    private void validarRelacionamentos(DocumentosRequestDTO data, Pedidos pedido, Parceiros cliente) {
        if (pedido == null && cliente == null) {
            throw new ValidacaoException("Pedido ou cliente deve ser informado");
        }

        if (pedido != null && cliente != null && pedido.getCliente() != null
                && !pedido.getCliente().getId().equals(cliente.getId())) {
            throw new ValidacaoException("Cliente informado diverge do cliente do pedido");
        }

        if (pedido != null) {
            String statusPedido = pedido.getStatus() == null ? null : pedido.getStatus().trim().toLowerCase();
            if ("cancelado".equals(statusPedido)) {
                throw new ValidacaoException("Nao e permitido emitir documento para pedido cancelado");
            }
        }

        String tipoDocumento = normalizarTipoDocumento(data.tipoDocumento());
        if ((tipoDocumento.equals("nfe") || tipoDocumento.equals("nfce"))
                && normalizarOpcional(data.chaveAcesso()) == null) {
            throw new ValidacaoException("Chave de acesso e obrigatoria para NFe e NFCe");
        }

        validarStatusComFluxo(
                normalizarStatus(data.status()),
                tipoDocumento,
                normalizarOpcional(data.chaveAcesso()),
                normalizarOpcional(data.xml_file())
        );
    }

    private void validarTipoDocumento(String tipoDocumento) {
        if (!tipoDocumento.equals("nfe")
                && !tipoDocumento.equals("nfce")
                && !tipoDocumento.equals("cte")
                && !tipoDocumento.equals("nfse")) {
            throw new ValidacaoException("Tipo de documento invalido");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("digitado")
                && !status.equals("emitido")
                && !status.equals("cancelado")
                && !status.equals("inutilizado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarChaveAcesso(String chaveAcesso) {
        if (chaveAcesso == null) {
            return;
        }

        if (!chaveAcesso.matches("\\d{44}")) {
            throw new ValidacaoException("Chave de acesso deve conter 44 digitos numericos");
        }
    }

    private void validarStatusComFluxo(
            String status,
            String tipoDocumento,
            String chaveAcesso,
            String xmlFile
    ) {
        if ("emitido".equals(status) && xmlFile == null) {
            throw new ValidacaoException("XML e obrigatorio para documento emitido");
        }

        boolean documentoEletronico = tipoDocumento.equals("nfe")
                || tipoDocumento.equals("nfce")
                || tipoDocumento.equals("cte");

        if (documentoEletronico
                && ("emitido".equals(status) || "cancelado".equals(status) || "inutilizado".equals(status))
                && chaveAcesso == null) {
            throw new ValidacaoException("Chave de acesso e obrigatoria para este status do documento eletronico");
        }
    }

    private void validarExclusao(Documentos entity) {
        String status = entity.getStatus() == null ? null : entity.getStatus().trim().toLowerCase();

        if ("emitido".equals(status) || "cancelado".equals(status) || "inutilizado".equals(status)) {
            throw new ValidacaoException("Nao e permitido excluir documento em status final");
        }
    }

    private void validarChaveDuplicadaParaCriacao(String chaveAcesso) {
        if (chaveAcesso != null && repository.existsByChaveAcesso(chaveAcesso)) {
            throw new ValidacaoException("Ja existe documento com a chave de acesso informada");
        }
    }

    private void validarChaveDuplicadaParaAtualizacao(String chaveAcesso, Integer id) {
        if (chaveAcesso != null && repository.existsByChaveAcessoAndIdNot(chaveAcesso, id)) {
            throw new ValidacaoException("Ja existe documento com a chave de acesso informada");
        }
    }

    private Pedidos buscarPedidoOpcional(Integer pedidoId) {
        if (pedidoId == null) {
            return null;
        }

        return pedidosRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));
    }

    private Parceiros buscarClienteOpcional(Integer clienteId) {
        if (clienteId == null) {
            return null;
        }

        return parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private String normalizarTipoDocumento(String tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.isBlank()) {
            throw new ValidacaoException("Tipo do documento e obrigatorio");
        }

        return tipoDocumento.trim().toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "digitado" : valor.toLowerCase();
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
