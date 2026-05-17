package com.example.backend.sd.oportunidades;

import com.example.backend.sd.clientes.Clientes;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/oportunidades")
public class OportunidadesController {

    private final OportunidadesRepository repository;
    private final ClientesRepository clientesRepository;
    private final UsuariosRepository usuariosRepository;

    public OportunidadesController(
            OportunidadesRepository repository,
            ClientesRepository clientesRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.clientesRepository = clientesRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<OportunidadesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OportunidadesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new OportunidadesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveOportunidades(@RequestBody OportunidadesRequestDTO data) {
        try {
            Clientes cliente = data.cliente() != null
                    ? clientesRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            Oportunidades entity = new Oportunidades();
            entity.setCliente(cliente);
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setValorEstimado(data.valorEstimado());
            entity.setProbabilidade(data.probabilidade());
            entity.setEstagio(data.estagio());
            entity.setDataPrevistaFechamento(data.dataPrevistaFechamento());
            entity.setMotivoPerda(data.motivoPerda());
            entity.setResponsavel(responsavel);
            entity.setCreatedAt(data.createdAt());

            Oportunidades saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new OportunidadesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOportunidades(@PathVariable Integer id, @RequestBody OportunidadesRequestDTO data) {
        try {
            Oportunidades entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Oportunidade nao encontrada"));

            Clientes cliente = data.cliente() != null
                    ? clientesRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setCliente(cliente);
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setValorEstimado(data.valorEstimado());
            entity.setProbabilidade(data.probabilidade());
            entity.setEstagio(data.estagio());
            entity.setDataPrevistaFechamento(data.dataPrevistaFechamento());
            entity.setMotivoPerda(data.motivoPerda());
            entity.setResponsavel(responsavel);
            entity.setCreatedAt(data.createdAt());

            Oportunidades updated = repository.save(entity);
            return ResponseEntity.ok(new OportunidadesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOportunidades(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Oportunidade deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}