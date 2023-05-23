package io.hamal.backend.usecase.request

import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase
import io.hamal.lib.domain.vo.TriggerId

object TriggerRequest {
    data class ManualTriggerInvocation(
        override val reqId: ReqId,
        override val shard: Shard,
        val triggerId: TriggerId
    ) : RequestOneUseCase<InvokedTrigger.Manual>

}
