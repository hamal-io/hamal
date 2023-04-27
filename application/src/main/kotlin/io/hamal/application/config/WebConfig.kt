//package io.hamal.application.config
//
//import kotlinx.serialization.json.Json
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
//
//@Configuration
//class WebConfig {
//    @Bean
//    fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
//        return KotlinSerializationJsonHttpMessageConverter(Json {
//            ignoreUnknownKeys = false
//        })
//    }
//}