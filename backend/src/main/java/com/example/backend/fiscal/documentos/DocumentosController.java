package com.example.backend.fiscal.documentos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/documentos")
public class DocumentosController {

    private final DocumentosRepository repository;
    private final DocumentosService documentosService;

    public DocumentosController(
            DocumentosRepository repository,
            DocumentosService documentosService
    ) {
        this.repository = repository;
        this.documentosService = documentosService;
    }

    @GetMapping
    public List<DocumentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DocumentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public DocumentosResponseDTO getById(@PathVariable Integer id) {
        Documentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento nao encontrado"));

        return new DocumentosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentosResponseDTO saveDocumentos(@RequestBody DocumentosRequestDTO data) {
        Documentos saved = documentosService.criar(data);
        return new DocumentosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public DocumentosResponseDTO updateDocumentos(@PathVariable Integer id, @RequestBody DocumentosRequestDTO data) {
        Documentos updated = documentosService.atualizar(id, data);
        return new DocumentosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteDocumentos(@PathVariable Integer id) {
        documentosService.excluir(id);
        return "Documento deleted";
    }
}