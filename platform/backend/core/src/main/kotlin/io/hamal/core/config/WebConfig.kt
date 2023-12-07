package io.hamal.core.config

import io.hamal.core.component.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@OptIn(ExperimentalSerializationApi::class)
open class WebConfig : WebMvcConfigurer {

    @Bean
    open fun json(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Bean
    open fun protobuf(): ProtoBuf = ProtoBuf { }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(KotlinSerializationJsonHttpMessageConverter(json()))
        super.configureMessageConverters(converters)
    }

    override fun addFormatters(registry: FormatterRegistry) {
        super.addFormatters(registry)
        registry.addConverter(AccountIdConverter)
        registry.addConverter(CodeVersionConverter)
        registry.addConverter(CorrelationIdConverter)
        registry.addConverter(ExecIdConverter)
        registry.addConverter(ExecLogIdConverter)
        registry.addConverter(FuncIdConverter)
        registry.addConverter(GroupIdConverter)
        registry.addConverter(FlowIdConverter)
        registry.addConverter(LimitConverter)
        registry.addConverter(ReqIdConverter)
        registry.addConverter(TopicEntryIdConverter)
        registry.addConverter(TopicIdConverter)
        registry.addConverter(TopicNameConverter)
        registry.addConverter(TriggerIdConverter)
    }
}
