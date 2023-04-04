package io.hamal.application

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.core.env.Environment
import java.io.PrintStream

@SpringBootApplication
open class HamalApplication

//Doh font

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
            out.println("""
                                                                                                                                                
                                                                                                                                                
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
                                                                                                                                                
                                                                                                                                         
                                               
            """.trimIndent())
            out.println("")
            out.println("")
        }
        .web(WebApplicationType.NONE)
        .run()
}