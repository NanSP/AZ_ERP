package com.example.backend.bi.dashboards;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/dashboards")
public class DashboardsController {

    private final DashboardsRepository repository;
    private final DashboardsService dashboardsService;

    public DashboardsController(
            DashboardsRepository repository,
            DashboardsService dashboardsService
    ) {
        this.repository = repository;
        this.dashboardsService = dashboardsService;
    }

    @GetMapping
    public List<DashboardsResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DashboardsResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public DashboardsResponseDTO getById(@PathVariable Integer id) {
        Dashboards entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dashboard nao encontrado"));

        return new DashboardsResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DashboardsResponseDTO saveDashboards(@RequestBody DashboardsRequestDTO data) {
        Dashboards saved = dashboardsService.criar(data);
        return new DashboardsResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public DashboardsResponseDTO updateDashboards(@PathVariable Integer id, @RequestBody DashboardsRequestDTO data) {
        Dashboards updated = dashboardsService.atualizar(id, data);
        return new DashboardsResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteDashboards(@PathVariable Integer id) {
        dashboardsService.excluir(id);
        return "Dashboard deleted";
    }
}