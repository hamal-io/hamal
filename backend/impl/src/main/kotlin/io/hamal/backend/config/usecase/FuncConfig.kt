package io.hamal.backend.config.usecase

import io.hamal.backend.cmd.handler.func.CreateFuncRequestHandler
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestFuncConfig {
    @Bean
    open fun createFuncRequestHandler(
        eventEmitter: EventEmitter,
        funcRequestRepository: FuncRequestRepository
    ) = CreateFuncRequestHandler(
        eventEmitter,
        funcRequestRepository
    )

}
