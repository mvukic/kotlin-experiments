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
    fun cacheManager() = CaffeineCacheManager("myCache")
        .apply {
            setCaffeine(caffeineCacheBuilder())
        }

    fun caffeineCacheBuilder(): Caffeine<Any, Any> = Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(500)
        .expireAfterWrite(Duration.ofMinutes(10))

}
