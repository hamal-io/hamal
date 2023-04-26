package io.hamal.application

import io.hamal.application.config.*
import io.hamal.module.launchpad.infra.LaunchpadModuleConfig
import io.hamal.module.worker.infra.WorkerModuleConfig
import io.module.hamal.queue.infra.QueueModuleConfig
import kotlinx.serialization.ExperimentalSerializationApi
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
class HamalApplication

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val applicationBuilder = SpringApplicationBuilder()
        .main(HamalApplication::class.java)
        .parent(
//            DatabaseConfig::class.java,
            AsyncConfig::class.java,
            DomainNotificationConfig::class.java,
            LoggerConfig::class.java,
            UseCaseConfig::class.java,
            WebConfig::class.java
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

//    if (isEnable(ctx, "bus")) {
//        applicationBuilder
//            .parent(parent)
//            .child(
//                BusModuleConfig::class.java,
//            )
//            .web(WebApplicationType.NONE)
//            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
//                out.println("")
//                out.println("")
//                out.println(
//                    """
//  ____
// |  _ \
// | |_) |_   _ ___
// |  _ <| | | / __|
// | |_) | |_| \__ \
// |____/ \__,_|___/
//                """.trimIndent()
//                )
//                out.println("")
//                out.println("")
//            }
//            .run(*args)
//    }

    if (isEnable(ctx, "launchpad")) {
        applicationBuilder
            .parent(parent)
            .child(
                LaunchpadModuleConfig::class.java,
            )
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8084")
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
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
            }
            .run(*args)
    }

    if (isEnable(ctx, "queue")) {
        applicationBuilder
            .parent(parent)
            .child(
                QueueModuleConfig::class.java,
            )
            .web(WebApplicationType.NONE)
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
   ____                        
  / __ \                       
 | |  | |_   _  ___ _   _  ___ 
 | |  | | | | |/ _ \ | | |/ _ \
 | |__| | |_| |  __/ |_| |  __/
  \___\_\\__,_|\___|\__,_|\___|
                """.trimIndent()
                )
                out.println("")
                out.println("")
            }
            .run(*args)
    }

    if (isEnable(ctx, "worker")) {
        applicationBuilder
            .parent(parent)
            .child(
                WorkerModuleConfig::class.java,
            )
            .web(WebApplicationType.NONE)
            .banner { _: Environment?, _: Class<*>?, out: PrintStream ->
                out.println("")
                out.println("")
                out.println(
                    """
 __          __        _             
 \ \        / /       | |            
  \ \  /\  / /__  _ __| | _____ _ __ 
   \ \/  \/ / _ \| '__| |/ / _ \ '__|
    \  /\  / (_) | |  |   <  __/ |   
     \/  \/ \___/|_|  |_|\_\___|_|
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