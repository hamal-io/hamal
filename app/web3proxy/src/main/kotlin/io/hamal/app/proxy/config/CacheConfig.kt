package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.EthCache
import io.hamal.app.proxy.cache.EthLruCache
import io.hamal.app.proxy.cache.HmlCache
import io.hamal.app.proxy.cache.HmlLruCache
import io.hamal.app.proxy.repository.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun ethCache(
        proxyRepository: ProxyRepository,
        addressRepository: AddressRepository,
        blockRepository: BlockRepository,
        callRepository: CallRepository,
        transactionRepository: TransactionRepository
    ): EthCache = EthLruCache(
        proxyRepository,
        addressRepository,
        blockRepository,
        callRepository,
        transactionRepository
    )


    @Bean
    fun hmlCache(
        proxyRepository: ProxyRepository,
        addressRepository: AddressRepository,
        blockRepository: BlockRepository,
        callRepository: CallRepository,
        transactionRepository: TransactionRepository
    ): HmlCache = HmlLruCache(
        proxyRepository,
        addressRepository,
        blockRepository,
        callRepository,
        transactionRepository
    )
}

