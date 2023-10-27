package io.hamal.testbed.admin

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryFixedTime
import io.hamal.core.config.BackendBasePath
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@TestConfiguration
class ApiTestConfig {
    @Bean
    fun backendBasePath() = BackendBasePath("/tmp/hamal/test-sqlite/${UUID.randomUUID()}")

    @Bean
    fun delayRetry(): DelayRetry = DelayRetryFixedTime(1.milliseconds)
}