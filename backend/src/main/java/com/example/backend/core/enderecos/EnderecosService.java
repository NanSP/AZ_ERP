package com.example.backend.core.enderecos;

import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class EnderecosService {

    private final EnderecosRepository repository;
    private final EmpresasRepository empresasRepository;
    private final ParceirosRepository parceirosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public EnderecosService(
            EnderecosRepository repository,
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
    public Enderecos criar(EnderecosRequestDTO data) {
        validar(data);
        validarEntidade(data.entidadeTipo(), data.entidadeId());

        Enderecos entity = new Enderecos();
        preencher(entity, data, LocalDateTime.now());
        ajustarPrincipalSeNecessario(entity);

        return repository.save(entity);
    }

    @Transactional
    public Enderecos atualizar(Integer id, EnderecosRequestDTO data) {
        validar(data);
        validarEntidade(data.entidadeTipo(), data.entidadeId());

        Enderecos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco nao encontrado"));

        validarAlteracaoVinculo(entity, data);
        preencher(entity, data, entity.getCreatedAt());
        ajustarPrincipalSeNecessario(entity);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Enderecos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Enderecos entity,
            EnderecosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setEntidadeTipo(normalizarEntidadeTipo(data.entidadeTipo()));
        entity.setEntidadeId(data.entidadeId());
        entity.setTipoEndereco(normalizarTipoEndereco(data.tipoEndereco()));
        entity.setLogradouro(normalizarOpcional(data.logradouro()));
        entity.setNumero(normalizarOpcional(data.numero()));
        entity.setComplemento(normalizarOpcional(data.complemento()));
        entity.setBairro(normalizarOpcional(data.bairro()));
        entity.setCidade(normalizarOpcional(data.cidade()));
        entity.setUf(normalizarUf(data.uf()));
        entity.setCep(normalizarCep(data.cep()));
        entity.setPais(normalizarPais(data.pais()));
        entity.setPrincipal(Boolean.TRUE.equals(data.principal()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(EnderecosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do endereco sao obrigatorios");
        }

        String entidadeTipo = normalizarEntidadeTipo(data.entidadeTipo());
        validarEntidadeTipo(entidadeTipo);

        if (data.entidadeId() == null) {
            throw new ValidacaoException("Entidade e obrigatoria");
        }

        validarTipoEndereco(normalizarTipoEndereco(data.tipoEndereco()));

        String uf = normalizarUf(data.uf());
        if (uf != null && !uf.matches("[A-Z]{2}")) {
            throw new ValidacaoException("UF deve conter 2 letras");
        }

        String cep = normalizarCep(data.cep());
        if (cep != null && !cep.matches("\\d{8}")) {
            throw new ValidacaoException("CEP deve conter 8 digitos numericos");
        }
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
            throw new RecursoNaoEncontradoException("Entidade do endereco nao encontrada");
        }
    }

    private void ajustarPrincipalSeNecessario(Enderecos entity) {
        if (!Boolean.TRUE.equals(entity.getPrincipal())) {
            return;
        }

        repository.clearPrincipalByEntidade(
                entity.getEntidadeTipo(),
                entity.getEntidadeId(),
                entity.getId()
        );
    }

    private void validarAlteracaoVinculo(Enderecos entity, EnderecosRequestDTO data) {
        String entidadeTipoAtual = normalizarEntidadeTipo(entity.getEntidadeTipo());
        String novoEntidadeTipo = normalizarEntidadeTipo(data.entidadeTipo());

        if (!entidadeTipoAtual.equals(novoEntidadeTipo)
                || !entity.getEntidadeId().equals(data.entidadeId())) {
            throw new ValidacaoException("Nao e permitido alterar o vinculo do endereco com a entidade");
        }
    }

    private void validarExclusao(Enderecos entity) {
        if (Boolean.TRUE.equals(entity.getPrincipal())) {
            throw new ValidacaoException("Nao e permitido excluir endereco principal");
        }
    }

    private void validarEntidadeTipo(String entidadeTipo) {
        if (!entidadeTipo.equals("empresa")
                && !entidadeTipo.equals("parceiro")
                && !entidadeTipo.equals("colaborador")) {
            throw new ValidacaoException("Tipo de entidade invalido");
        }
    }

    private void validarTipoEndereco(String tipoEndereco) {
        if (!tipoEndereco.equals("comercial")
                && !tipoEndereco.equals("cobranca")
                && !tipoEndereco.equals("entrega")
                && !tipoEndereco.equals("residencial")) {
            throw new ValidacaoException("Tipo de endereco invalido");
        }
    }

    private String normalizarEntidadeTipo(String entidadeTipo) {
        if (entidadeTipo == null || entidadeTipo.isBlank()) {
            throw new ValidacaoException("Tipo de entidade e obrigatorio");
        }

        return entidadeTipo.trim().toLowerCase();
    }

    private String normalizarTipoEndereco(String tipoEndereco) {
        String valor = normalizarOpcional(tipoEndereco);
        return valor == null ? "comercial" : valor.toLowerCase();
    }

    private String normalizarUf(String uf) {
        String valor = normalizarOpcional(uf);
        return valor == null ? null : valor.toUpperCase(Locale.ROOT);
    }

    private String normalizarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return null;
        }

        return cep.replaceAll("\\D", "");
    }

    private String normalizarPais(String pais) {
        String valor = normalizarOpcional(pais);
        return valor == null ? "BRASIL" : valor.toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
