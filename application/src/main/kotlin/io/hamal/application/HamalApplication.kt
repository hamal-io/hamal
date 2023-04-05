package io.hamal.application

import io.hamal.application.config.LoggerConfig
import io.hamal.application.config.UseCaseConfig
import io.hamal.lib.ddd.usecase.InvokeQueryUseCasePort
import io.hamal.lib.ddd.usecase.QueryUseCase
import io.hamal.lib.ddd.usecase.QueryUseCasePayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
open class HamalApplication
//Doh font

data class SomePayload(val x: Int, val y: Int) : QueryUseCasePayload


class SomeQueryUseCase : QueryUseCase<Int, SomePayload>(Int::class, SomePayload::class) {
    override fun invoke(payload: SomePayload): List<Int> {
        return listOf(payload.x + payload.y)
    }
}

@Configuration
open class DifferentPlace {

    @Bean
    open fun someUseCase() = SomeQueryUseCase()

}

@Configuration
open class RunItConfig {
    @Bean
    open fun commandLineRunner(): CommandLineRunner {
        return object : CommandLineRunner {

            @Autowired
            lateinit var usecaseInvoker: InvokeQueryUseCasePort

            override fun run(vararg args: String?) {
                println("CALLED")
                val x = usecaseInvoker.query(Int::class, SomePayload(2800, 10))
                println(x)
            }

        }
    }
}

fun main(args: Array<String>) {
    val applicationBuilder = SpringApplicationBuilder()
        .main(HamalApplication::class.java)
        .parent(
            LoggerConfig::class.java,
            DifferentPlace::class.java,
            RunItConfig::class.java,
            UseCaseConfig::class.java
//            SupplierConfig::class.java,
//            SerdeConfig::class.java,
//            UseCaseRegistryConfig::class.java
        )
        .banner { _: Environment, _: Class<*>, out: PrintStream ->
            out.println("")
            out.println("")
            out.println(
                """
                                                                                                                                                
                                                                                                                                                
HHHHHHHHH     HHHHHHHHH               AAA               MMMMMMMM               MMMMMMMM               AAA               LLLLLLLLLLL             
H:::::::H     H:::::::H              A:::A              M:::::::M             M:::::::M              A:::A              L:::::::::L             
H:::::::H     H:::::::H             A:::::A             M::::::::M           M::::::::M             A:::::A             L:::::::::L             
HH::::::H     H::::::HH            A:::::::A            M:::::::::M         M:::::::::M            A:::::::A            LL:::::::LL             
  H:::::H     H:::::H             A:::::::::A           M::::::::::M       M::::::::::M           A:::::::::A             L:::::L               
  H:::::H     H:::::H            A:::::A:::::A          M:::::::::::M     M:::::::::::M          A:::::A:::::A            L:::::L               
  H::::::HHHHH::::::H           A:::::A A:::::A         M:::::::M::::M   M::::M:::::::M         A:::::A A:::::A           L:::::L               
  H:::::::::::::::::H          A:::::A   A:::::A        M::::::M M::::M M::::M M::::::M        A:::::A   A:::::A          L:::::L               
  H:::::::::::::::::H         A:::::A     A:::::A       M::::::M  M::::M::::M  M::::::M       A:::::A     A:::::A         L:::::L               
  H::::::HHHHH::::::H        A:::::AAAAAAAAA:::::A      M::::::M   M:::::::M   M::::::M      A:::::AAAAAAAAA:::::A        L:::::L               
  H:::::H     H:::::H       A:::::::::::::::::::::A     M::::::M    M:::::M    M::::::M     A:::::::::::::::::::::A       L:::::L               
  H:::::H     H:::::H      A:::::AAAAAAAAAAAAA:::::A    M::::::M     MMMMM     M::::::M    A:::::AAAAAAAAAAAAA:::::A      L:::::L         LLLLLL
HH::::::H     H::::::HH   A:::::A             A:::::A   M::::::M               M::::::M   A:::::A             A:::::A   LL:::::::LLLLLLLLL:::::L
H:::::::H     H:::::::H  A:::::A               A:::::A  M::::::M               M::::::M  A:::::A               A:::::A  L::::::::::::::::::::::L
H:::::::H     H:::::::H A:::::A                 A:::::A M::::::M               M::::::M A:::::A                 A:::::A L::::::::::::::::::::::L
HHHHHHHHH     HHHHHHHHHAAAAAAA                   AAAAAAAMMMMMMMM               MMMMMMMMAAAAAAA                   AAAAAAALLLLLLLLLLLLLLLLLLLLLLLL
                                                                                                                                                
                                                                                                                                         
                                               
            """.trimIndent()
            )
            out.println("")
            out.println("")
        }
        .web(WebApplicationType.NONE)
        .run()


}