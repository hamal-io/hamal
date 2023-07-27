package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.cache.LruCache
import io.hamal.app.proxy.repository.DepBlockRepository
import io.hamal.app.proxy.repository.ReceiptRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun cache(
        blockRepository: DepBlockRepository,
        receiptRepository: ReceiptRepository
    ): Cache = LruCache(
        blockRepository,
        receiptRepository
    )
}

