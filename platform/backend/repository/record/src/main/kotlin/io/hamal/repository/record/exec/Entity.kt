package io.hamal.repository.api.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Exec
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt
import io.hamal.repository.record.exec.ExecRecord
import java.time.Instant

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
    var plannedAt: Instant? = null,
    var scheduledAt: Instant? = null,
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
                status = ExecStatus.Planned,
                correlation = rec.correlation,
                inputs = rec.inputs,
                code = rec.code,
                plannedAt = Instant.now(), // FIXME
                recordedAt = rec.recordedAt()
            )

            is ExecRecord.Scheduled -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Scheduled,
                scheduledAt = Instant.now(), // FIXME
                recordedAt = rec.recordedAt()
            )

            is ExecRecord.Queued -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Queued,
                recordedAt = rec.recordedAt()
//                enqueuedAt = Instant.now() // FIXME

            )

            is ExecRecord.Started -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Started,
                recordedAt = rec.recordedAt()
//                startedAt = Instant.now() // FIXME
                //picked by :platform:runner id..
            )

            is ExecRecord.Completed -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Completed,
//                enqueuedAt = Instant.now() // FIXME
                result = rec.result,
                recordedAt = rec.recordedAt(),
                state = rec.state
            )

            is ExecRecord.Failed -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Failed,
                result = rec.result,
                recordedAt = rec.recordedAt()
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
            code = code ?: ExecCode()
        )

        if (status == ExecStatus.Planned) return plannedExec

        val scheduledExec = Exec.Scheduled(cmdId, id, recordedAt.toUpdatedAt(), plannedExec, ExecScheduledAt.now())
        if (status == ExecStatus.Scheduled) return scheduledExec

        val queuedExec = Exec.Queued(cmdId, id, recordedAt.toUpdatedAt(), scheduledExec, ExecQueuedAt.now())
        if (status == ExecStatus.Queued) return queuedExec

        val startedExec = Exec.Started(cmdId, id, recordedAt.toUpdatedAt(), queuedExec)
        if (status == ExecStatus.Started) return startedExec

        return when (status) {
            ExecStatus.Completed -> Exec.Completed(
                cmdId,
                id,
                recordedAt.toUpdatedAt(),
                startedExec,
                ExecCompletedAt.now(),
                result!!,
                state!!
            )

            ExecStatus.Failed -> Exec.Failed(
                cmdId,
                id,
                recordedAt.toUpdatedAt(),
                startedExec,
                ExecFailedAt.now(),
                result!!
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
        id = firstRecord.entityId,
        triggerId = firstRecord.triggerId,
        namespaceId = firstRecord.namespaceId,
        workspaceId = firstRecord.workspaceId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }


    return result
}

object CreateExecFromRecords : CreateDomainObject<ExecId, ExecRecord, Exec> {
    override fun invoke(recs: List<ExecRecord>): Exec {
        return recs.createEntity().toDomainObject()
    }
}