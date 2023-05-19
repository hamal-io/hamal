package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.usecase.request.adhoc.ExecuteAdhocRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdhocExecConfig {
    @Bean
    open fun executeAdhocExecRequestHandler(
        funcRequestRepository: FuncRepository,
        notifyDomainPort: NotifyDomainPort
    ) = ExecuteAdhocRequestHandler(
        notifyDomainPort,
        funcRequestRepository
    )
}