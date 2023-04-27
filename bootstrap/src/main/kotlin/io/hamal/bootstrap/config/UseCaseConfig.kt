package io.hamal.bootstrap.config

import io.hamal.bootstrap.adapter.DefaultUseCaseInvokerAdapter
import io.hamal.bootstrap.adapter.DefaultUseCaseRegistryAdapter
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