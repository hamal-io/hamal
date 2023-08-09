package io.hamal.bootstrap

import io.hamal.backend.instance.BackendConfig
import io.hamal.frontend.FrontendConfig
import io.hamal.runner.RunnerConfig
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
class Hamal

fun main(args: Array<String>) {
    val applicationBuilder = SpringApplicationBuilder()
        .parent(Hamal::class.java)
        .banner { _: Environment, _: Class<*>, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(
                """
  _    _                       _ 
 | |  | |                     | |
 | |__| | __ _ _ __ ___   __ _| |
 |  __  |/ _` | '_ ` _ \ / _` | |
 | |  | | (_| | | | | | | (_| | |
 |_|  |_|\__,_|_| |_| |_|\__,_|_|
            """.trimIndent()
            )
            out.println("")
            out.println("")
        }
        .web(WebApplicationType.NONE)

    val parent: ConfigurableApplicationContext = applicationBuilder.run(*args)
    val ctx: ConfigurableApplicationContext = applicationBuilder.context()


    applicationBuilder
        .parent(parent)
        .child(BackendConfig::class.java, FrontendConfig::class.java)
        .web(WebApplicationType.SERVLET)
        .properties("server.port=8008")
        .run(*args)


    applicationBuilder
        .parent(parent)
        .child(RunnerConfig::class.java)
        .web(WebApplicationType.NONE)
        .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(
                """
  _____                             
 |  __ \                            
 | |__) |   _ _ __  _ __   ___ _ __ 
 |  _  / | | | '_ \| '_ \ / _ \ '__|
 | | \ \ |_| | | | | | | |  __/ |   
 |_|  \_\__,_|_| |_|_| |_|\___|_| 
                """.trimIndent()
            )
            out.println("")
            out.println("")
        }
        .run(*args)

}

fun isEnable(ctx: ConfigurableApplicationContext, module: String): Boolean {
//    return ctx.environment.getProperty("io.hamal.module.$module.enabled", "false") == "true"
    return true
}