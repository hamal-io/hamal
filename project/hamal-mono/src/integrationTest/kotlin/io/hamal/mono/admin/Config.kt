package io.hamal.mono.admin

import io.hamal.core.config.HubBasePath
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.util.*

@TestConfiguration
open class AdminTestConfig {
    @Bean
    fun hubBasePath() = HubBasePath("/tmp/hamal/test-admin/${UUID.randomUUID()}")
}