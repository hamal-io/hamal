package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.EthCache
import io.hamal.app.proxy.cache.HmlCache
import io.hamal.app.proxy.handler.EthRequestHandlerImpl
import io.hamal.app.proxy.handler.HmlRequestHandlerImpl
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlerConfig {

    @Bean
    fun ethRequestHandler(
        json: Json,
        cache: EthCache,
        upstreamHttpTemplate: HttpTemplate
    ) = EthRequestHandlerImpl(
        json,
        cache,
        upstreamHttpTemplate
    )

    @Bean
    fun hmlRequestHandler(
        json: Json,
        cache: HmlCache,
    ) = HmlRequestHandlerImpl(
        json,
        cache
    )
}