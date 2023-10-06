package io.hamal.backend.admin

import io.hamal.core.config.BackendBasePath
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.util.*

@TestConfiguration
open class AdminTestConfig {
    @Bean
    fun hubBasePath() = BackendBasePath("/tmp/hamal/test-admin/${UUID.randomUUID()}")
}