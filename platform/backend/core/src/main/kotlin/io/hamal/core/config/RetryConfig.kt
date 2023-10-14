package io.hamal.core.config

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryExponentialTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@Configuration
@Profile("!test")
open class RetryConfig {
    @Bean
    open fun delayRetry(): DelayRetry = DelayRetryExponentialTime(
        base = 10.milliseconds,
        maxBackOffTime = 2.seconds
    )
}
