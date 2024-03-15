package rocks.candy

import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc


@EnableWebMvc
@Configuration
@SpringBootApplication
class FnGuru

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .parent(FnGuru::class.java)
        .web(WebApplicationType.SERVLET)
        .properties(
            "server.port=6006"
        )
        .bannerMode(Banner.Mode.OFF)
        .run(*args)
}
