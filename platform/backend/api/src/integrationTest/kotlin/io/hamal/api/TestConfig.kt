package io.hamal.api

import io.hamal.core.component.DelayRetry
import io.hamal.core.component.DelayRetryNoDelay
import io.hamal.core.component.SetupInternalTopics
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
open class TestConfig {

    @Bean
    open fun delayRetry(): DelayRetry = DelayRetryNoDelay

    @Bean
    open fun commandLineRunnerTest() = CommandLineRunner {
        setupInternalTopics()
    }

    @Autowired
    private lateinit var setupInternalTopics: SetupInternalTopics
}