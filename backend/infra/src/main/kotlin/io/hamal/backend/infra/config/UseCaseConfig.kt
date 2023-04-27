package io.hamal.backend.infra.config

import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.backend.infra.adapter.BackendUseCaseInvokerAdapter
import io.hamal.backend.infra.adapter.BackendUseCaseRegistryAdapter
import io.hamal.lib.ddd.usecase.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {

    @Bean
    open fun getUseCase(): GetUseCasePort = BackendUseCaseRegistryAdapter()

    @Bean
    open fun getCommandUseCase(): GetCommandUseCasePort = getUseCase()

    @Bean
    open fun getQueryUseCase(): GetQueryUseCasePort = getUseCase()

    @Bean
    open fun getFetchOneUseCase(): GetFetchOneUseCasePort = getUseCase()

    @Bean
    open fun invokeUseCase(
        flushDomainNotificationPort: FlushDomainNotificationPort
    ): InvokeUseCasePort = BackendUseCaseInvokerAdapter(
        getCommandUseCase(),
        getQueryUseCase(),
        getFetchOneUseCase(),
        flushDomainNotificationPort
    )
}