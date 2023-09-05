package io.hamal.mono

import io.hamal.admin.AdminConfig
import io.hamal.api.ApiConfig
import io.hamal.runner.RunnerConfig
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Mono

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .parent(Mono::class.java)
        .sources(AdminConfig::class.java, ApiConfig::class.java, RunnerConfig::class.java)
        .web(WebApplicationType.SERVLET)
        .properties("server.port=8008")
        .bannerMode(Banner.Mode.OFF)
        .run(*args)
}
