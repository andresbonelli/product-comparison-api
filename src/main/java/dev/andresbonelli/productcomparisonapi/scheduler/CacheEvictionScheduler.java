package dev.andresbonelli.productcomparisonapi.scheduler;

import dev.andresbonelli.productcomparisonapi.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("prod")
public class CacheEvictionScheduler {


    @Scheduled(fixedRateString = "${app.cache.ttl:10000}")
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public void clearProductsCache() {
        log.info("{} cache cleared. Interval: {}ms", CacheConfig.PRODUCTS_CACHE, "${app.cache.ttl:10000}");
    }
}
