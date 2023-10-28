package io.hamal.core.config

import io.hamal.core.component.*
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
open class WebConfig : WebMvcConfigurer {

    @Bean
    @OptIn(ExperimentalSerializationApi::class)
    open fun json(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Bean
    @OptIn(ExperimentalSerializationApi::class)
    open fun protobuf(): ProtoBuf = ProtoBuf {
        encodeDefaults = true
    }


    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.configureMessageConverters(converters)
        converters.add(KotlinSerializationJsonHttpMessageConverter(json()))
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
        registry.addConverter(NamespaceIdConverter)
        registry.addConverter(LimitConverter)
        registry.addConverter(ReqIdConverter)
        registry.addConverter(TopicEntryIdConverter)
        registry.addConverter(TopicIdConverter)
        registry.addConverter(TopicNameConverter)
        registry.addConverter(TriggerIdConverter)
    }

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.setUseTrailingSlashMatch(true)
    }
}

@Serializable
data class Test<ID : SerializableDomainId>(
    val reqId: ReqId,
    val status: ReqStatus,
    val id: ID,
    val namespaceId: NamespaceId? = null,
    val groupId: GroupId? = null,
)


fun main() {
    val j = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    println(
        j.encodeToString(
            Test<ExecId>(
                reqId = ReqId(1),
                status = ReqStatus.Failed,
                id = ExecId(12),
                namespaceId = NamespaceId(23),
                groupId = GroupId(23)
            )
        )
    )
}
