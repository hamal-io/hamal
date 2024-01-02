package io.hamal.lib.http.fixture

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@ComponentScan
@EnableAutoConfiguration
open class TestWebConfig : WebMvcConfigurer {
    @Bean
    open fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        TODO()
//        return KotlinSerializationJsonHttpMessageConverter(Json {
//            explicitNulls = false
//            ignoreUnknownKeys = true
//            encodeDefaults = true
//        })
    }
}