package io.hamal.backend.repository.api

import io.hamal.lib.domain.*
import io.hamal.lib.domain.vo.*

interface ExecCmdRepository {
    fun plan(cmd: PlanCmd): PlannedExec
    fun schedule(cmd: ScheduleCmd): ScheduledExec
    fun queue(cmd: QueueCmd): QueuedExec
    fun complete(cmd: CompleteCmd): CompletedExec
    fun fail(cmd: FailCmd): FailedExec
    fun start(cmd: StartCmd): List<StartedExec>
    fun clear()

    data class PlanCmd(
        val id: CmdId,
        val execId: ExecId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val secrets: ExecSecrets,
        val code: Code
    )

    data class ScheduleCmd(
        val id: CmdId,
        val execId: ExecId
    )

    data class QueueCmd(
        val id: CmdId,
        val execId: ExecId
    )

    data class StartCmd(
        val id: CmdId,
    )

    data class CompleteCmd(
        val id: CmdId,
        val execId: ExecId
    )

    data class FailCmd(
        val id: CmdId,
        val execId: ExecId
    )
}

interface ExecQueryRepository {
    fun find(execId: ExecId): Exec?
    fun list(block: ExecQuery.() -> Unit): List<Exec>
    data class ExecQuery(
        var afterId: ExecId = ExecId(0),
        var limit: Limit = Limit(1)
    )
}