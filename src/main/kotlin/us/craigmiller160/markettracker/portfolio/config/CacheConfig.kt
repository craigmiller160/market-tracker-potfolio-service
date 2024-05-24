package io.craigmiller160.markettracker.portfolio.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean

@EnableCaching
class CacheConfig {
  @Bean fun cacheManager(): CacheManager = ConcurrentMapCacheManager()
}
