package io.hamal.backend.instance.config

import io.hamal.lib.sdk.domain.ApiError
import kotlinx.serialization.ExperimentalSerializationApi
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
    @OptIn(ExperimentalSerializationApi::class)
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.configureMessageConverters(converters)
        converters.add(KotlinSerializationJsonHttpMessageConverter(Json {
            explicitNulls = false
            ignoreUnknownKeys = true
            encodeDefaults = true
        }))
    }

    override fun addFormatters(registry: FormatterRegistry) {
        super.addFormatters(registry)
        registry.addConverter(CorrelationIdConverter)
        registry.addConverter(EventIdConverter)
        registry.addConverter(ExecIdConverter)
        registry.addConverter(ExecLogIdConverter)
        registry.addConverter(FuncIdConverter)
        registry.addConverter(NamespaceIdConverter)
        registry.addConverter(LimitConverter)
        registry.addConverter(ReqIdConverter)
        registry.addConverter(TopicIdConverter)
        registry.addConverter(TriggerIdConverter)
    }


    @ControllerAdvice
    @Order(Ordered.HIGHEST_PRECEDENCE)
    class ErrorHandler {

        @ExceptionHandler(Exception::class)
        fun handle(e: Exception): ResponseEntity<ApiError> {
            return when (e) {
                is IllegalArgumentException -> ResponseEntity(
                    ApiError(
                        message = e.message
                    ), HttpStatus.BAD_REQUEST
                )

                is NoSuchElementException -> ResponseEntity(
                    ApiError(
                        message = e.message
                    ), HttpStatus.NOT_FOUND
                )

                is IllegalStateException -> ResponseEntity(
                    ApiError(
                        message = e.message
                    ), HttpStatus.INTERNAL_SERVER_ERROR
                )

                is MethodArgumentTypeMismatchException -> ResponseEntity(
                    ApiError(
                        message = e.cause?.message
                    ), HttpStatus.BAD_REQUEST
                )

                else -> ResponseEntity(ApiError(e.message), HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}