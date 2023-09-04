package guru.fn

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@ComponentScan
@EnableWebMvc
@EnableAutoConfiguration
open class FrontendConfig