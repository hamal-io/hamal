package io.hamal.app.proxy.config

import com.google.gson.Gson
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import org.apache.coyote.ProtocolHandler
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import javax.xml.transform.Source

@Configuration
class WebConfig : WebMvcConfigurer {

    @Bean
    fun protocolHandlerVirtualThreadExecutorCustomizer(): TomcatProtocolHandlerCustomizer<*> {
        return TomcatProtocolHandlerCustomizer { protocolHandler: ProtocolHandler ->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }

    @Bean
    fun gson(): Gson = JsonFactoryBuilder()
        .register(HotJsonModule)
        .register(ValueObjectJsonModule)
        .build()

    @Bean
    fun gsonHttpMessageConverter(gson: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gson
        result.defaultCharset = StandardCharsets.UTF_8
        result.supportedMediaTypes = listOf(
            MediaType("application", "json", StandardCharsets.UTF_8)
        )
        return result
    }


    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val stringConverter = StringHttpMessageConverter()
        stringConverter.setWriteAcceptCharset(false)
        stringConverter.supportedMediaTypes = listOf(MediaType.TEXT_PLAIN)
        converters.add(stringConverter)

        converters.add(SourceHttpMessageConverter<Source>())

        converters.add(gsonHttpMessageConverter(gson()))
    }
}