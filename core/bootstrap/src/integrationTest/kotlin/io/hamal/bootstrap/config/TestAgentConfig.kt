package io.hamal.bootstrap.config

import io.hamal.bootstrap.httpTemplate
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestAgentConfig {
    @Bean
    fun httpTemplateSupplier(): HttpTemplateSupplier = { httpTemplate }

}