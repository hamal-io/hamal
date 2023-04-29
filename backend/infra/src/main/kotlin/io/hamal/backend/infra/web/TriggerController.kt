package io.hamal.backend.infra.web

import io.hamal.backend.application.trigger.InvokeManualTriggerUseCase
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.*
import io.hamal.lib.vo.port.GenerateDomainIdPort
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Serializable
class SomeTest(val id: JobId, val definitionId: JobDefinitionId, val regionId: RegionId)

@RestController

open class JobController @Autowired constructor(
    val invokeUseCasePort: InvokeUseCasePort,
    val generateDomainId: GenerateDomainIdPort
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: String,
        @RequestAttribute("regionId") regionId: RegionId
    ): ResponseEntity<SomeTest> {

        val invokedTrigger = invokeUseCasePort.executeOne(
            InvokeManualTriggerUseCase(
                regionId = regionId,
                triggerId = generateDomainId(regionId, ::TriggerId)
            )
        )
        return ResponseEntity.ok(
            SomeTest(
                generateDomainId(regionId, ::JobId),
                generateDomainId(regionId, ::JobDefinitionId),
                regionId
            )
        )
    }

}