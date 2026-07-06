package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/solicitacoesTitular")
public class SolicitacoesTitularController {

    private final SolicitacoesTitularRepository repository;
    private final SolicitacoesTitularService service;
    private final SolicitacaoTitularEventosService eventosService;

    public SolicitacoesTitularController(
            SolicitacoesTitularRepository repository,
            SolicitacoesTitularService service,
            SolicitacaoTitularEventosService eventosService
    ) {
        this.repository = repository;
        this.service = service;
        this.eventosService = eventosService;
    }

    @GetMapping
    public List<SolicitacoesTitularResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SolicitacoesTitularResponseDTO::new)
                .toList();
    }

    @GetMapping("/summary")
    public SolicitacoesTitularResumoResponseDTO getSummary() {
        return service.gerarResumo();
    }

    @GetMapping("/{id}")
    public SolicitacoesTitularResponseDTO getById(@PathVariable Integer id) {
        SolicitacoesTitular entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao do titular nao encontrada"));
        return new SolicitacoesTitularResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacoesTitularResponseDTO create(@RequestBody SolicitacoesTitularRequestDTO data) {
        return new SolicitacoesTitularResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public SolicitacoesTitularResponseDTO update(@PathVariable Integer id, @RequestBody SolicitacoesTitularRequestDTO data) {
        return new SolicitacoesTitularResponseDTO(service.atualizar(id, data));
    }

    @GetMapping("/{id}/eventos")
    public List<SolicitacaoTitularEventoResponseDTO> getEvents(@PathVariable Integer id) {
        return eventosService.listarPorSolicitacao(id)
                .stream()
                .map(SolicitacaoTitularEventoResponseDTO::new)
                .toList();
    }

    @PostMapping("/{id}/eventos")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoTitularEventoResponseDTO createEvent(
            @PathVariable Integer id,
            @RequestBody SolicitacaoTitularEventoRequestDTO data
    ) {
        return new SolicitacaoTitularEventoResponseDTO(eventosService.registrarEventoManual(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Solicitacao do titular deleted";
    }
}
