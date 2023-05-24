package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.request.adhoc.ExecuteAdhocRequestHandler
import io.hamal.backend.repository.api.FuncRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdhocExecConfig {
    @Bean
    open fun executeAdhocExecRequestHandler(
        funcRequestRepository: FuncRequestRepository,
        notifyDomainPort: NotifyDomainPort
    ) = ExecuteAdhocRequestHandler(
        notifyDomainPort,
        funcRequestRepository
    )
}