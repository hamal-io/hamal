package io.hamal.backend.config.usecase

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.cmd.handler.adhoc.ExecuteAdhocRequestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdhocExecConfig {
    @Bean
    open fun executeAdhocExecRequestHandler(
        funcRequestRepository: FuncRequestRepository,
        eventEmitter: EventEmitter
    ) = ExecuteAdhocRequestHandler(
        eventEmitter,
        funcRequestRepository
    )
}