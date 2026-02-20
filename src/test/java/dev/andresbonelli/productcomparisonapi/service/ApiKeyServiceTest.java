package dev.andresbonelli.productcomparisonapi.service;

import dev.andresbonelli.productcomparisonapi.api.dto.ApiKeyDTO;
import dev.andresbonelli.productcomparisonapi.domain.entity.ApiKey;
import dev.andresbonelli.productcomparisonapi.domain.entity.Role;
import dev.andresbonelli.productcomparisonapi.domain.repository.ApiKeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository repository;

    @InjectMocks
    private ApiKeyService service;

    @Test
    void createUserApiKey_OK()  {
        // Arrange
        doAnswer(a -> {
            ApiKey apiKey = a.getArgument(0);
            apiKey.setId(1L);
            return apiKey;
        }).when(repository).save(any(ApiKey.class));
        // Act
        ApiKeyDTO apiKeyDTO = service.createUserApiKey();
        // Assert
        assertNotNull(apiKeyDTO);
        assertNotNull(apiKeyDTO.key());
        assertEquals(Role.USER.name(), apiKeyDTO.role());
        assertNotNull(apiKeyDTO.expiresAt());
    }


}