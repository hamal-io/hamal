package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.handler.DefaultEthRequestHandler
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlerConfig {

    @Bean
    fun requestHandler(
        json: Json,
        cache: Cache,
        upstreamHttpTemplate: HttpTemplate
    ) = DefaultEthRequestHandler(
        json,
        cache,
        upstreamHttpTemplate
    )

}