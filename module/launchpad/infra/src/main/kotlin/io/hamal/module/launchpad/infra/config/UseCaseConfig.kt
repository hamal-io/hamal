package io.hamal.module.launchpad.infra.config

import io.hamal.module.launchpad.application.job.CreateJobDefinitionUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {
    @Bean
    open fun createJobDefinitionUseCase() = CreateJobDefinitionUseCase.Operation()
}