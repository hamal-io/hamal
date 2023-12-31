package io.hamal.core.config

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.core.component.*
import io.hamal.lib.common.serialization.GsonFactory
import io.hamal.lib.common.serialization.GsonSerde
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
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
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.util.*
import javax.xml.transform.Source


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

//    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
//        converters.add(KotlinSerializationJsonHttpMessageConverter(json()))
//        super.configureMessageConverters(converters)
//    }

    @Bean
    open fun gson(): Gson = GsonFactory
        .registerTypeAdapter(Email::class.java, object : GsonSerde<Email> {
            override fun serialize(value: Email, type: Type, context: JsonSerializationContext): JsonElement {
                TODO("Not yet implemented")
            }

            override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Email {
//                TODO("Not yet implemented")
                return Email(element.asString)
            }

        })
        .registerTypeAdapter(Password::class.java, object : GsonSerde<Password> {
            override fun serialize(value: Password, type: Type, context: JsonSerializationContext): JsonElement {
                TODO("Not yet implemented")
            }

            override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): Password {
//                TODO("Not yet implemented")
                return Password(element.asString)
            }
        })
        .create()

    @Bean
    open fun gsonHttpMessageConverter(gson: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gson
        result.supportedMediaTypes = Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType("application", "json", StandardCharsets.UTF_8)
        )
        return result
    }


    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val stringConverter = StringHttpMessageConverter()
        stringConverter.setWriteAcceptCharset(false)
        stringConverter.supportedMediaTypes = listOf(MediaType.TEXT_PLAIN)
        converters.add(stringConverter)

        converters.add(ByteArrayHttpMessageConverter())
        converters.add(SourceHttpMessageConverter<Source>())

        converters.add(gsonHttpMessageConverter(gson()))
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
