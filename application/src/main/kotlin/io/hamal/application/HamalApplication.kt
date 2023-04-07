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
open class HamalApplication

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
HHHHHHHHH     HHHHHHHHH                                                            lllllll 
H:::::::H     H:::::::H                                                            l:::::l 
H:::::::H     H:::::::H                                                            l:::::l 
HH::::::H     H::::::HH                                                            l:::::l 
  H:::::H     H:::::H    aaaaaaaaaaaaa      mmmmmmm    mmmmmmm     aaaaaaaaaaaaa    l::::l 
  H:::::H     H:::::H    a::::::::::::a   mm:::::::m  m:::::::mm   a::::::::::::a   l::::l 
  H::::::HHHHH::::::H    aaaaaaaaa:::::a m::::::::::mm::::::::::m  aaaaaaaaa:::::a  l::::l 
  H:::::::::::::::::H             a::::a m::::::::::::::::::::::m           a::::a  l::::l 
  H:::::::::::::::::H      aaaaaaa:::::a m:::::mmm::::::mmm:::::m    aaaaaaa:::::a  l::::l 
  H::::::HHHHH::::::H    aa::::::::::::a m::::m   m::::m   m::::m  aa::::::::::::a  l::::l 
  H:::::H     H:::::H   a::::aaaa::::::a m::::m   m::::m   m::::m a::::aaaa::::::a  l::::l 
  H:::::H     H:::::H  a::::a    a:::::a m::::m   m::::m   m::::ma::::a    a:::::a  l::::l 
HH::::::H     H::::::HHa::::a    a:::::a m::::m   m::::m   m::::ma::::a    a:::::a l::::::l
H:::::::H     H:::::::Ha:::::aaaa::::::a m::::m   m::::m   m::::ma:::::aaaa::::::a l::::::l
H:::::::H     H:::::::H a::::::::::aa:::am::::m   m::::m   m::::m a::::::::::aa:::al::::::l
HHHHHHHHH     HHHHHHHHH  aaaaaaaaaa  aaaammmmmm   mmmmmm   mmmmmm  aaaaaaaaaa  aaaallllllll
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
                                                                                                                                                                                                  
                                                                                                                                                                                          dddddddd
                        LLLLLLLLLLL                                                                                   hhhhhhh                                                             d::::::d
                        L:::::::::L                                                                                   h:::::h                                                             d::::::d
                        L:::::::::L                                                                                   h:::::h                                                             d::::::d
                        LL:::::::LL                                                                                   h:::::h                                                             d:::::d 
                          L:::::L                 aaaaaaaaaaaaa  uuuuuu    uuuuuunnnn  nnnnnnnn        cccccccccccccccch::::h hhhhh      ppppp   ppppppppp     aaaaaaaaaaaaa      ddddddddd:::::d 
                          L:::::L                 a::::::::::::a u::::u    u::::un:::nn::::::::nn    cc:::::::::::::::ch::::hh:::::hhh   p::::ppp:::::::::p    a::::::::::::a   dd::::::::::::::d 
                          L:::::L                 aaaaaaaaa:::::au::::u    u::::un::::::::::::::nn  c:::::::::::::::::ch::::::::::::::hh p:::::::::::::::::p   aaaaaaaaa:::::a d::::::::::::::::d 
                          L:::::L                          a::::au::::u    u::::unn:::::::::::::::nc:::::::cccccc:::::ch:::::::hhh::::::hpp::::::ppppp::::::p           a::::ad:::::::ddddd:::::d 
                          L:::::L                   aaaaaaa:::::au::::u    u::::u  n:::::nnnn:::::nc::::::c     ccccccch::::::h   h::::::hp:::::p     p:::::p    aaaaaaa:::::ad::::::d    d:::::d 
                          L:::::L                 aa::::::::::::au::::u    u::::u  n::::n    n::::nc:::::c             h:::::h     h:::::hp:::::p     p:::::p  aa::::::::::::ad:::::d     d:::::d 
                          L:::::L                a::::aaaa::::::au::::u    u::::u  n::::n    n::::nc:::::c             h:::::h     h:::::hp:::::p     p:::::p a::::aaaa::::::ad:::::d     d:::::d 
                          L:::::L         LLLLLLa::::a    a:::::au:::::uuuu:::::u  n::::n    n::::nc::::::c     ccccccch:::::h     h:::::hp:::::p    p::::::pa::::a    a:::::ad:::::d     d:::::d 
                        LL:::::::LLLLLLLLL:::::La::::a    a:::::au:::::::::::::::uun::::n    n::::nc:::::::cccccc:::::ch:::::h     h:::::hp:::::ppppp:::::::pa::::a    a:::::ad::::::ddddd::::::dd
                        L::::::::::::::::::::::La:::::aaaa::::::a u:::::::::::::::un::::n    n::::n c:::::::::::::::::ch:::::h     h:::::hp::::::::::::::::p a:::::aaaa::::::a d:::::::::::::::::d
                        L::::::::::::::::::::::L a::::::::::aa:::a uu::::::::uu:::un::::n    n::::n  cc:::::::::::::::ch:::::h     h:::::hp::::::::::::::pp   a::::::::::aa:::a d:::::::::ddd::::d
                        LLLLLLLLLLLLLLLLLLLLLLLL  aaaaaaaaaa  aaaa   uuuuuuuu  uuuunnnnnn    nnnnnn    cccccccccccccccchhhhhhh     hhhhhhhp::::::pppppppp      aaaaaaaaaa  aaaa  ddddddddd   ddddd
                                                                                                                                          p:::::p                                                 
                                                                                                                                          p:::::p                                                 
                                                                                                                                         p:::::::p                                                
                                                                                                                                         p:::::::p                                                
                                                                                                                                         p:::::::p                                                
                                                                                                                                         ppppppppp                                                
                                                                                                                                                                                                  
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