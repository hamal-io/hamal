package io.hamal.bootstrap

import io.hamal.agent.AgentConfig
import io.hamal.backend.instance.BackendConfig
import io.hamal.frontend.FrontendConfig
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


    if (isEnable(ctx, "backend")) {
        applicationBuilder
            .parent(parent)
            .child(BackendConfig::class.java)
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8084")
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
  ____             _                  _ 
 |  _ \           | |                | |
 | |_) | __ _  ___| | _____ _ __   __| |
 |  _ < / _` |/ __| |/ / _ \ '_ \ / _` |
 | |_) | (_| | (__|   <  __/ | | | (_| |
 |____/ \__,_|\___|_|\_\___|_| |_|\__,_|
                """.trimIndent()
                )
                out.println("")
                out.println("")
            }
            .run(*args)
    }


    if (isEnable(ctx, "agent")) {
        applicationBuilder
            .parent(parent)
            .child(
                AgentConfig::class.java,
            )
            .web(WebApplicationType.NONE)
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
                           _   
     /\                   | |  
    /  \   __ _  ___ _ __ | |_ 
   / /\ \ / _` |/ _ \ '_ \| __|
  / ____ \ (_| |  __/ | | | |_ 
 /_/    \_\__, |\___|_| |_|\__|
           __/ |               
          |___/                
                """.trimIndent()
                )
                out.println("")
                out.println("")
            }
            .run(*args)
    }

    if (isEnable(ctx, "frontend")) {
        applicationBuilder
            .parent(parent)
            .child(FrontendConfig::class.java)
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8085")
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
  ______               _                 _ 
 |  ____|             | |               | |
 | |__ _ __ ___  _ __ | |_ ___ _ __   __| |
 |  __| '__/ _ \| '_ \| __/ _ \ '_ \ / _` |
 | |  | | | (_) | | | | ||  __/ | | | (_| |
 |_|  |_|  \___/|_| |_|\__\___|_| |_|\__,_|
                """.trimIndent()
                )
                out.println("")
                out.println("")
            }
            .run(*args)
    }

}

fun isEnable(ctx: ConfigurableApplicationContext, module: String): Boolean {
//    return ctx.environment.getProperty("io.hamal.module.$module.enabled", "false") == "true"
    return true
}