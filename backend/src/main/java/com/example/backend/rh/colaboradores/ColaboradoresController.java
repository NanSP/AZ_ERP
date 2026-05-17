package com.example.backend.rh.colaboradores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/colaboradores")
public class ColaboradoresController {

    private final ColaboradoresRepository repository;

    public ColaboradoresController(ColaboradoresRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ColaboradoresResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ColaboradoresResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ColaboradoresResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveColaborador(@RequestBody ColaboradoresRequestDTO data) {
        Colaboradores entity = new Colaboradores(data);
        Colaboradores saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ColaboradoresResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateColaborador(@PathVariable Integer id, @RequestBody ColaboradoresRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setCodigo(data.codigo());
                    entity.setNome(data.nome());
                    entity.setCpf(data.cpf());
                    entity.setRg(data.rg());
                    entity.setDataNascimento(data.dataNascimento());
                    entity.setSexo(data.sexo());
                    entity.setEstadoCivil(data.estadoCivil());
                    entity.setNacionalidade(data.nacionalidade());
                    entity.setEmailPessoal(data.emailPessoal());
                    entity.setEmailCorporativo(data.emailCorporativo());
                    entity.setTelefone(data.telefone());
                    entity.setCelular(data.celular());
                    entity.setDataAdmissao(data.dataAdmissao());
                    entity.setDataDemissao(data.dataDemissao());
                    entity.setCargo(data.cargo());
                    entity.setDepartamento(data.departamento());
                    entity.setSalario(data.salario());
                    entity.setTipoContrato(data.tipoContrato());
                    entity.setJornadaSemanal(data.jornadaSemanal());
                    entity.setSituacao(data.situacao());
                    entity.setCreatedAt(data.createdAt());

                    Colaboradores updated = repository.save(entity);
                    return ResponseEntity.ok(new ColaboradoresResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColaborador(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Colaborador deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}