package io.hamal.mono

import io.hamal.api.ApiConfig
import io.hamal.bridge.BridgeConfig
import io.hamal.core.CoreConfig
import io.hamal.runner.RunnerConfig
import org.springframework.boot.Banner.Mode.OFF
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
class Mono

fun main(args: Array<String>) {

    val applicationBuilder = SpringApplicationBuilder()
        .parent(CoreConfig::class.java)
        .banner { _: Environment, _: Class<*>, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(
                """
 _    _                       _   _       
 | |  | |                     | | (_)      
 | |__| | __ _ _ __ ___   __ _| |  _  ___  
 |  __  |/ _` | '_ ` _ \ / _` | | | |/ _ \ 
 | |  | | (_| | | | | | | (_| | |_| | (_) |
 |_|  |_|\__,_|_| |_| |_|\__,_|_(_)_|\___/ 
            """.trimIndent()
            )
            out.println("")
            out.println("")
        }
        .web(NONE)

    val parent: ConfigurableApplicationContext = applicationBuilder.run(*args)

    applicationBuilder
        .parent(parent)
        .child(ApiConfig::class.java)
        .web(SERVLET)
        .properties("server.port=8008")
        .bannerMode(OFF)
        .run(*args)

    applicationBuilder
        .parent(parent)
        .child(BridgeConfig::class.java)
        .web(SERVLET)
        .properties("server.port=7007")
        .bannerMode(OFF)
        .run(*args)

    applicationBuilder
        .parent(parent)
        .child(RunnerConfig::class.java)
        .web(NONE)
        .bannerMode(OFF)
        .run(*args)
}
