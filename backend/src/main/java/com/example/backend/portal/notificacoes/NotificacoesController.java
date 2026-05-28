package com.example.backend.portal.notificacoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/notificacoes")
public class NotificacoesController {

    private final NotificacoesRepository repository;
    private final NotificacoesService notificacoesService;

    public NotificacoesController(
            NotificacoesRepository repository,
            NotificacoesService notificacoesService
    ) {
        this.repository = repository;
        this.notificacoesService = notificacoesService;
    }

    @GetMapping
    public List<NotificacoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(NotificacoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public NotificacoesResponseDTO getById(@PathVariable Integer id) {
        Notificacoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificacao nao encontrada"));

        return new NotificacoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificacoesResponseDTO saveNotificacoes(@RequestBody NotificacoesRequestDTO data) {
        Notificacoes saved = notificacoesService.criar(data);
        return new NotificacoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public NotificacoesResponseDTO updateNotificacoes(@PathVariable Integer id, @RequestBody NotificacoesRequestDTO data) {
        Notificacoes updated = notificacoesService.atualizar(id, data);
        return new NotificacoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteNotificacoes(@PathVariable Integer id) {
        notificacoesService.excluir(id);
        return "Notificacao deleted";
    }
}