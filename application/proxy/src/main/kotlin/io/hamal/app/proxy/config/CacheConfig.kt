package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.cache.LruCache
import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.ReceiptRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun cache(
        blockRepository: BlockRepository,
        receiptRepository: ReceiptRepository
    ): Cache = LruCache(
        blockRepository,
        receiptRepository
    )
}

