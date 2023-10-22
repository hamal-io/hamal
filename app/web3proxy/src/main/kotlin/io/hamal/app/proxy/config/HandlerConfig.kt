package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.EthCache
import io.hamal.app.proxy.handler.EthRequestHandlerImpl
import io.hamal.lib.http.HttpTemplateImpl
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlerConfig {

    @Bean
    fun ethRequestHandler(
        json: Json,
        cache: EthCache,
        upstreamHttpTemplate: HttpTemplateImpl
    ) = EthRequestHandlerImpl(
        json,
        cache,
        upstreamHttpTemplate
    )

}