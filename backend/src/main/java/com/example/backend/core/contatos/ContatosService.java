package com.example.backend.core.contatos;

import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContatosService {

    private final ContatosRepository repository;
    private final EmpresasRepository empresasRepository;
    private final ParceirosRepository parceirosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ContatosService(
            ContatosRepository repository,
            EmpresasRepository empresasRepository,
            ParceirosRepository parceirosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.empresasRepository = empresasRepository;
        this.parceirosRepository = parceirosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Contatos criar(ContatosRequestDTO data) {
        validar(data);
        validarEntidade(data.entidadeTipo(), data.entidadeId());

        Contatos entity = new Contatos();
        preencher(entity, data, LocalDateTime.now());
        ajustarPrincipalSeNecessario(entity);

        return repository.save(entity);
    }

    @Transactional
    public Contatos atualizar(Integer id, ContatosRequestDTO data) {
        validar(data);
        validarEntidade(data.entidadeTipo(), data.entidadeId());

        Contatos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato nao encontrado"));

        validarAlteracaoVinculo(entity, data);
        preencher(entity, data, entity.getCreatedAt());
        ajustarPrincipalSeNecessario(entity);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Contatos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Contatos entity,
            ContatosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setEntidadeTipo(normalizarEntidadeTipo(data.entidadeTipo()));
        entity.setEntidadeId(data.entidadeId());
        entity.setTipoContato(normalizarTipoContato(data.tipoContato()));
        entity.setValor(normalizarObrigatorio(data.valor(), "Valor do contato e obrigatorio"));
        entity.setPrincipal(Boolean.TRUE.equals(data.principal()));
        entity.setObservacao(normalizarOpcional(data.observacao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ContatosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do contato sao obrigatorios");
        }

        String entidadeTipo = normalizarEntidadeTipo(data.entidadeTipo());
        validarEntidadeTipo(entidadeTipo);

        if (data.entidadeId() == null) {
            throw new ValidacaoException("Entidade e obrigatoria");
        }

        String tipoContato = normalizarTipoContato(data.tipoContato());
        validarTipoContato(tipoContato);

        String valor = normalizarObrigatorio(data.valor(), "Valor do contato e obrigatorio");
        validarValorPorTipo(tipoContato, valor);
    }

    private void validarEntidade(String entidadeTipo, Integer entidadeId) {
        String tipo = normalizarEntidadeTipo(entidadeTipo);

        boolean existe = switch (tipo) {
            case "empresa" -> empresasRepository.existsById(entidadeId);
            case "parceiro" -> parceirosRepository.existsById(entidadeId);
            case "colaborador" -> colaboradoresRepository.existsById(entidadeId);
            default -> false;
        };

        if (!existe) {
            throw new RecursoNaoEncontradoException("Entidade do contato nao encontrada");
        }
    }

    private void ajustarPrincipalSeNecessario(Contatos entity) {
        if (!Boolean.TRUE.equals(entity.getPrincipal())) {
            return;
        }

        repository.clearPrincipalByEntidadeAndTipoContato(
                entity.getEntidadeTipo(),
                entity.getEntidadeId(),
                entity.getTipoContato(),
                entity.getId()
        );
    }

    private void validarAlteracaoVinculo(Contatos entity, ContatosRequestDTO data) {
        String entidadeTipoAtual = normalizarEntidadeTipo(entity.getEntidadeTipo());
        String novoEntidadeTipo = normalizarEntidadeTipo(data.entidadeTipo());

        if (!entidadeTipoAtual.equals(novoEntidadeTipo)
                || !entity.getEntidadeId().equals(data.entidadeId())) {
            throw new ValidacaoException("Nao e permitido alterar o vinculo do contato com a entidade");
        }
    }

    private void validarExclusao(Contatos entity) {
        if (!Boolean.TRUE.equals(entity.getPrincipal())) {
            return;
        }

        repository.findFirstByEntidadeTipoAndEntidadeIdAndTipoContatoAndIdNotOrderByIdAsc(
                entity.getEntidadeTipo(),
                entity.getEntidadeId(),
                entity.getTipoContato(),
                entity.getId()
        ).ifPresent(contato -> contato.setPrincipal(true));
    }

    private void validarEntidadeTipo(String entidadeTipo) {
        if (!entidadeTipo.equals("empresa")
                && !entidadeTipo.equals("parceiro")
                && !entidadeTipo.equals("colaborador")) {
            throw new ValidacaoException("Tipo de entidade invalido");
        }
    }

    private void validarTipoContato(String tipoContato) {
        if (!tipoContato.equals("telefone")
                && !tipoContato.equals("email")
                && !tipoContato.equals("whatsapp")
                && !tipoContato.equals("site")) {
            throw new ValidacaoException("Tipo de contato invalido");
        }
    }

    private void validarValorPorTipo(String tipoContato, String valor) {
        switch (tipoContato) {
            case "email" -> {
                if (!valor.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    throw new ValidacaoException("Email invalido");
                }
            }
            case "site" -> {
                if (!valor.matches("^(https?://).+")) {
                    throw new ValidacaoException("Site deve iniciar com http:// ou https://");
                }
            }
            case "telefone" -> {
                String numerico = valor.replaceAll("\\D", "");
                if (numerico.length() < 10 || numerico.length() > 11) {
                    throw new ValidacaoException("Telefone invalido");
                }
            }
            case "whatsapp" -> {
                String numerico = valor.replaceAll("\\D", "");
                if (numerico.length() < 11 || numerico.length() > 13) {
                    throw new ValidacaoException("WhatsApp invalido");
                }
            }
            default -> throw new ValidacaoException("Tipo de contato invalido");
        }
    }

    private String normalizarEntidadeTipo(String entidadeTipo) {
        if (entidadeTipo == null || entidadeTipo.isBlank()) {
            throw new ValidacaoException("Tipo de entidade e obrigatorio");
        }

        return entidadeTipo.trim().toLowerCase();
    }

    private String normalizarTipoContato(String tipoContato) {
        if (tipoContato == null || tipoContato.isBlank()) {
            throw new ValidacaoException("Tipo de contato e obrigatorio");
        }

        return tipoContato.trim().toLowerCase();
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
