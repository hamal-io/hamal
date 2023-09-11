package io.hamal.admin

import io.hamal.core.route.adhoc.AdhocRoute
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@ComponentScan
@EnableWebMvc
@EnableAutoConfiguration
@Import(AdhocRoute::class)
open class AdminConfig