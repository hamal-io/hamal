package io.hamal.backend.infra.module.flow_definition.web

import io.hamal.backend.core.model.FlowDefinition
import io.hamal.backend.usecase.flow_definition.FlowDefinitionRequest.*
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.ddd.usecase.RequestId
import io.hamal.lib.vo.Shard
import io.hamal.lib.vo.port.GenerateDomainIdPort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController

@RestController
open class FlowDefinitionController(
    @Autowired val request: InvokeUseCasePort,
    @Autowired val generateDomainId: GenerateDomainIdPort
) {

    @PostMapping("/v1/flow-definitions")
    fun createFlowDefinition(
        @RequestAttribute shard: Shard
    ): FlowDefinition {
//        return request(CreateFlowDefinition(shard))
        return request(
            FlowDefinitionCreation(
                RequestId(10),
                shard
            )
        )
    }
}