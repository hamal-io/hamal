package io.hamal.application.config

import io.hamal.application.adapter.DefaultUseCaseInvokerAdapter
import io.hamal.application.adapter.DefaultUseCaseRegistryAdapter
import io.hamal.lib.ddd.usecase.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UseCaseConfig {

    @Bean
    open fun getUseCase(): GetUseCasePort = DefaultUseCaseRegistryAdapter()

    @Bean
    open fun getCommandUseCase(): GetCommandUseCasePort = getUseCase()

    @Bean
    open fun getQueryUseCase(): GetQueryUseCasePort = getUseCase()

    @Bean
    open fun getFetchOneUseCase(): GetFetchOneUseCasePort = getUseCase()

    @Bean(autowireCandidate = false)
    open fun invokeUseCase(): InvokeUseCasePort = DefaultUseCaseInvokerAdapter(
        getCommandUseCase(),
        getQueryUseCase(),
        getFetchOneUseCase()
    )

    @Bean
    open fun invokeQueryUseCase(): InvokeQueryUseCasePort = invokeUseCase()
}