package io.hamal.application

import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.core.env.Environment
import java.io.PrintStream

object HamalApplication

fun main(args: Array<String>) {
    val applicationBuilder = SpringApplicationBuilder()
        .main(HamalApplication::class.java)
        .parent(
//            LoggerConfig::class.java,
//            SupplierConfig::class.java,
//            SerdeConfig::class.java,
//            UseCaseRegistryConfig::class.java
        )
        .banner { _: Environment, _: Class<*>, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(""" '-. .-.   ('-.     _   .-')      ('-.""")
            out.println("""'( OO )  /  ( OO ).-.( '.( OO )_   ( OO ).-.""")
            out.println(""",--. ,--.  / . --. / ,--.   ,--.) / . --. / ,--.""")
            out.println("""|  | |  |  | \-.  \  |   `.'   |  | \-.  \  |  |.-')""")
            out.println("""|   .|  |.-'-'  |  | |         |.-'-'  |  | |  | OO )""")
            out.println("""|       | \| |_.'  | |  |'.'|  | \| |_.'  | |  |`-' | """)
            out.println("""|  .-.  |  |  .-.  | |  |   |  |  |  .-.  |(|  '---.'""")
            out.println("""|  | |  |  |  | |  | |  |   |  |  |  | |  | |      |""")
            out.println(""""--' `--'  `--' `--' `--'   `--'  `--' `--' `------'""")
            out.println("")
            out.println("")
        }
        .web(WebApplicationType.NONE)
        .run()
}