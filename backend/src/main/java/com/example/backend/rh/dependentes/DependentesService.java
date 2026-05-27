package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.rh.folhaDePagamento.FolhaDePagamentoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DependentesService {

    private final DependentesRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;
    private final FolhaDePagamentoRepository folhaDePagamentoRepository;

    public DependentesService(
            DependentesRepository repository,
            ColaboradoresRepository colaboradoresRepository,
            FolhaDePagamentoRepository folhaDePagamentoRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
        this.folhaDePagamentoRepository = folhaDePagamentoRepository;
    }

    @Transactional
    public Dependentes criar(DependentesRequestDTO data) {
        validar(data);
        validarCpfDuplicadoParaCriacao(normalizarCpfOpcional(data.cpf()));
        validarDuplicidadeDependenteParaCriacao(data.colaborador(), normalizarObrigatorio(data.nome(), "Nome do dependente e obrigatorio"), data.dataNascimento());

        Colaboradores colaborador = buscarColaborador(data.colaborador());

        Dependentes entity = new Dependentes();
        preencher(entity, data, colaborador, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Dependentes atualizar(Integer id, DependentesRequestDTO data) {
        validar(data);
        validarCpfDuplicadoParaAtualizacao(normalizarCpfOpcional(data.cpf()), id);
        validarDuplicidadeDependenteParaAtualizacao(
                data.colaborador(),
                normalizarObrigatorio(data.nome(), "Nome do dependente e obrigatorio"),
                data.dataNascimento(),
                id
        );

        Dependentes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dependente nao encontrado"));

        Colaboradores colaborador = buscarColaborador(data.colaborador());
        preencher(entity, data, colaborador, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Dependentes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dependente nao encontrado"));

        if (entity.getColaborador() != null
                && entity.getColaborador().getId() != null
                && folhaDePagamentoRepository.existsByColaboradorId(entity.getColaborador().getId())) {
            throw new ValidacaoException("Nao e permitido excluir dependente de colaborador com historico de folha de pagamento");
        }

        repository.delete(entity);
    }

    private void preencher(
            Dependentes entity,
            DependentesRequestDTO data,
            Colaboradores colaborador,
            LocalDateTime createdAt
    ) {
        entity.setColaborador(colaborador);
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do dependente e obrigatorio"));
        entity.setDataNascimento(data.dataNascimento());
        entity.setParentesco(normalizarParentesco(data.parentesco()));
        entity.setCpf(normalizarCpfOpcional(data.cpf()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(DependentesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do dependente sao obrigatorios");
        }

        if (data.colaborador() == null) {
            throw new ValidacaoException("Colaborador e obrigatorio");
        }

        normalizarObrigatorio(data.nome(), "Nome do dependente e obrigatorio");

        if (data.dataNascimento() != null && data.dataNascimento().isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data de nascimento nao pode estar no futuro");
        }

        validarParentesco(normalizarParentesco(data.parentesco()));

        String cpf = normalizarCpfOpcional(data.cpf());
        if (cpf != null) {
            validarCpf(cpf);
        }
    }

    private void validarParentesco(String parentesco) {
        if (parentesco == null) {
            return;
        }

        if (!parentesco.equals("filho")
                && !parentesco.equals("filha")
                && !parentesco.equals("conjuge")
                && !parentesco.equals("enteado")
                && !parentesco.equals("enteada")
                && !parentesco.equals("outro")) {
            throw new ValidacaoException("Parentesco invalido");
        }
    }

    private void validarCpfDuplicadoParaCriacao(String cpf) {
        if (cpf != null && repository.existsByCpf(cpf)) {
            throw new ValidacaoException("Ja existe dependente com o CPF informado");
        }
    }

    private void validarCpfDuplicadoParaAtualizacao(String cpf, Integer id) {
        if (cpf != null && repository.existsByCpfAndIdNot(cpf, id)) {
            throw new ValidacaoException("Ja existe dependente com o CPF informado");
        }
    }

    private void validarDuplicidadeDependenteParaCriacao(Integer colaboradorId, String nome, LocalDate dataNascimento) {
        if (dataNascimento != null
                && repository.existsByColaboradorIdAndNomeIgnoreCaseAndDataNascimento(colaboradorId, nome, dataNascimento)) {
            throw new ValidacaoException("Ja existe dependente com o mesmo nome e data de nascimento para este colaborador");
        }
    }

    private void validarDuplicidadeDependenteParaAtualizacao(Integer colaboradorId, String nome, LocalDate dataNascimento, Integer id) {
        if (dataNascimento != null
                && repository.existsByColaboradorIdAndNomeIgnoreCaseAndDataNascimentoAndIdNot(colaboradorId, nome, dataNascimento, id)) {
            throw new ValidacaoException("Ja existe dependente com o mesmo nome e data de nascimento para este colaborador");
        }
    }

    private Colaboradores buscarColaborador(Integer colaboradorId) {
        return colaboradoresRepository.findById(colaboradorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));
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

    private String normalizarParentesco(String parentesco) {
        String valor = normalizarOpcional(parentesco);
        return valor == null ? null : valor.toLowerCase();
    }

    private String normalizarCpfOpcional(String cpf) {
        String valor = normalizarOpcional(cpf);
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    private void validarCpf(String cpf) {
        if (!cpf.matches("\\d{11}")) {
            throw new ValidacaoException("CPF deve conter 11 digitos numericos");
        }

        if (cpf.chars().distinct().count() == 1) {
            throw new ValidacaoException("CPF invalido");
        }

        if (!digitosCpfValidos(cpf)) {
            throw new ValidacaoException("CPF invalido");
        }
    }

    private boolean digitosCpfValidos(String cpf) {
        int primeiroDigito = calcularDigitoCpf(cpf, 9, 10);
        int segundoDigito = calcularDigitoCpf(cpf, 10, 11);

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9))
                && segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    private int calcularDigitoCpf(String cpf, int tamanhoBase, int pesoInicial) {
        int soma = 0;

        for (int i = 0; i < tamanhoBase; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
