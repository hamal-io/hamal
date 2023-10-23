package io.hamal.app.proxy.config

import io.hamal.lib.http.HttpTemplateImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.math.min

@Configuration
class UpstreamConfig {

    @Bean
    fun upstreamTemplate(
        @Value("\${io.hamal.proxy.upstream.host}") upstreamHost: String
    ): HttpTemplateImpl {
        log.info("${upstreamHost.substring(0, min(upstreamHost.length, 25))}...")
        return HttpTemplateImpl(
            baseUrl = upstreamHost
        )
    }

    private val log = LoggerFactory.getLogger(UpstreamConfig::class.java)
}