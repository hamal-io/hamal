package io.hamal.backend.usecase.request

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestOneUseCase
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.TriggerName

object TriggerRequest {
    data class TriggerCreation(
        override val reqId: ReqId,
        override val shard: Shard,
        val name: TriggerName,
        val code: Code
    ) : RequestOneUseCase<Trigger>

}
