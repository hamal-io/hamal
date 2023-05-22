package io.hamal.backend.repository.api

import io.hamal.backend.core.exec.*
import io.hamal.backend.core.func.Func
import io.hamal.backend.core.trigger.Cause
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.ExecId

interface ExecRequestRepository {

    fun plan(reqId: ReqId, execToPlan: ExecToPlan): PlannedExec
    fun schedule(reqId: ReqId, planedExec: PlannedExec): ScheduledExec
    fun queue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec
    fun complete(reqId: ReqId, startedExec: StartedExec): CompleteExec
    fun dequeue(): List<StartedExec>

    data class ExecToPlan(
        val shard: Shard,
        val id: ExecId,
        val definition: Func,
        val trigger: Cause
    )

}

interface ExecQueryRepository {
    fun find(execId: ExecId): Exec?

    fun list(afterId: ExecId, limit: Int): List<Exec>
}