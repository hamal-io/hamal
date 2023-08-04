package io.hamal.app.proxy.config

import io.hamal.lib.http.HttpTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.math.min

@Configuration
class UpstreamConfig {

    @Bean
    fun upstreamTemplate(
        @Value("\${io.hamal.upstream.host}") upstreamHost: String
    ): HttpTemplate {
        logger.info("${upstreamHost.substring(0, min(upstreamHost.length, 25))}...")
        return HttpTemplate(
            baseUrl = upstreamHost
        )
    }

    private val logger = LoggerFactory.getLogger(UpstreamConfig::class.java)
}