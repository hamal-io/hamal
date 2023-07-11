package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.handler.DefaultEthRequestHandler
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlerConfig {

    @Bean
    fun requestHandler(
        json: Json,
        cache: Cache
    ) = DefaultEthRequestHandler(
        json,
        cache
    )

}