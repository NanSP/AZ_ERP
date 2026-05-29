package com.example.backend.bi.relatorios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RelatoriosService {

    private final RelatoriosRepository repository;

    public RelatoriosService(RelatoriosRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Relatorios criar(RelatoriosRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaCriacao(normalizarObrigatorio(data.nome(), "Nome do relatorio e obrigatorio"));

        Relatorios entity = new Relatorios();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Relatorios atualizar(Integer id, RelatoriosRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaAtualizacao(normalizarObrigatorio(data.nome(), "Nome do relatorio e obrigatorio"), id);

        Relatorios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Relatorios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio nao encontrado"));

        throw new ValidacaoException("Relatorio nao pode ser excluido");
    }

    private void preencher(
            Relatorios entity,
            RelatoriosRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do relatorio e obrigatorio"));
        entity.setDescricao(normalizarDescricao(data.descricao()));
        entity.setTipoRelatorio(normalizarTipoRelatorio(data.tipoRelatorio()));
        entity.setQuerySql(normalizarQuerySql(data.querySql()));
        entity.setParametros(data.parametros());
        entity.setCreatedAt(createdAt);
    }

    private void validar(RelatoriosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do relatorio sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome do relatorio e obrigatorio");
        validarDescricao(normalizarDescricao(data.descricao()));

        String tipoRelatorio = normalizarTipoRelatorio(data.tipoRelatorio());
        String querySql = normalizarQuerySql(data.querySql());

        validarTipoRelatorio(tipoRelatorio);
        validarQuerySql(tipoRelatorio, querySql);
        validarParametros(data.parametros());
    }

    private void validarTipoRelatorio(String tipoRelatorio) {
        if (!tipoRelatorio.equals("tabela")
                && !tipoRelatorio.equals("grafico")
                && !tipoRelatorio.equals("indicador")
                && !tipoRelatorio.equals("customizado")) {
            throw new ValidacaoException("Tipo de relatorio invalido");
        }
    }

    private void validarQuerySql(String tipoRelatorio, String querySql) {
        if ((tipoRelatorio.equals("tabela") || tipoRelatorio.equals("grafico") || tipoRelatorio.equals("customizado"))
                && (querySql == null || querySql.isBlank())) {
            throw new ValidacaoException("Query SQL e obrigatoria para o tipo de relatorio informado");
        }

        if (querySql != null) {
            String valor = querySql.trim().toLowerCase();

            if (!valor.startsWith("select")) {
                throw new ValidacaoException("A query SQL do relatorio deve iniciar com SELECT");
            }

            if (valor.contains(" update ")
                    || valor.contains(" delete ")
                    || valor.contains(" insert ")
                    || valor.contains(" drop ")
                    || valor.contains(" alter ")
                    || valor.contains(" truncate ")) {
                throw new ValidacaoException("A query SQL do relatorio possui comandos nao permitidos");
            }
        }
    }

    private void validarParametros(Map<String, Object> parametros) {
        if (parametros == null) {
            return;
        }

        if (parametros.size() > 50) {
            throw new ValidacaoException("Parametros do relatorio excedem o limite permitido");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 500) {
            throw new ValidacaoException("Descricao do relatorio nao pode exceder 500 caracteres");
        }
    }

    private void validarNomeDuplicadoParaCriacao(String nome) {
        if (repository.existsByNome(nome)) {
            throw new ValidacaoException("Ja existe relatorio com o nome informado");
        }
    }

    private void validarNomeDuplicadoParaAtualizacao(String nome, Integer id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new ValidacaoException("Ja existe relatorio com o nome informado");
        }
    }

    private String normalizarTipoRelatorio(String tipoRelatorio) {
        if (tipoRelatorio == null || tipoRelatorio.isBlank()) {
            throw new ValidacaoException("Tipo de relatorio e obrigatorio");
        }

        return tipoRelatorio.trim().toLowerCase();
    }

    private String normalizarQuerySql(String querySql) {
        return normalizarOpcional(querySql);
    }

    private String normalizarDescricao(String descricao) {
        return normalizarOpcional(descricao);
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
