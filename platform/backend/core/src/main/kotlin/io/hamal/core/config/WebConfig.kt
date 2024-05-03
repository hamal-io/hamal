package io.hamal.core.config

import com.google.gson.Gson
import io.hamal.core.component.*
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.domain.vo.SerdeModuleValueVariableJson
import io.hamal.lib.sdk.api.SerdeModuleJsonApi
import io.hamal.repository.api.event.SerdeModuleJsonInternalEvent
import org.apache.coyote.ProtocolHandler
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import javax.xml.transform.Source


@Configuration
open class WebConfig : WebMvcConfigurer {

    @Bean
    open fun protocolHandlerVirtualThreadExecutorCustomizer(): TomcatProtocolHandlerCustomizer<*> {
        return TomcatProtocolHandlerCustomizer { protocolHandler: ProtocolHandler ->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }

    @Bean
    open fun gsonJson(): Gson = Serde.json()
        .register(SerdeModuleJsonApi)
        .register(SerdeModuleJsonInternalEvent)
        .register(SerdeModuleValueJson)
        .register(SerdeModuleValueVariableJson)
        .gson


    @Bean
    open fun httpMessageJsonConverter(gson: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gson
        result.defaultCharset = StandardCharsets.UTF_8
        result.supportedMediaTypes = listOf(MediaType("application", "json", StandardCharsets.UTF_8))
        return result
    }


    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val stringConverter = StringHttpMessageConverter()
        stringConverter.setWriteAcceptCharset(false)
        stringConverter.supportedMediaTypes = listOf(MediaType.TEXT_PLAIN)
        converters.add(stringConverter)

        converters.add(ByteArrayHttpMessageConverter())
        converters.add(SourceHttpMessageConverter<Source>())
        converters.add(httpMessageJsonConverter(gsonJson()))
    }


    override fun addFormatters(registry: FormatterRegistry) {
        super.addFormatters(registry)
        registry.addConverter(AccountIdConverter)
        registry.addConverter(CodeIdConverter)
        registry.addConverter(CodeVersionConverter)
        registry.addConverter(CorrelationIdConverter)
        registry.addConverter(ExecIdConverter)
        registry.addConverter(ExecLogIdConverter)
        registry.addConverter(ExtensionIdConverter)
        registry.addConverter(FeedbackIdConverter)
        registry.addConverter(FuncIdConverter)
        registry.addConverter(LimitConverter)
        registry.addConverter(NamespaceIdConverter)
        registry.addConverter(RequestIdConverter)
        registry.addConverter(RecipeIdConverter)
        registry.addConverter(TopicEntryIdConverter)
        registry.addConverter(TopicIdConverter)
        registry.addConverter(TopicNameConverter)
        registry.addConverter(TriggerIdConverter)
        registry.addConverter(WorkspaceIdConverter)

    }
}
