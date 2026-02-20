package dev.andresbonelli.productcomparisonapi.runner;

import dev.andresbonelli.productcomparisonapi.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class GenerateRootUserRunner implements CommandLineRunner {

    private final ApiKeyService apiKeyService;

    @Override
    public void run(String... args) {
        var rootApiKey = apiKeyService.createRootApiKey();
        log.info("Root API key generated: {}", rootApiKey.key());
    }
}
