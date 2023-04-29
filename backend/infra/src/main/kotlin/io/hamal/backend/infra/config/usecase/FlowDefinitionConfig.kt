package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.store.impl.DefaultFlowDefinitionStore
import io.hamal.backend.usecase.flow_definition.CreateFlowDefinitionRequestHandler
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FlowDefinitionConfig {
    @Bean
    open fun createFlowDefinitionRequestHandler(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = CreateFlowDefinitionRequestHandler(
        notifyDomainPort,
        generateDomainId,
        DefaultFlowDefinitionStore
    )

//    @Bean
//    open fun getFlowDefinition() = GetFlowDefinitionUseCase.Operation()
}