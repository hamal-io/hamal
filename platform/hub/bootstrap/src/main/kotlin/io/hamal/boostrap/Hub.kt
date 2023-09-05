package io.hamal.boostrap

import io.hamal.admin.AdminConfig
import io.hamal.backend.BackendConfig
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Instance

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .main(Instance::class.java)
        .sources(AdminConfig::class.java, BackendConfig::class.java)
        .web(WebApplicationType.SERVLET)
        .properties("server.port=8008")
        .bannerMode(Banner.Mode.OFF)
        .run(*args)
}
