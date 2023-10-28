package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

interface ExecRepository : ExecCmdRepository, ExecQueryRepository

interface ExecCmdRepository : CmdRepository {
    fun plan(cmd: PlanCmd): PlannedExec
    fun schedule(cmd: ScheduleCmd): ScheduledExec
    fun queue(cmd: QueueCmd): QueuedExec

    fun complete(cmd: CompleteCmd): CompletedExec
    fun fail(cmd: FailCmd): FailedExec

    fun start(cmd: StartCmd): List<StartedExec>

    data class PlanCmd(
        val id: CmdId,
        val execId: ExecId,
        val namespaceId: NamespaceId,
        val groupId: GroupId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val code: ExecCode,
        val events: List<Event>
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
        val execId: ExecId,
        val result: ExecResult,
        val state: ExecState
    )

    data class FailCmd(
        val id: CmdId,
        val execId: ExecId,
        val result: ExecResult
    )
}

interface ExecQueryRepository {
    fun get(execId: ExecId) = find(execId) ?: throw NoSuchElementException("Exec not found")
    fun find(execId: ExecId): Exec?
    fun list(query: ExecQuery): List<Exec>
    fun list(execIds: List<ExecId>): List<Exec> = list(
        ExecQuery(
            limit = Limit.all,
            groupIds = listOf(),
            execIds = execIds,
        )
    )

    fun count(query: ExecQuery): ULong
    data class ExecQuery(
        var afterId: ExecId = ExecId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var namespaceIds: List<NamespaceId> = listOf(),
        var groupIds: List<GroupId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var execIds: List<ExecId> = listOf()
    )
}

@Serializable
sealed class Exec : DomainObject<ExecId> {
    abstract val cmdId: CmdId
    abstract override val id: ExecId
    abstract val namespaceId: NamespaceId
    abstract val groupId: GroupId
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val code: ExecCode
    abstract val events: List<Event>

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
    override val namespaceId: NamespaceId,
    override val groupId: GroupId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val code: ExecCode,
    override val events: List<Event>
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
    override val namespaceId get() = plannedExec.namespaceId
    override val groupId get() = plannedExec.groupId
    override val correlation get() = plannedExec.correlation
    override val inputs get() = plannedExec.inputs
    override val code get() = plannedExec.code
    override val events get() = plannedExec.events
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
    override val namespaceId get() = scheduledExec.namespaceId
    override val groupId get() = scheduledExec.groupId
    override val correlation get() = scheduledExec.correlation
    override val inputs get() = scheduledExec.inputs
    override val code get() = scheduledExec.code
    override val events get() = scheduledExec.events
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
    override val namespaceId get() = queuedExec.namespaceId
    override val groupId get() = queuedExec.groupId
    override val correlation get() = queuedExec.correlation
    override val inputs get() = queuedExec.inputs
    override val code get() = queuedExec.code
    override val events get() = queuedExec.events
    override fun toString(): String {
        return "StartedExec($id)"
    }
}

@Serializable
class CompletedExec(
    override val cmdId: CmdId,
    override val id: ExecId,
    val startedExec: StartedExec,
    val completedAt: CompletedAt,
    val result: ExecResult,
    val state: ExecState
) : Exec() {
    override val status = ExecStatus.Completed
    override val namespaceId get() = startedExec.namespaceId
    override val groupId get() = startedExec.groupId
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val code get() = startedExec.code
    override val events get() = startedExec.events

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
    val result: ExecResult
) : Exec() {
    override val status = ExecStatus.Failed
    override val namespaceId get() = startedExec.namespaceId
    override val groupId get() = startedExec.groupId
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val code get() = startedExec.code
    override val events get() = startedExec.events
    override fun toString(): String {
        return "FailedExec($id)"
    }
}