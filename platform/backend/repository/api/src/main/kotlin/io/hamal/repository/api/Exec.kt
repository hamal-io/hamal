package io.hamal.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*

interface ExecRepository : ExecCmdRepository, ExecQueryRepository

interface ExecCmdRepository : CmdRepository {
    fun plan(cmd: PlanCmd): Exec.Planned
    fun schedule(cmd: ScheduleCmd): Exec.Scheduled
    fun queue(cmd: QueueCmd): Exec.Queued

    fun complete(cmd: CompleteCmd): Exec.Completed
    fun fail(cmd: FailCmd): Exec.Failed

    fun start(cmd: StartCmd): List<Exec.Started>

    data class PlanCmd(
        val id: CmdId,
        val execId: ExecId,
        val namespaceId: NamespaceId,
        val workspaceId: WorkspaceId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val code: ExecCode,
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
            workspaceIds = listOf(),
            execIds = execIds,
        )
    )

    fun count(query: ExecQuery): Count
    data class ExecQuery(
        var afterId: ExecId = ExecId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1),
        var namespaceIds: List<NamespaceId> = listOf(),
        var workspaceIds: List<WorkspaceId> = listOf(),
        var funcIds: List<FuncId> = listOf(),
        var execIds: List<ExecId> = listOf()
    )
}

sealed class Exec : DomainObject<ExecId>, HasNamespaceId, HasWorkspaceId {
    abstract val cmdId: CmdId
    abstract override val id: ExecId
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val code: ExecCode
    abstract val invocation: Invocation

    val type: ExecType = ExecType(this::class.simpleName!!)

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

    object Adapter : JsonAdapter<Exec> {
        override fun serialize(
            src: Exec,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): Exec {
            val type = json.asJsonObject.get("type").asString
            return context.deserialize(json, classMapping[type]!!.java)
        }

        private val classMapping = listOf(
            Planned::class,
            Scheduled::class,
            Queued::class,
            Started::class,
            Completed::class,
            Failed::class
        ).associateBy { it.simpleName }
    }

    class Planned(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        override val invocation: Invocation,
// FIXME    val plannedAt: PlannedAt
    ) : Exec() {
        override val status = ExecStatus.Planned

        override fun toString(): String {
            return "Planned($id)"
        }

    }

    class Scheduled(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        val plannedExec: Planned,
        val scheduledAt: ExecScheduledAt,
    ) : Exec() {
        override val status = ExecStatus.Scheduled
        override val namespaceId get() = plannedExec.namespaceId
        override val workspaceId get() = plannedExec.workspaceId
        override val correlation get() = plannedExec.correlation
        override val inputs get() = plannedExec.inputs
        override val code get() = plannedExec.code
        override val invocation get() = plannedExec.invocation
        override fun toString(): String {
            return "Scheduled($id)"
        }

    }

    class Queued(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        val scheduledExec: Scheduled,
        val queuedAt: ExecQueuedAt,
    ) : Exec() {
        override val status = ExecStatus.Queued
        override val namespaceId get() = scheduledExec.namespaceId
        override val workspaceId get() = scheduledExec.workspaceId
        override val correlation get() = scheduledExec.correlation
        override val inputs get() = scheduledExec.inputs
        override val code get() = scheduledExec.code
        override val invocation get() = scheduledExec.invocation
        override fun toString(): String {
            return "Queued($id)"
        }
    }


    class Started(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        val queuedExec: Queued
    ) : Exec() {
        override val status = ExecStatus.Started
        override val namespaceId get() = queuedExec.namespaceId
        override val workspaceId get() = queuedExec.workspaceId
        override val correlation get() = queuedExec.correlation
        override val inputs get() = queuedExec.inputs
        override val code get() = queuedExec.code
        override val invocation get() = queuedExec.invocation
        override fun toString(): String {
            return "Started($id)"
        }
    }

    class Completed(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        val startedExec: Started,
        val completedAt: ExecCompletedAt,
        val result: ExecResult,
        val state: ExecState
    ) : Exec() {
        override val status = ExecStatus.Completed
        override val namespaceId get() = startedExec.namespaceId
        override val workspaceId get() = startedExec.workspaceId
        override val correlation get() = startedExec.correlation
        override val inputs get() = startedExec.inputs
        override val code get() = startedExec.code
        override val invocation get() = startedExec.invocation

        override fun toString(): String {
            return "Completed($id)"
        }
    }

    class Failed(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val updatedAt: UpdatedAt,
        val startedExec: Started,
        //FIXME failedAt
        val failedAt: ExecFailedAt,
        val result: ExecResult
    ) : Exec() {
        override val status = ExecStatus.Failed
        override val namespaceId get() = startedExec.namespaceId
        override val workspaceId get() = startedExec.workspaceId
        override val correlation get() = startedExec.correlation
        override val inputs get() = startedExec.inputs
        override val code get() = startedExec.code
        override val invocation get() = startedExec.invocation
        override fun toString(): String {
            return "Failed($id)"
        }
    }
}

