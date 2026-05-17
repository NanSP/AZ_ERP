package com.example.backend.sm.ordensServico;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/ordensServico")
public class OrdensServicoController {

    private final OrdensServicoRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ProdutosRepository produtosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public OrdensServicoController(
            OrdensServicoRepository repository,
            ParceirosRepository parceirosRepository,
            ProdutosRepository produtosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.produtosRepository = produtosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<OrdensServicoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OrdensServicoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new OrdensServicoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveOrdensServico(@RequestBody OrdensServicoRequestDTO data) {
        try {
            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            OrdensServico entity = new OrdensServico();
            entity.setNumeroOs(data.numeroOs());
            entity.setCliente(cliente);
            entity.setProduto(produto);
            entity.setTipoServico(data.tipoServico());
            entity.setDescricaoProblema(data.descricaoProblema());
            entity.setPrioridade(data.prioridade());
            entity.setDataAbertura(data.dataAbertura());
            entity.setDataAgendamento(data.dataAgendamento());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setTecnico(tecnico);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            OrdensServico saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrdensServicoResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdensServico(@PathVariable Integer id, @RequestBody OrdensServicoRequestDTO data) {
        try {
            OrdensServico entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ordem de servico nao encontrada"));

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            entity.setNumeroOs(data.numeroOs());
            entity.setCliente(cliente);
            entity.setProduto(produto);
            entity.setTipoServico(data.tipoServico());
            entity.setDescricaoProblema(data.descricaoProblema());
            entity.setPrioridade(data.prioridade());
            entity.setDataAbertura(data.dataAbertura());
            entity.setDataAgendamento(data.dataAgendamento());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setTecnico(tecnico);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            OrdensServico updated = repository.save(entity);
            return ResponseEntity.ok(new OrdensServicoResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrdensServico(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Ordem de servico deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}