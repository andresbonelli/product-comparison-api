package dev.andresbonelli.productcomparisonapi.api.dto;

import java.time.LocalDateTime;

public record ApiKeyDTO (
        String key,
        String role,
        LocalDateTime expiresAt
){
}
