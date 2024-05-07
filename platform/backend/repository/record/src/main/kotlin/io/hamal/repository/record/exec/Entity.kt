package io.hamal.repository.api.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Companion.ExecStatus
import io.hamal.repository.api.Exec
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt
import io.hamal.repository.record.exec.ExecRecord

data class ExecEntity(
    override val cmdId: CmdId,
    override val id: ExecId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val triggerId: TriggerId?,
    val namespaceId: NamespaceId,
    val workspaceId: WorkspaceId,

    var status: ExecStatus? = null,
    var correlation: Correlation? = null,
    var inputs: ExecInputs? = null,
    var code: ExecCode? = null,
    var plannedAt: ExecPlannedAt? = null,
    var scheduledAt: ExecScheduledAt? = null,
    var queuedAt: ExecQueuedAt? = null,
    var startedAt: ExecStartedAt? = null,
    var completedAt: ExecCompletedAt? = null,
    var failedAt: ExecFailedAt? = null,
    var statusCode: ExecStatusCode? = null,
    var result: ExecResult? = null,
    var state: ExecState? = null

) : RecordEntity<ExecId, ExecRecord, Exec> {

    override fun apply(rec: ExecRecord): ExecEntity {
        return when (rec) {
            is ExecRecord.Planned -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                namespaceId = rec.namespaceId,
                workspaceId = rec.workspaceId,
                sequence = rec.sequence(),
                status = ExecStatus(Planned),
                correlation = rec.correlation,
                inputs = rec.inputs,
                code = rec.code,
                plannedAt = ExecPlannedAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt()
            )

            is ExecRecord.Scheduled -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus(Scheduled),
                scheduledAt = ExecScheduledAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt()
            )

            is ExecRecord.Queued -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus(Queued),
                queuedAt = ExecQueuedAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt(),
            )

            is ExecRecord.Started -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus(Started),
                startedAt = ExecStartedAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt()
            )

            is ExecRecord.Completed -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus(Completed),
                completedAt = ExecCompletedAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt(),
                statusCode = rec.statusCode,
                result = rec.result,
                state = rec.state
            )

            is ExecRecord.Failed -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus(Failed),
                failedAt = ExecFailedAt(rec.recordedAt().value),
                recordedAt = rec.recordedAt(),
                statusCode = rec.statusCode,
                result = rec.result
            )
        }
    }

    override fun toDomainObject(): Exec {

        val plannedExec = Exec.Planned(
            cmdId = cmdId,
            id = id,
            triggerId = triggerId,
            updatedAt = recordedAt.toUpdatedAt(),
            namespaceId = namespaceId,
            workspaceId = workspaceId,
            correlation = correlation,
            inputs = inputs ?: ExecInputs(ValueObject.empty),
            code = code ?: ExecCode(),
            plannedAt = plannedAt!!
        )

        if (status == ExecStatus(Planned)) return plannedExec

        val scheduledExec = Exec.Scheduled(
            cmdId = cmdId,
            exec = plannedExec,
            scheduledAt = scheduledAt!!
        )
        if (status == ExecStatus(Scheduled)) return scheduledExec

        val queuedExec = Exec.Queued(
            cmdId = cmdId,
            exec = scheduledExec,
            queuedAt = queuedAt!!
        )
        if (status == ExecStatus(Queued)) return queuedExec

        val startedExec = Exec.Started(
            cmdId = cmdId,
            exec = queuedExec,
            startedAt = startedAt!!
        )
        if (status == ExecStatus(Started)) return startedExec

        return when (status) {
            ExecStatus(Completed) -> Exec.Completed(
                cmdId = cmdId,
                exec = startedExec,
                completedAt = completedAt!!,
                statusCode = statusCode!!,
                result = result!!,
                state = state!!
            )

            ExecStatus(Failed) -> Exec.Failed(
                cmdId = cmdId,
                exec = startedExec,
                failedAt = failedAt!!,
                statusCode = statusCode!!,
                result = result!!
            )

            else -> TODO()
        }
    }
}

fun List<ExecRecord>.createEntity(): ExecEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is ExecRecord.Planned)

    var result = ExecEntity(
        cmdId = firstRecord.cmdId,
        id = firstRecord.entityId,
        namespaceId = firstRecord.namespaceId,
        workspaceId = firstRecord.workspaceId,
        triggerId = firstRecord.triggerId,
        plannedAt = ExecPlannedAt(firstRecord.recordedAt().value),
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record -> result = result.apply(record) }

    return result
}

object CreateExecFromRecords : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): Exec {
        return recs.createEntity().toDomainObject()
    }
}