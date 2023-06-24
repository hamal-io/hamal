package io.hamal.backend.instance.config

import io.hamal.backend.instance.config.converter.TopicIdConverter
import io.hamal.lib.domain.HamalError
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.format.FormatterRegistry
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfig : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.configureMessageConverters(converters)
        converters.add(KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = false
            encodeDefaults = true
        }))
    }

    override fun addFormatters(registry: FormatterRegistry) {
        super.addFormatters(registry)

//        registry.addConverter(ContentConverter())
        registry.addConverter(TopicIdConverter())
    }


    @ControllerAdvice
    @Order(Ordered.HIGHEST_PRECEDENCE)
    class ErrorHandler {


        @ExceptionHandler(Exception::class)
        fun handle(e: Exception): ResponseEntity<HamalError> {
            return when (e) {
                is IllegalArgumentException -> ResponseEntity(
                    HamalError(
                        message = e.message
                    ), HttpStatus.BAD_REQUEST
                )

                is NoSuchElementException -> ResponseEntity(
                    HamalError(
                        message = e.message
                    ), HttpStatus.NOT_FOUND
                )

                is IllegalStateException -> ResponseEntity(
                    HamalError(
                        message = e.message
                    ), HttpStatus.INTERNAL_SERVER_ERROR
                )

                is MethodArgumentTypeMismatchException -> ResponseEntity(
                    HamalError(
                        message = e.cause?.message
                    ), HttpStatus.BAD_REQUEST
                )

                else -> ResponseEntity(HamalError(e.message), HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}