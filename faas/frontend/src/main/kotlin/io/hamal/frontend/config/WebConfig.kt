//package io.hamal.frontend.config
//
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.servlet.config.annotation.EnableWebMvc
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//
//
//@Configuration
//@EnableWebMvc
//open class WebConfig : WebMvcConfigurer {
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry
//            .addResourceHandler("/**")
//            .addResourceLocations(
//                "classpath:/META-INF/resources/",
//                "classpath:/resources/",
//                "classpath:/static/",
//                "classpath:/public/"
//            )
//    }
//}