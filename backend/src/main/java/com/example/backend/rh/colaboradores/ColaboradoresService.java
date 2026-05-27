package com.example.backend.rh.colaboradores;

import com.example.backend.rh.beneficios.BeneficiosRepository;
import com.example.backend.rh.controleDePonto.ControleDePontoRepository;
import com.example.backend.rh.dependentes.DependentesRepository;
import com.example.backend.rh.folhaDePagamento.FolhaDePagamentoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ColaboradoresService {

    private final ColaboradoresRepository repository;
    private final DependentesRepository dependentesRepository;
    private final BeneficiosRepository beneficiosRepository;
    private final ControleDePontoRepository controleDePontoRepository;
    private final FolhaDePagamentoRepository folhaDePagamentoRepository;

    public ColaboradoresService(
            ColaboradoresRepository repository,
            DependentesRepository dependentesRepository,
            BeneficiosRepository beneficiosRepository,
            ControleDePontoRepository controleDePontoRepository,
            FolhaDePagamentoRepository folhaDePagamentoRepository
    ) {
        this.repository = repository;
        this.dependentesRepository = dependentesRepository;
        this.beneficiosRepository = beneficiosRepository;
        this.controleDePontoRepository = controleDePontoRepository;
        this.folhaDePagamentoRepository = folhaDePagamentoRepository;
    }

    @Transactional
    public Colaboradores criar(ColaboradoresRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarOpcional(data.codigo()));
        validarCpfDuplicadoParaCriacao(normalizarObrigatorio(data.cpf(), "CPF e obrigatorio"));

        Colaboradores entity = new Colaboradores();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Colaboradores atualizar(Integer id, ColaboradoresRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarOpcional(data.codigo()), id);
        validarCpfDuplicadoParaAtualizacao(normalizarObrigatorio(data.cpf(), "CPF e obrigatorio"), id);

        Colaboradores entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Colaboradores entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador nao encontrado"));

        if (dependentesRepository.existsByColaboradorId(id)) {
            throw new ValidacaoException("Nao e permitido excluir colaborador que possui dependentes");
        }

        if (beneficiosRepository.existsByColaboradorId(id)) {
            throw new ValidacaoException("Nao e permitido excluir colaborador que possui beneficios");
        }

        if (controleDePontoRepository.existsByColaboradorId(id)) {
            throw new ValidacaoException("Nao e permitido excluir colaborador que possui registros de ponto");
        }

        if (folhaDePagamentoRepository.existsByColaboradorId(id)) {
            throw new ValidacaoException("Nao e permitido excluir colaborador que possui registros de folha de pagamento");
        }

        repository.delete(entity);
    }

    private void preencher(
            Colaboradores entity,
            ColaboradoresRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome e obrigatorio"));
        entity.setCpf(normalizarObrigatorio(data.cpf(), "CPF e obrigatorio"));
        entity.setRg(normalizarOpcional(data.rg()));
        entity.setDataNascimento(data.dataNascimento());
        entity.setSexo(normalizarSexo(data.sexo()));
        entity.setEstadoCivil(normalizarOpcional(data.estadoCivil()));
        entity.setNacionalidade(normalizarOpcional(data.nacionalidade()));
        entity.setEmailPessoal(normalizarOpcional(data.emailPessoal()));
        entity.setEmailCorporativo(normalizarOpcional(data.emailCorporativo()));
        entity.setTelefone(normalizarOpcional(data.telefone()));
        entity.setCelular(normalizarOpcional(data.celular()));
        entity.setDataAdmissao(data.dataAdmissao());
        entity.setDataDemissao(data.dataDemissao());
        entity.setCargo(normalizarOpcional(data.cargo()));
        entity.setDepartamento(normalizarOpcional(data.departamento()));
        entity.setSalario(data.salario());
        entity.setTipoContrato(normalizarOpcional(data.tipoContrato()));
        entity.setJornadaSemanal(data.jornadaSemanal());
        entity.setSituacao(normalizarSituacao(data.situacao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ColaboradoresRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do colaborador sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome e obrigatorio");
        String cpf = normalizarObrigatorio(data.cpf(), "CPF e obrigatorio");

        validarCpf(cpf);

        validarNaoNegativo(data.salario(), "Salario nao pode ser negativo");

        if (data.jornadaSemanal() != null && data.jornadaSemanal() <= 0) {
            throw new ValidacaoException("Jornada semanal deve ser maior que zero");
        }

        if (data.dataNascimento() != null && data.dataNascimento().isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data de nascimento nao pode estar no futuro");
        }

        if (data.dataAdmissao() != null
                && data.dataDemissao() != null
                && data.dataDemissao().isBefore(data.dataAdmissao())) {
            throw new ValidacaoException("Data de demissao nao pode ser anterior a data de admissao");
        }

        String situacao = normalizarSituacao(data.situacao());
        validarSituacao(situacao, data.dataDemissao());
        validarSexo(normalizarSexo(data.sexo()));
    }

    private void validarSituacao(String situacao, LocalDate dataDemissao) {
        if (!situacao.equals("ativo")
                && !situacao.equals("inativo")
                && !situacao.equals("desligado")) {
            throw new ValidacaoException("Situacao invalida");
        }

        if (situacao.equals("desligado") && dataDemissao == null) {
            throw new ValidacaoException("Data de demissao e obrigatoria para colaborador desligado");
        }
    }

    private void validarSexo(String sexo) {
        if (sexo == null) {
            return;
        }

        if (!sexo.equals("m") && !sexo.equals("f")) {
            throw new ValidacaoException("Sexo invalido");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe colaborador com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe colaborador com o codigo informado");
        }
    }

    private void validarCpfDuplicadoParaCriacao(String cpf) {
        if (repository.existsByCpf(cpf)) {
            throw new ValidacaoException("Ja existe colaborador com o CPF informado");
        }
    }

    private void validarCpfDuplicadoParaAtualizacao(String cpf, Integer id) {
        if (repository.existsByCpfAndIdNot(cpf, id)) {
            throw new ValidacaoException("Ja existe colaborador com o CPF informado");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
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

    private String normalizarSituacao(String situacao) {
        String valor = normalizarOpcional(situacao);
        return valor == null ? "ativo" : valor.toLowerCase();
    }

    private String normalizarSexo(String sexo) {
        String valor = normalizarOpcional(sexo);
        return valor == null ? null : valor.toLowerCase();
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
