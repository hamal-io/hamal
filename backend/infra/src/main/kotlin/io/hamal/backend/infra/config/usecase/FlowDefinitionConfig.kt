package io.hamal.backend.infra.config.usecase

import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.request.flow_definition.FlowDefinitionRequest
import io.hamal.backend.store.impl.DefaultFlowDefinitionStore
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FlowDefinitionConfig {
    @Bean
    open fun createFlowDefinition(
        notifyDomainPort: NotifyDomainPort,
        generateDomainId: GenerateDomainIdPort
    ) = FlowDefinitionRequest.CreateFlowDefinition.Operation(
        notifyDomainPort,
        generateDomainId,
        DefaultFlowDefinitionStore
    )

//    @Bean
//    open fun getFlowDefinition() = GetFlowDefinitionUseCase.Operation()
}