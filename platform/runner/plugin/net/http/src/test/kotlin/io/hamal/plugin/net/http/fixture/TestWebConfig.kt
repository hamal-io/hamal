package io.hamal.plugin.net.http.fixture

import com.google.gson.Gson
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.common.value.serde.SerdeModuleValueHon
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.StandardCharsets
import javax.xml.transform.Source

@Configuration
@EnableWebMvc
@ComponentScan
@EnableAutoConfiguration
open class TestWebConfig : WebMvcConfigurer {

    @Bean
    open fun gsonJson(): Gson = Serde.json().register(SerdeModuleValueJson).gson

    @Bean
    open fun httpMessageJsonConverter(gsonJson: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gsonJson
        result.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON, MediaType("application", "json", StandardCharsets.UTF_8))
        return result
    }


    @Bean
    open fun gsonHon(): Gson = Serde.hon().register(SerdeModuleValueHon).gson

    @Bean
    open fun httpMessageHonConverter(gsonHon: Gson): GsonHttpMessageConverter {
        val result = GsonHttpMessageConverter()
        result.gson = gsonHon
        result.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON, MediaType("application", "hon", StandardCharsets.UTF_8))
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
        converters.add(httpMessageHonConverter(gsonHon()))
    }
}