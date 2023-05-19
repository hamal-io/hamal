package io.hamal.backend.usecase.request

import io.hamal.backend.core.exec.*
import io.hamal.backend.core.func.Func
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.ddd.RequestManyUseCase
import io.hamal.lib.domain.ddd.RequestOneUseCase

object ExecRequest {
    data class PlanExec(
        override val reqId: ReqId,
        override val shard: Shard,
        val func: Func,
        val trigger: InvokedTrigger
    ) : RequestOneUseCase<PlannedExec>

    data class SchedulePlannedExec(
        override val reqId: ReqId,
        override val shard: Shard,
        val plannedExec: PlannedExec
    ) : RequestOneUseCase<ScheduledExec>

    data class QueueScheduledExec(
        override val reqId: ReqId,
        override val shard: Shard,
        val scheduledExec: ScheduledExec
    ) : RequestOneUseCase<QueuedExec>

    data class DequeueExec(
        override val reqId: ReqId,
        override val shard: Shard,
    ) : RequestManyUseCase<StartedExec>

    data class CompleteStartedExec(
        override val reqId: ReqId,
        override val shard: Shard,
        val startedExec: StartedExec
    ) : RequestOneUseCase<CompleteExec>
}