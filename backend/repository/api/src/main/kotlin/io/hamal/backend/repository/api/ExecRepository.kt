package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.domain.CommandId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*

interface ExecCmdRepository {
    fun plan(commandId: CommandId, execToPlan: ExecToPlan): PlannedExec
    fun schedule(commandId: CommandId, planedExec: PlannedExec): ScheduledExec
    fun enqueue(commandId: CommandId, scheduledExec: ScheduledExec): QueuedExec
    fun complete(commandId: CommandId, inFlightExec: InFlightExec): CompletedExec
    fun dequeue(commandId: CommandId): List<InFlightExec>

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