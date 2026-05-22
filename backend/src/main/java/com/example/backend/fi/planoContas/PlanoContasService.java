package com.example.backend.fi.planoContas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PlanoContasService {

    private final PlanoContasRepository repository;

    public PlanoContasService(PlanoContasRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PlanoContas criar(PlanoContasRequestDTO data) {
        validar(data);

        PlanoContas contaPai = buscarContaPai(data.contaPai(), null);

        PlanoContas entity = new PlanoContas();
        preencher(entity, data, contaPai);

        return repository.save(entity);
    }

    @Transactional
    public PlanoContas atualizar(Integer id, PlanoContasRequestDTO data) {
        validar(data);

        PlanoContas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de contas nao encontrado"));

        PlanoContas contaPai = buscarContaPai(data.contaPai(), id);
        preencher(entity, data, contaPai);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        PlanoContas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de contas nao encontrado"));

        if (entity.getContasFilhas() != null && !entity.getContasFilhas().isEmpty()) {
            throw new ValidacaoException("Nao e permitido excluir conta que possui contas filhas");
        }

        repository.delete(entity);
    }

    private void preencher(PlanoContas entity, PlanoContasRequestDTO data, PlanoContas contaPai) {
        entity.setCodigo(data.codigo().trim());
        entity.setNome(data.nome().trim());
        entity.setTipoConta(normalizar(data.tipoConta()));
        entity.setNatureza(normalizar(data.natureza()));
        entity.setContaPai(contaPai);
        entity.setSituacao(normalizarSituacao(data.situacao()));
    }

    private void validar(PlanoContasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do plano de contas sao obrigatorios");
        }

        if (data.codigo() == null || data.codigo().isBlank()) {
            throw new ValidacaoException("Codigo e obrigatorio");
        }

        if (data.nome() == null || data.nome().isBlank()) {
            throw new ValidacaoException("Nome e obrigatorio");
        }

        String tipoConta = normalizar(data.tipoConta());
        if (!tipoConta.equals("analitica") && !tipoConta.equals("sintetica")) {
            throw new ValidacaoException("Tipo de conta invalido");
        }

        String natureza = normalizar(data.natureza());
        if (!natureza.equals("devedora") && !natureza.equals("credora")) {
            throw new ValidacaoException("Natureza invalida");
        }

        String situacao = normalizarSituacao(data.situacao());
        if (!situacao.equals("ativo") && !situacao.equals("inativo")) {
            throw new ValidacaoException("Situacao invalida");
        }
    }

    private PlanoContas buscarContaPai(Integer contaPaiId, Integer contaAtualId) {
        if (contaPaiId == null) {
            return null;
        }

        if (contaAtualId != null && contaPaiId.equals(contaAtualId)) {
            throw new ValidacaoException("Conta nao pode ser pai dela mesma");
        }

        return repository.findById(contaPaiId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta pai nao encontrada"));
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }

    private String normalizarSituacao(String situacao) {
        String valor = normalizar(situacao);
        return valor.isBlank() ? "ativo" : valor;
    }
}
