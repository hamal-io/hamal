package io.hamal.testbed.api

import io.hamal.core.config.BackendBasePath
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.util.*

@TestConfiguration
open class ApiTestConfig {
    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/test-sqlite/${UUID.randomUUID()}")
}