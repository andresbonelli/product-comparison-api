package dev.andresbonelli.productcomparisonapi.service;

import dev.andresbonelli.productcomparisonapi.api.dto.ApiKeyDTO;
import dev.andresbonelli.productcomparisonapi.domain.entity.ApiKey;
import dev.andresbonelli.productcomparisonapi.domain.entity.Role;
import dev.andresbonelli.productcomparisonapi.domain.repository.ApiKeyRepository;
import dev.andresbonelli.productcomparisonapi.utils.ApiKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository repository;

    @Value("${app.api-keys.days-valid-user:30}")
    private int userApiKeyDaysValid;

    public Optional<ApiKey> validate(String keyValue) {
        return repository.findByKeyValue(keyValue)
                .filter(ApiKey::isActive)
                .filter(key -> null == key.getExpiresAt() || key.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    public ApiKeyDTO createUserApiKey() {
        var apiKey = createApiKey(Role.USER, userApiKeyDaysValid);
        return toDTO(apiKey);
    }

    public ApiKeyDTO createRootApiKey() {
        var apiKey = createApiKey(Role.ROOT, null);
        return toDTO(apiKey);
    }

    private ApiKey createApiKey(Role role, Integer daysValid) {
        String newApiKey = ApiKeyUtils.generateNew();

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(newApiKey);
        apiKey.setRole(role);
        apiKey.setExpiresAt(null != daysValid ? LocalDateTime.now().plusDays(daysValid) : null);
        apiKey.setActive(true);

        return repository.save(apiKey);
    }

    private ApiKeyDTO toDTO(ApiKey apiKey) {
        return new ApiKeyDTO(apiKey.getKeyValue(), apiKey.getRole().name(), apiKey.getExpiresAt());
    }
}