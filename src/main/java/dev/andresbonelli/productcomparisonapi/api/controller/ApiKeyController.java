package dev.andresbonelli.productcomparisonapi.api.controller;

import dev.andresbonelli.productcomparisonapi.api.dto.ApiKeyDTO;
import dev.andresbonelli.productcomparisonapi.service.ApiKeyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
@Tag(name = "API Keys", description = "API Key management")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/generate")
    public ResponseEntity<ApiKeyDTO> generateUser() {
        var result = apiKeyService.createUserApiKey();
        return ResponseEntity.ok(result);
    }
}
