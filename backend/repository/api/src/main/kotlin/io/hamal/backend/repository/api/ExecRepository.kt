package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecSecrets

interface ExecCmdRepository {

    fun plan(computeId: ComputeId, execToPlan: ExecToPlan): PlannedExec
    fun schedule(computeId: ComputeId, planedExec: PlannedExec): ScheduledExec
    fun enqueue(computeId: ComputeId, scheduledExec: ScheduledExec): QueuedExec
    fun complete(computeId: ComputeId, inFlightExec: InFlightExec): CompletedExec
    fun dequeue(computeId: ComputeId): List<InFlightExec>

    data class ExecToPlan(
        val id: ExecId,
        val shard: Shard,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
        val trigger: Invocation
    )

}

interface ExecQueryRepository {
    fun find(execId: ExecId): Exec?

    fun list(afterId: ExecId, limit: Int): List<Exec>
}