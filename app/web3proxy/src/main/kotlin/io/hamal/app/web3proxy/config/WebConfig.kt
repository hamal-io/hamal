package io.hamal.app.web3proxy.config

import com.google.gson.Gson
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.web3.evm.EvmHotModule
import org.apache.coyote.ProtocolHandler
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
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
        .register(EvmHotModule)
        .register(SerdeModule)
        .register(ValueVariableJsonModule)
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
        converters.add(SourceHttpMessageConverter<Source>())
        converters.add(gsonHttpMessageConverter(gson()))
    }
}