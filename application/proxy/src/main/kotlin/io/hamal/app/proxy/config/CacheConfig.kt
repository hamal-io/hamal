package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.cache.LruCache
import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.ProxyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun cache(
        proxyRepository: ProxyRepository,
        blockRepository: BlockRepository
    ): Cache = LruCache(
        proxyRepository,
        blockRepository
    )
}

