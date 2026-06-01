package com.example.backend.master.platform.templateMigration;

import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TemplateRegistryService {

    private final TemplateRegistryRepository repository;
    private final TemplateMigrationProperties properties;

    public TemplateRegistryService(
            TemplateRegistryRepository repository,
            TemplateMigrationProperties properties
    ) {
        this.repository = repository;
        this.properties = properties;
    }

    @Transactional
    public TemplateRegistry ensureRegistry() {
        return repository.findByDatabaseName(properties.getDatabase())
                .orElseGet(() -> repository.save(criarNovoRegistry()));
    }

    @Transactional
    public TemplateRegistry acquireLock(String status) {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseGet(() -> repository.save(criarNovoRegistry()));

        if (registry.isLockActive()) {
            throw new ValidacaoException("Template em uso por outra operacao");
        }

        registry.setLockActive(true);
        registry.setStatus(status);
        return repository.save(registry);
    }

    @Transactional
    public TemplateRegistry markReady(String currentVersion) {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        registry.setCurrentVersion(currentVersion);
        registry.setStatus("READY");
        registry.setLockActive(false);
        registry.setLastMigratedAt(LocalDateTime.now());
        return repository.save(registry);
    }

    @Transactional
    public TemplateRegistry markValidated() {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        registry.setStatus("READY");
        registry.setLockActive(false);
        registry.setLastValidatedAt(LocalDateTime.now());
        return repository.save(registry);
    }

    @Transactional
    public TemplateRegistry markCloneCompleted() {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        registry.setStatus("READY");
        registry.setLockActive(false);
        registry.setLastClonedAt(LocalDateTime.now());
        return repository.save(registry);
    }

    @Transactional
    public TemplateRegistry releaseLockToReady() {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        registry.setStatus("READY");
        registry.setLockActive(false);
        return repository.save(registry);
    }

    @Transactional
    public TemplateRegistry markError(String status) {
        TemplateRegistry registry = repository.findByDatabaseNameForUpdate(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        registry.setStatus(status);
        registry.setLockActive(false);
        return repository.save(registry);
    }

    public TemplateRegistry getReadyRegistry() {
        TemplateRegistry registry = repository.findByDatabaseName(properties.getDatabase())
                .orElseThrow(() -> new ValidacaoException("Registry do template nao encontrado"));

        if (!"READY".equalsIgnoreCase(registry.getStatus()) || registry.getCurrentVersion() == null) {
            throw new ValidacaoException("Template ainda nao esta pronto para provisionamento");
        }

        return registry;
    }

    private TemplateRegistry criarNovoRegistry() {
        TemplateRegistry registry = new TemplateRegistry();
        registry.setDatabaseName(properties.getDatabase());
        registry.setStatus("PENDING");
        registry.setLockActive(false);
        return registry;
    }
}
