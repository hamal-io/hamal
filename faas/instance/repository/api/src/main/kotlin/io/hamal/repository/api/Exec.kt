package io.hamal.repository.api

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import kotlinx.serialization.Serializable

interface ExecRepository : ExecCmdRepository, ExecQueryRepository

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
        val code: CodeType,
        val invocation: Invocation
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
        val execId: ExecId,
        val cause: ErrorType
    )
}

interface ExecQueryRepository {
    fun get(execId: ExecId) = find(execId) ?: throw NoSuchElementException("Exec not found")
    fun find(execId: ExecId): Exec?
    fun list(block: ExecQuery.() -> Unit): List<Exec>
    data class ExecQuery(
        var afterId: ExecId = ExecId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}

@Serializable
sealed class Exec : DomainObject<ExecId> {
    abstract override val id: ExecId
    abstract val cmdId: CmdId
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val code: CodeType
    abstract val invocation: Invocation

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exec

        if (id != other.id) return false
        if (cmdId != other.cmdId) return false
        return status == other.status
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + cmdId.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}

@Serializable
class PlannedExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val code: CodeType,
    override val invocation: Invocation
// FIXME    val plannedAt: PlannedAt
) : Exec() {
    override val status = ExecStatus.Planned

    override fun toString(): String {
        return "PlannedExec($id)"
    }

}

@Serializable
class ScheduledExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    val plannedExec: PlannedExec,
    val scheduledAt: ScheduledAt
) : Exec() {
    override val status = ExecStatus.Scheduled
    override val correlation get() = plannedExec.correlation
    override val inputs get() = plannedExec.inputs
    override val code get() = plannedExec.code
    override val invocation get() = plannedExec.invocation
    override fun toString(): String {
        return "ScheduledExec($id)"
    }

}

@Serializable
class QueuedExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    val scheduledExec: ScheduledExec,
    val queuedAt: QueuedAt
) : Exec() {
    override val status = ExecStatus.Queued
    override val correlation get() = scheduledExec.correlation
    override val inputs get() = scheduledExec.inputs
    override val code get() = scheduledExec.code
    override val invocation get() = scheduledExec.invocation
    override fun toString(): String {
        return "QueuedExec($id)"
    }
}


@Serializable
class StartedExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    val queuedExec: QueuedExec
) : Exec() {
    override val status = ExecStatus.Started
    override val correlation get() = queuedExec.correlation
    override val inputs get() = queuedExec.inputs
    override val code get() = queuedExec.code
    override val invocation get() = queuedExec.invocation
    override fun toString(): String {
        return "StartedExec($id)"
    }
}

@Serializable
class CompletedExec(
    override val cmdId: CmdId,

    override val id: ExecId,
    val startedExec: StartedExec,
    val completedAt: CompletedAt
) : Exec() {
    override val status = ExecStatus.Completed
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val code get() = startedExec.code
    override val invocation get() = startedExec.invocation

    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    val startedExec: StartedExec,
    //FIXME failedAt
    val failedAt: FailedAt,
    val cause: ErrorType
) : Exec() {
    override val status = ExecStatus.Failed
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val code get() = startedExec.code
    override val invocation get() = startedExec.invocation
    override fun toString(): String {
        return "FailedExec($id)"
    }

}