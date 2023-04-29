package io.hamal.backend.infra.module.flow_definition.web

import io.hamal.backend.infra.DomainType
import io.hamal.backend.infra.Request
import io.hamal.backend.infra.requests
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.FlowDefinitionId
import io.hamal.lib.vo.RegionId
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
        @RequestAttribute regionId: RegionId
    ): Request<FlowDefinitionId> {
//        return request(CreateFlowDefinition(regionId))

        val domainId = generateDomainId(regionId, ::FlowDefinitionId)
        val domainType = DomainType.FlowDefinition


        val result = Request<FlowDefinitionId>(
            id = 10,
            regionId = regionId,
            domainId = domainId,
            domainVersion = 0,
            domainType = domainType,
            requestType = "CreateFlowDefinition"
        )


        requests.add(result)

        return result
    }
}