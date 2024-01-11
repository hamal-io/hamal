package io.hamal.app.proxy.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.configureMessageConverters(converters)
//        converters.add(KotlinSerializationJsonHttpMessageConverter(json()))
        TODO()
    }
}