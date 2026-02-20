package dev.andresbonelli.productcomparisonapi.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_key")
@Data
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String keyValue;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDateTime expiresAt;
    private boolean isActive;

}