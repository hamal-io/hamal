package io.hamal.runner

import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
class Runner

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .parent(RunnerConfig::class.java)
        .banner { _: Environment, _: Class<*>, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(
                """
  _    _                       _            _____                             
 | |  | |                     | |          |  __ \                            
 | |__| | __ _ _ __ ___   __ _| |  ______  | |__) |   _ _ __  _ __   ___ _ __ 
 |  __  |/ _` | '_ ` _ \ / _` | | |______| |  _  / | | | '_ \| '_ \ / _ \ '__|
 | |  | | (_| | | | | | | (_| | |          | | \ \ |_| | | | | | | |  __/ |   
 |_|  |_|\__,_|_| |_| |_|\__,_|_|          |_|  \_\__,_|_| |_|_| |_|\___|_| 
            """.trimIndent()
            )
            out.println("")
            out.println("")
        }
        .web(NONE)
        .run(*args)
}
