package com.example.backend.fi.planoContas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fi/planoContas")
public class PlanoContasController {

    private final PlanoContasRepository repository;

    public PlanoContasController(PlanoContasRepository repository) {
        this.repository = repository;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PlanoContasResponseDTO> getAll(){

        List<PlanoContasResponseDTO> planoContasList = repository.findAll().stream().map(PlanoContasResponseDTO::new).toList();
        return planoContasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<PlanoContas> planoContas = repository.findById(id);
        if(planoContas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PlanoContasResponseDTO planoContasDTO = new PlanoContasResponseDTO(planoContas.get());
        return  ResponseEntity.ok(planoContasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public ResponseEntity<?> savePlanoContas(@RequestBody PlanoContasRequestDTO data) {
        try {
            PlanoContas contaPai = null;

            if (data.contaPai() != null) {
                contaPai = repository.findById(data.contaPai())
                        .orElseThrow(() -> new RuntimeException("Conta pai nao encontrada"));
            }

            PlanoContas entity = new PlanoContas();
            entity.setCodigo(data.codigo());
            entity.setNome(data.nome());
            entity.setTipoConta(data.tipoConta());
            entity.setNatureza(data.natureza());
            entity.setContaPai(contaPai);
            entity.setSituacao(data.situacao());

            PlanoContas saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PlanoContasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlanoContas(@PathVariable Integer id, @RequestBody PlanoContasRequestDTO data) {
        try {
            PlanoContas entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Plano de contas nao encontrado"));

            PlanoContas contaPai = null;

            if (data.contaPai() != null) {
                contaPai = repository.findById(data.contaPai())
                        .orElseThrow(() -> new RuntimeException("Conta pai nao encontrada"));
            }

            entity.setCodigo(data.codigo());
            entity.setNome(data.nome());
            entity.setTipoConta(data.tipoConta());
            entity.setNatureza(data.natureza());
            entity.setContaPai(contaPai);
            entity.setSituacao(data.situacao());

            PlanoContas updated = repository.save(entity);
            return ResponseEntity.ok(new PlanoContasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlanoContas(@PathVariable(value = "id") Integer id){

        Optional<PlanoContas> planoContas = repository.findById(id);
        if(planoContas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(planoContas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Plano Contas deleted");
    }
}
