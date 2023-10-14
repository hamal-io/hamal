package io.hamal.api

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryNoDelay
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
open class TestConfig {
    @Bean
    open fun delayRetry(): DelayRetry = DelayRetryNoDelay
}