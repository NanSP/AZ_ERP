package com.example.backend.fiscal.documentos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fiscal/documentos")
public class DocumentosController {

    @Autowired
    private DocumentosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<DocumentosResponseDTO> getAll(){

        List<DocumentosResponseDTO> documentosList = repository.findAll().stream().map(DocumentosResponseDTO::new).toList();
        return documentosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Documentos> documentos = repository.findById(id);
        if(documentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        DocumentosResponseDTO documentosDTO = new DocumentosResponseDTO(documentos.get());
        return  ResponseEntity.ok(documentosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody DocumentosRequestDTO data){

        Documentos documentosData = new Documentos(data);
        repository.save(documentosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocumentos(@PathVariable(value = "id") Integer id, @RequestBody DocumentosRequestDTO upData){

        Optional<Documentos> documentos = repository.findById(id);
        if(documentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Documentos documentosModel = documentos.get();
        BeanUtils.copyProperties(upData, documentosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(documentosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocumentos(@PathVariable(value = "id") Integer id){

        Optional<Documentos> documentos = repository.findById(id);
        if(documentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(documentos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Documentos deleted");
    }
}
