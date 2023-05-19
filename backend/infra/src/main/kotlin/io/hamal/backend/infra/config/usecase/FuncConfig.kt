package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.usecase.request.func.CreateFuncRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FuncConfig {
    @Bean
    open fun createFuncRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        funcRepository: FuncRepository
    ) = CreateFuncRequestHandler(
        notifyDomainPort,
        funcRepository
    )

}