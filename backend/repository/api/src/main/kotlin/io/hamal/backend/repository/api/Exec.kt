package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId

interface ExecCmdRepository {
    fun plan(reqId: ReqId, execToPlan: ExecToPlan): PlannedExec
    fun schedule(reqId: ReqId, planedExec: PlannedExec): ScheduledExec
    fun enqueue(reqId: ReqId, scheduledExec: ScheduledExec): QueuedExec
    fun complete(reqId: ReqId, startedExec: StartedExec): CompletedExec
    fun dequeue(): List<StartedExec>

    data class ExecToPlan(
        val id: ExecId,
        val shard: Shard,
        val correlation: Correlation?,
        val code: Code,
        val trigger: Invocation
    )

}

interface ExecQueryRepository {
    fun find(execId: ExecId): Exec?

    fun list(afterId: ExecId, limit: Int): List<Exec>
}