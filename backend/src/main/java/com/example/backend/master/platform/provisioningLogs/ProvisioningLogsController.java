package com.example.backend.master.platform.provisioningLogs;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/provisioningLogs")
public class ProvisioningLogsController {

    private final ProvisioningLogsRepository repository;
    private final ProvisioningLogsService provisioningLogsService;

    public ProvisioningLogsController(
            ProvisioningLogsRepository repository,
            ProvisioningLogsService provisioningLogsService
    ) {
        this.repository = repository;
        this.provisioningLogsService = provisioningLogsService;
    }

    @GetMapping
    public List<ProvisioningLogsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProvisioningLogsResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ProvisioningLogsResponseDTO getById(@PathVariable Long id) {
        ProvisioningLogs entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Provisioning log nao encontrado"));

        return new ProvisioningLogsResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProvisioningLogsResponseDTO saveProvisioningLogs(@RequestBody ProvisioningLogsRequestDTO data) {
        ProvisioningLogs saved = provisioningLogsService.criar(data);
        return new ProvisioningLogsResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ProvisioningLogsResponseDTO updateProvisioningLogs(@PathVariable Long id, @RequestBody ProvisioningLogsRequestDTO data) {
        ProvisioningLogs updated = provisioningLogsService.atualizar(id, data);
        return new ProvisioningLogsResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteProvisioningLogs(@PathVariable Long id) {
        provisioningLogsService.excluir(id);
        return "Provisioning log deleted";
    }

}
