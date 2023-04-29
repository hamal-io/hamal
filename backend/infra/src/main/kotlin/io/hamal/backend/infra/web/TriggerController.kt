package io.hamal.backend.infra.web

import io.hamal.backend.application.trigger.counter
import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.Scheduled
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.infra.DummyDb
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.*
import io.hamal.lib.vo.port.GenerateDomainIdPort
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
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
    val generateDomainId: GenerateDomainIdPort,
    val notifyDomainPort: NotifyDomainPort,
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") rawTriggerId: String,
        @RequestAttribute("regionId") regionId: RegionId
    ): InvokedTrigger {

        val triggerId = TriggerId(Snowflake.Id(rawTriggerId.toLong()))
        val trigger = DummyDb.triggers[triggerId]!!
        val definition = DummyDb.jobDefinitions[trigger.jobDefinitionId]!!
        // wait for


        notifyDomainPort.invoke(
            Scheduled(
                id = generateDomainId(regionId, ::JobId),
                regionId = RegionId(1),
                inputs = counter.incrementAndGet()
            )
        )

        val invokedTrigger = InvokedTrigger.Manual(
            id = InvokedTriggerId(Snowflake.Id(1)),
            trigger = Trigger.ManualTrigger(
                id = TriggerId(Snowflake.Id(2)),
                reference = TriggerReference("some-ref"),
                jobDefinitionId = definition.id,
            ),
            invokedAt = InvokedAt(TimeUtils.now()),
            invokedBy = AccountId(Snowflake.Id(123))
        )

//        val invokedTrigger = invokeUseCasePort.executeOne(
//            InvokeManualTriggerUseCase(
//                regionId = regionId,
//                triggerId = generateDomainId(regionId, ::TriggerId)
//            )
//        )
//        return ResponseEntity.ok(
//            SomeTest(
//                generateDomainId(regionId, ::JobId),
//                generateDomainId(regionId, ::JobDefinitionId),
//                regionId
//            )
//        )

        return invokedTrigger
    }

}