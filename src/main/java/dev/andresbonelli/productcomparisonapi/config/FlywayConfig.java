package dev.andresbonelli.productcomparisonapi.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayConfig {

    @Bean
    @Profile("dev")
    public FlywayMigrationStrategy cleanMigrationStrategy() {
        return flyway -> {
            // Wipes the database structure and the flyway_schema_history table
            flyway.clean();
            // Re-runs all migrations (V1, V2, etc.)
            flyway.migrate();
        };
    }
}
