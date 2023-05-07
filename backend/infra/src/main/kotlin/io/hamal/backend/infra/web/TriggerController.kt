package io.hamal.backend.infra.web

import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.backend.usecase.request.TriggerRequest
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.vo.*
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
open class TriggerController @Autowired constructor(
    val request: InvokeUseCasePort,
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: TriggerId,
        @RequestAttribute("shard") shard: Shard,
        @RequestAttribute("tenant") tenant: Tenant,
        @RequestAttribute("requestId") requestId: RequestId
    ): InvokedTrigger {

//        val triggerId = TriggerId(SnowflakeId(rawTriggerId.toLong()))
//        return InvokedTrigger.Manual(
//            id = InvokedTriggerId(123),
//            trigger = Trigger.ManualTrigger(
//                id = triggerId,
//                reference = TriggerReference("some_ref"),
//                jobDefinitionId = JobDefinitionId(0)
//            ),
//            invokedBy = tenant.id,
//            invokedAt = InvokedAt.now()
//        )
        return request(
            TriggerRequest.ManualTriggerInvocation(
                requestId = requestId,
                shard = shard,
                triggerId = triggerId
            )
        )

    }
}