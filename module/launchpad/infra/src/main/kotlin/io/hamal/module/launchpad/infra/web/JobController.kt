package io.hamal.module.launchpad.infra.web

import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.module.launchpad.application.trigger.InvokeManualTriggerUseCase
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Serializable
class SomeTest(val id: JobId, val definitionId: JobDefinitionId)

@RestController
open class JobController(
    @Autowired val invokeUseCasePort: InvokeUseCasePort
) {

    //FIXME must operate on manual trigger instead of job def hack
    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: String
    ): ResponseEntity<SomeTest> {
        invokeUseCasePort.command(
            InvokeManualTriggerUseCase(
                regionId = RegionId("this"),
                triggerId = TriggerId(triggerId)
            )
        )
        return ResponseEntity.ok(
            SomeTest(
                JobId(UUID.randomUUID().toString()),
                JobDefinitionId(UUID.randomUUID().toString())
            )
        )
    }

}