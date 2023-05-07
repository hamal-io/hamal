package io.hamal.backend.usecase.request

import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.core.RequestId
import io.hamal.lib.core.Shard
import io.hamal.lib.core.ddd.usecase.RequestOneUseCase
import io.hamal.lib.core.vo.TriggerId

object TriggerRequest {
    data class ManualTriggerInvocation(
        override val requestId: RequestId,
        override val shard: Shard,
        val triggerId: TriggerId
    ) : RequestOneUseCase<InvokedTrigger.Manual>

}
