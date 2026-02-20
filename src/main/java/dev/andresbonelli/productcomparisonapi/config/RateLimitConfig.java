package dev.andresbonelli.productcomparisonapi.config;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "bucket4j.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitConfig {

    @Bean
    public CacheManager jCacheManager() {
        CachingProvider provider = Caching.getCachingProvider(CaffeineCachingProvider.class.getName());
        CacheManager cacheManager = provider.getCacheManager();

        // Explicitly create the cache if it doesn't exist
        if (null == cacheManager.getCache("rate-limit-buckets")) {
            cacheManager.createCache("rate-limit-buckets", new javax.cache.configuration.MutableConfiguration<>());
        }

        return cacheManager;
    }
}
