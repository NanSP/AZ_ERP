package com.example.backend.master.platform.templateMigration;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "template_registry", schema = "platform")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TemplateRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "database_name", nullable = false, unique = true)
    private String databaseName;

    @Column(name = "current_version")
    private String currentVersion;

    @Column(nullable = false)
    private String status;

    @Column(name = "lock_active", nullable = false)
    private boolean lockActive;

    @Column(name = "last_migrated_at")
    private LocalDateTime lastMigratedAt;

    @Column(name = "last_validated_at")
    private LocalDateTime lastValidatedAt;

    @Column(name = "last_cloned_at")
    private LocalDateTime lastClonedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
