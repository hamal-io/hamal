package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.usecase.query.func.GetFuncUseCaseHandler
import io.hamal.backend.infra.usecase.query.func.ListFuncUseCaseHandler
import io.hamal.backend.infra.usecase.request.func.CreateFuncRequestHandler
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.FuncRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestFuncConfig {
    @Bean
    open fun createFuncRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        funcRequestRepository: FuncRequestRepository
    ) = CreateFuncRequestHandler(
        notifyDomainPort,
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