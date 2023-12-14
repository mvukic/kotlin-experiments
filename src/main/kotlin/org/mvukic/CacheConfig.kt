package org.mvukic

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration


@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager() = CaffeineCacheManager("api_1", "api_1").apply {
        setAsyncCacheMode(true)
        setCaffeine(caffeineCacheBuilder())
    }

    fun caffeineCacheBuilder(): Caffeine<Any, Any> = Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(500)
        .expireAfterWrite(Duration.ofMinutes(10))
        .removalListener<Any?, Any?> { key, value, cause -> println("Removed $key with value $value because of $cause") }
        .evictionListener<Any?, Any?> { key, value, cause -> println("Evicted $key with value $value because of $cause") }
}
