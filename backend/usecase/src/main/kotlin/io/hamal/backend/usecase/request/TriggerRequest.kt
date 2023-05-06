package io.hamal.backend.usecase.request

import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.vo.TriggerId

data class ManualTriggerInvocation(
    override val requestId: RequestId,
    override val shard: Shard,
    val triggerId: TriggerId
) : RequestOneUseCase<InvokedTrigger.Manual>