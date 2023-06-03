package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*

interface ExecCmdRepository {
    fun plan(computeId: ComputeId, execToPlan: ExecToPlan): PlannedExec
    fun schedule(computeId: ComputeId, planedExec: PlannedExec): ScheduledExec
    fun enqueue(computeId: ComputeId, scheduledExec: ScheduledExec): QueuedExec
    fun complete(computeId: ComputeId, inFlightExec: InFlightExec): CompletedExec
    fun dequeue(computeId: ComputeId): List<InFlightExec>

    data class ExecToPlan(
        val accountId: AccountId,
        val id: ExecId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code,
        val invocation: Invocation
    )
}

interface ExecQueryRepository {
    fun find(execId: ExecId): Exec?

    fun list(afterId: ExecId, limit: Int): List<Exec>
}