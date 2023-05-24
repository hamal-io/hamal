package io.hamal.backend.config.usecase

import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.query.GetFuncUseCaseHandler
import io.hamal.backend.query.ListFuncUseCaseHandler
import io.hamal.backend.cmd.handler.func.CreateFuncRequestHandler
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

@Configuration
open class QueryFuncConfig {

    @Bean
    open fun listFuncUseCaseHandler(
        funcQueryRepository: FuncQueryRepository
    ) = ListFuncUseCaseHandler(
        funcQueryRepository
    )

    @Bean
    open fun getFuncUseCaseHandler(
        funcQueryRepository: FuncQueryRepository
    ) = GetFuncUseCaseHandler(
        funcQueryRepository
    )

}