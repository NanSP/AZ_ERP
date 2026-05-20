package com.example.backend.rh.folhaDePagamento;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/folhaDePagamento")
public class FolhaDePagamentoController {

    private final FolhaDePagamentoRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;
    private final FolhaDePagamentoService folhaDePagamentoService;

    public FolhaDePagamentoController(
            FolhaDePagamentoRepository repository,
            ColaboradoresRepository colaboradoresRepository,
            FolhaDePagamentoService folhaDePagamentoService
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
        this.folhaDePagamentoService = folhaDePagamentoService;
    }

    @GetMapping
    public List<FolhaDePagamentoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FolhaDePagamentoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new FolhaDePagamentoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveFolhaDePagamento(@RequestBody FolhaDePagamentoRequestDTO data) {
        try {
            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            FolhaCalculadaDTO calculada = folhaDePagamentoService.calcular(data);

            FolhaDePagamento entity = new FolhaDePagamento();
            entity.setColaborador(colaborador);
            entity.setCompetencia(data.competencia());
            entity.setSalarioBase(data.salarioBase());
            entity.setHorasNormais(data.horasNormais());
            entity.setHorasExtras(data.horasExtras());
            entity.setAdicionais(data.adicionais());
            entity.setDescontos(data.descontos());
            entity.setValorHora(calculada.valorHora());
            entity.setValorHorasNormais(calculada.valorHorasNormais());
            entity.setValorHorasExtras(calculada.valorHorasExtras());
            entity.setValorBruto(calculada.valorBruto());
            entity.setValorLiquido(calculada.valorLiquido());
            entity.setDataPagamento(data.dataPagamento());
            entity.setStatus(data.status());

            FolhaDePagamento saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new FolhaDePagamentoResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFolhaDePagamento(@PathVariable Integer id, @RequestBody FolhaDePagamentoRequestDTO data) {
        try {
            FolhaDePagamento entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Folha de pagamento nao encontrada"));

            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            FolhaCalculadaDTO calculada = folhaDePagamentoService.calcular(data);

            entity.setColaborador(colaborador);
            entity.setCompetencia(data.competencia());
            entity.setSalarioBase(data.salarioBase());
            entity.setHorasNormais(data.horasNormais());
            entity.setHorasExtras(data.horasExtras());
            entity.setAdicionais(data.adicionais());
            entity.setDescontos(data.descontos());
            entity.setValorHora(calculada.valorHora());
            entity.setValorHorasNormais(calculada.valorHorasNormais());
            entity.setValorHorasExtras(calculada.valorHorasExtras());
            entity.setValorBruto(calculada.valorBruto());
            entity.setValorLiquido(calculada.valorLiquido());
            entity.setDataPagamento(data.dataPagamento());
            entity.setStatus(data.status());

            FolhaDePagamento updated = repository.save(entity);
            return ResponseEntity.ok(new FolhaDePagamentoResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolhaDePagamento(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Folha de pagamento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}