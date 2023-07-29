package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.cache.LruCache
import io.hamal.app.proxy.repository.AddressRepository
import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.CallRepository
import io.hamal.app.proxy.repository.ProxyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun cache(
        proxyRepository: ProxyRepository,
        addressRepository: AddressRepository,
        blockRepository: BlockRepository,
        callRepository: CallRepository
    ): Cache = LruCache(
        proxyRepository,
        addressRepository,
        blockRepository,
        callRepository
    )
}

