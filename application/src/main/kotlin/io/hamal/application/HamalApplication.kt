package io.hamal.application

import io.hamal.application.config.DomainNotificationConfig
import io.hamal.application.config.LoggerConfig
import io.hamal.application.config.UseCaseConfig
import io.hamal.module.launchpad.infra.LaunchpadModuleConfig
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
class HamalApplication

fun main(args: Array<String>) {
    val applicationBuilder = SpringApplicationBuilder()
        .main(HamalApplication::class.java)
        .parent(
//            DatabaseConfig::class.java,
            DomainNotificationConfig::class.java,
            LoggerConfig::class.java,
            UseCaseConfig::class.java
        )
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

    if (isEnable(ctx, "launchpad")) {
        applicationBuilder
            .parent(parent)
            .child(
                LaunchpadModuleConfig::class.java,
            )
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8084")
            .banner(Banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
  _                            _                     _ 
 | |                          | |                   | |
 | |     __ _ _   _ _ __   ___| |__  _ __   __ _  __| |
 | |    / _` | | | | '_ \ / __| '_ \| '_ \ / _` |/ _` |
 | |___| (_| | |_| | | | | (__| | | | |_) | (_| | (_| |
 |______\__,_|\__,_|_| |_|\___|_| |_| .__/ \__,_|\__,_|
                                    | |                
                                    |_|  
                """.trimIndent()
                )
                out.println("")
                out.println("")
            })
            .run(*args)
    }

}

fun isEnable(ctx: ConfigurableApplicationContext, module: String): Boolean {
//    return ctx.environment.getProperty("io.hamal.module.$module.enabled", "false") == "true"
    return true
}