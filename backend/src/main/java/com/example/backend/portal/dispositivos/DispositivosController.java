package com.example.backend.portal.dispositivos;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/dispositivos")
public class DispositivosController {

    private final DispositivosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public DispositivosController(
            DispositivosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<DispositivosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DispositivosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new DispositivosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveDispositivos(@RequestBody DispositivosRequestDTO data) {
        try {
            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            Dispositivos entity = new Dispositivos();
            entity.setUsuario(usuario);
            entity.setDeviceId(data.deviceId());
            entity.setDeviceModel(data.deviceModel());
            entity.setDevicePlatform(data.devicePlatform());
            entity.setPushToken(data.pushToken());
            entity.setUltimoAcesso(data.ultimoAcesso());
            entity.setAtivo(data.ativo());
            entity.setCreatedAt(data.createdAt());

            Dispositivos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DispositivosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDispositivos(@PathVariable Integer id, @RequestBody DispositivosRequestDTO data) {
        try {
            Dispositivos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Dispositivo nao encontrado"));

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setUsuario(usuario);
            entity.setDeviceId(data.deviceId());
            entity.setDeviceModel(data.deviceModel());
            entity.setDevicePlatform(data.devicePlatform());
            entity.setPushToken(data.pushToken());
            entity.setUltimoAcesso(data.ultimoAcesso());
            entity.setAtivo(data.ativo());
            entity.setCreatedAt(data.createdAt());

            Dispositivos updated = repository.save(entity);
            return ResponseEntity.ok(new DispositivosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Dispositivo deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}