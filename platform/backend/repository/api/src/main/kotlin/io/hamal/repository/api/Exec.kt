package io.hamal.repository.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecStatus.Companion.ExecStatus

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
        val triggerId: TriggerId?,
        val namespaceId: NamespaceId,
        val workspaceId: WorkspaceId,
        val correlation: Correlation?,
        val inputs: ExecInputs,
        val code: ExecCode
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
        val statusCode: ExecStatusCode,
        val result: ExecResult,
        val state: ExecState
    )

    data class FailCmd(
        val id: CmdId,
        val execId: ExecId,
        val statusCode: ExecStatusCode,
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
    abstract val triggerId: TriggerId?
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val code: ExecCode

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

    object Adapter : AdapterGeneric<Exec> {
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
            val status = context.deserialize<ExecStatus>(
                json.asJsonObject.get("status"), ExecStatus::class.java
            )
            return context.deserialize(json, classMapping[status.enumValue]!!.java)
        }

        private val classMapping = mapOf(
            Planned to Exec.Planned::class,
            Scheduled to Exec.Scheduled::class,
            Queued to Exec.Queued::class,
            Started to Exec.Started::class,
            Completed to Exec.Completed::class,
            Failed to Exec.Failed::class
        )
    }

    class Planned(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt
    ) : Exec() {
        override val status = ExecStatus(Planned)

        override fun toString(): String {
            return "Planned($id)"
        }
    }

    class Scheduled(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt,
        val scheduledAt: ExecScheduledAt,
    ) : Exec() {

        constructor(cmdId: CmdId, exec: Planned, scheduledAt: ExecScheduledAt) : this(
            cmdId = cmdId,
            id = exec.id,
            triggerId = exec.triggerId,
            updatedAt = UpdatedAt(scheduledAt.value),
            namespaceId = exec.namespaceId,
            workspaceId = exec.workspaceId,
            correlation = exec.correlation,
            inputs = exec.inputs,
            code = exec.code,
            plannedAt = exec.plannedAt,
            scheduledAt = scheduledAt
        )

        override val status = ExecStatus(Scheduled)

        override fun toString(): String {
            return "Scheduled($id)"
        }
    }

    class Queued(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt,
        val scheduledAt: ExecScheduledAt,
        val queuedAt: ExecQueuedAt,
    ) : Exec() {

        constructor(cmdId: CmdId, exec: Scheduled, queuedAt: ExecQueuedAt) : this(
            cmdId = cmdId,
            id = exec.id,
            triggerId = exec.triggerId,
            updatedAt = UpdatedAt(queuedAt.value),
            namespaceId = exec.namespaceId,
            workspaceId = exec.workspaceId,
            correlation = exec.correlation,
            inputs = exec.inputs,
            code = exec.code,
            plannedAt = exec.plannedAt,
            scheduledAt = exec.scheduledAt,
            queuedAt = queuedAt,
        )

        override val status = ExecStatus(Queued)

        override fun toString(): String {
            return "Queued($id)"
        }
    }

    class Started(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt,
        val scheduledAt: ExecScheduledAt,
        val queuedAt: ExecQueuedAt,
        val startedAt: ExecStartedAt
    ) : Exec() {

        constructor(cmdId: CmdId, exec: Queued, startedAt: ExecStartedAt) : this(
            cmdId = cmdId,
            id = exec.id,
            triggerId = exec.triggerId,
            updatedAt = UpdatedAt(startedAt.value),
            namespaceId = exec.namespaceId,
            workspaceId = exec.workspaceId,
            correlation = exec.correlation,
            inputs = exec.inputs,
            code = exec.code,
            plannedAt = exec.plannedAt,
            scheduledAt = exec.scheduledAt,
            queuedAt = exec.queuedAt,
            startedAt = startedAt
        )

        override val status = ExecStatus(Started)

        override fun toString(): String {
            return "Started($id)"
        }
    }

    class Completed(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt,
        val scheduledAt: ExecScheduledAt,
        val queuedAt: ExecQueuedAt,
        val startedAt: ExecStartedAt,
        val completedAt: ExecCompletedAt,
        val statusCode: ExecStatusCode,
        val result: ExecResult,
        val state: ExecState
    ) : Exec() {

        constructor(cmdId: CmdId, exec: Started, completedAt: ExecCompletedAt, statusCode: ExecStatusCode, result: ExecResult, state: ExecState) : this(
            cmdId = cmdId,
            id = exec.id,
            triggerId = exec.triggerId,
            updatedAt = UpdatedAt(completedAt.value),
            namespaceId = exec.namespaceId,
            workspaceId = exec.workspaceId,
            correlation = exec.correlation,
            inputs = exec.inputs,
            code = exec.code,
            plannedAt = exec.plannedAt,
            scheduledAt = exec.scheduledAt,
            queuedAt = exec.queuedAt,
            startedAt = exec.startedAt,
            completedAt = completedAt,
            statusCode = statusCode,
            result = result,
            state = state
        )

        override val status = ExecStatus(Completed)

        override fun toString(): String {
            return "Completed($id)"
        }
    }

    class Failed(
        override val cmdId: CmdId,
        override val id: ExecId,
        override val triggerId: TriggerId?,
        override val updatedAt: UpdatedAt,
        override val namespaceId: NamespaceId,
        override val workspaceId: WorkspaceId,
        override val correlation: Correlation?,
        override val inputs: ExecInputs,
        override val code: ExecCode,
        val plannedAt: ExecPlannedAt,
        val scheduledAt: ExecScheduledAt,
        val queuedAt: ExecQueuedAt,
        val startedAt: ExecStartedAt,
        val failedAt: ExecFailedAt,
        val statusCode: ExecStatusCode,
        val result: ExecResult
    ) : Exec() {

        constructor(cmdId: CmdId, exec: Started, failedAt: ExecFailedAt, statusCode: ExecStatusCode, result: ExecResult) : this(
            cmdId = cmdId,
            id = exec.id,
            triggerId = exec.triggerId,
            updatedAt = UpdatedAt(failedAt.value),
            namespaceId = exec.namespaceId,
            workspaceId = exec.workspaceId,
            correlation = exec.correlation,
            inputs = exec.inputs,
            code = exec.code,
            plannedAt = exec.plannedAt,
            scheduledAt = exec.scheduledAt,
            queuedAt = exec.queuedAt,
            startedAt = exec.startedAt,
            failedAt = failedAt,
            statusCode = statusCode,
            result = result
        )

        override val status = ExecStatus(Failed)

        override fun toString(): String {
            return "Failed($id)"
        }
    }
}

