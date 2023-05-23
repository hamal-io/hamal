package io.hamal.backend.infra.web

import io.hamal.backend.core.tenant.Tenant
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.backend.usecase.request.TriggerRequest
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.InvokeUseCasePort
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.TriggerId
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Serializable
class SomeTest(val id: ExecId, val definitionId: FuncId, val shard: Shard)

@RestController
open class TriggerController @Autowired constructor(
    val request: InvokeUseCasePort,
) {

    @PostMapping("/v1/triggers/manual/{triggerId}")
    fun manualTrigger(
        @PathVariable("triggerId") triggerId: TriggerId,
        @RequestAttribute("shard") shard: Shard,
        @RequestAttribute("tenant") tenant: Tenant,
        @RequestAttribute("requestId") reqId: ReqId
    ): InvokedTrigger {
        return request(
            TriggerRequest.ManualTriggerInvocation(
                reqId = reqId,
                shard = shard,
                triggerId = triggerId
            )
        )

    }
}