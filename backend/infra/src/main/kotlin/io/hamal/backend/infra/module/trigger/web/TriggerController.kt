package io.hamal.backend.infra.module.trigger.web

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.usecase.trigger.ManualTriggerInvocation
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.util.Snowflake
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
class SomeTest(val id: FlowId, val definitionId: FlowDefinitionId, val shard: Shard)

@RestController
open class FlowController @Autowired constructor(
    val request: InvokeUseCasePort,
    val generateDomainId: GenerateDomainIdPort,
    val notifyDomainPort: NotifyDomainPort,
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") rawTriggerId: String,
        @RequestAttribute("shard") shard: Shard
    ): InvokedTrigger {

        val triggerId = TriggerId(Snowflake.Id(rawTriggerId.toLong()))

//        val trigger = invoke(GetTriggerUseCase(triggerId))
//        val definition = invoke(GetFlowDefinitionUseCase(trigger.flowDefinitionId))

//        notifyDomainPort.invoke(
//            Scheduled(
//                id = generateDomainId(shard, ::FlowId),
//                shard = Shard(1),
//
//                inputs = counter.incrementAndGet()
//            )
//        )
//
//        val invokedTrigger = InvokedTrigger.Manual(
//            id = InvokedTriggerId(Snowflake.Id(1)),
//            trigger = Trigger.ManualTrigger(
//                id = TriggerId(Snowflake.Id(2)),
//                reference = TriggerReference("some-ref"),
//                flowDefinitionId = definition.id,
//            ),
//            invokedAt = InvokedAt(TimeUtils.now()),
//            invokedBy = AccountId(Snowflake.Id(123))
//        )

//        val invokedTrigger = invokeUseCasePort.executeOne(
//            InvokeManualTriggerUseCase(
//                shard = shard,
//                triggerId = generateDomainId(shard, ::TriggerId)
//            )
//        )
//        return ResponseEntity.ok(
//            SomeTest(
//                generateDomainId(shard, ::FlowId),
//                generateDomainId(shard, ::FlowDefinitionId),
//                shard
//            )
//        )

        return request(ManualTriggerInvocation(shard, triggerId))
    }

}