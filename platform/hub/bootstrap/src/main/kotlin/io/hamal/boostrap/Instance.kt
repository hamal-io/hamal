package io.hamal.boostrap

import io.hamal.backend.instance.BackendConfig
import guru.fn.FrontendConfig
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Instance

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .main(Instance::class.java)
        .sources(BackendConfig::class.java, FrontendConfig::class.java)
        .web(WebApplicationType.SERVLET)
        .properties("server.port=8008")
        .bannerMode(Banner.Mode.OFF)
        .run(*args)
}
