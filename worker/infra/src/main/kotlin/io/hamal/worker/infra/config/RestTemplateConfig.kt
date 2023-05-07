package io.hamal.worker.infra.config

import kotlinx.serialization.json.Json
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.client.RestTemplate


@Configuration
open class RestTemplateConfig {
    @Bean
    open fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        return KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = false
        })
    }

    @Bean
    open fun restTemplate(): RestTemplate {
//        val restTemplate = RestTemplate()
//        val messageConverters: MutableList<HttpMessageConverter<*>> = ArrayList()
//        val converter = messageConverter()
//        converter.supportedMediaTypes = listOf<MediaType>(MediaType.ALL)
//        messageConverters.add(converter)
//        restTemplate.messageConverters = messageConverters
        return RestTemplateBuilder()
            .messageConverters(KotlinSerializationJsonHttpMessageConverter())
            .build()
    }
}