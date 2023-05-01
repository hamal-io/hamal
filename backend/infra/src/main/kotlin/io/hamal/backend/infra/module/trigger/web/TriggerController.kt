package io.hamal.backend.infra.module.trigger.web

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.usecase.trigger.ManualTriggerInvocation
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.util.SnowflakeId
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
class SomeTest(val id: JobId, val definitionId: JobDefinitionId, val shard: Shard)

@RestController
open class JobController @Autowired constructor(
    val request: InvokeUseCasePort,
    val generateDomainId: GenerateDomainIdPort,
    val notifyDomainPort: NotifyDomainPort,
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") rawTriggerId: String,
        @RequestAttribute("shard") shard: Shard
    ): InvokedTrigger {

        val triggerId = TriggerId(SnowflakeId(rawTriggerId.toLong()))

//        val trigger = invoke(GetTriggerUseCase(triggerId))
//        val definition = invoke(GetJobDefinitionUseCase(trigger.jobDefinitionId))

//        notifyDomainPort.invoke(
//            Scheduled(
//                id = generateDomainId(shard, ::JobId),
//                shard = Shard(1),
//
//                inputs = counter.incrementAndGet()
//            )
//        )
//
//        val invokedTrigger = InvokedTrigger.Manual(
//            id = InvokedTriggerId(SnowflakeId(1)),
//            trigger = Trigger.ManualTrigger(
//                id = TriggerId(SnowflakeId(2)),
//                reference = TriggerReference("some-ref"),
//                jobDefinitionId = definition.id,
//            ),
//            invokedAt = InvokedAt(TimeUtils.now()),
//            invokedBy = AccountId(SnowflakeId(123))
//        )

//        val invokedTrigger = invokeUseCasePort.requestOne(
//            InvokeManualTriggerUseCase(
//                shard = shard,
//                triggerId = generateDomainId(shard, ::TriggerId)
//            )
//        )
//        return ResponseEntity.ok(
//            SomeTest(
//                generateDomainId(shard, ::JobId),
//                generateDomainId(shard, ::JobDefinitionId),
//                shard
//            )
//        )

        return request(ManualTriggerInvocation(RequestId(10), shard, triggerId))
    }

}