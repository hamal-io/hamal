package io.hamal.repository.api.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.KuaMap
import io.hamal.repository.api.*
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt
import io.hamal.repository.record.exec.*
import java.time.Instant

data class ExecEntity(
    override val cmdId: CmdId,
    override val id: ExecId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val flowId: FlowId,
    val groupId: GroupId,

    var status: ExecStatus? = null,
    var correlation: Correlation? = null,
    var inputs: ExecInputs? = null,
    var code: ExecCode? = null,
    var plannedAt: Instant? = null,
    var scheduledAt: Instant? = null,
    var invocation: Invocation? = null,
    var result: ExecResult? = null,
    var state: ExecState? = null

) : RecordEntity<ExecId, ExecRecord, Exec> {

    override fun apply(rec: ExecRecord): ExecEntity {
        return when (rec) {
            is ExecPlannedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                flowId = rec.flowId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                status = ExecStatus.Planned,
                correlation = rec.correlation,
                inputs = rec.inputs,
                code = rec.code,
                invocation = rec.invocation,
                plannedAt = Instant.now(), // FIXME
                recordedAt = rec.recordedAt()
            )

            is ExecScheduledRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Scheduled,
                scheduledAt = Instant.now(), // FIXME
                recordedAt = rec.recordedAt()
            )

            is ExecQueuedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Queued,
                recordedAt = rec.recordedAt()
//                enqueuedAt = Instant.now() // FIXME

            )

            is ExecStartedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Started,
                recordedAt = rec.recordedAt()
//                startedAt = Instant.now() // FIXME
                //picked by :platform:runner id..
            )

            is ExecCompletedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Completed,
//                enqueuedAt = Instant.now() // FIXME
                result = rec.result,
                recordedAt = rec.recordedAt(),
                state = rec.state
            )

            is ExecFailedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Failed,
                result = rec.result,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Exec {

        val plannedExec = PlannedExec(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            flowId = flowId,
            groupId = groupId,
            correlation = correlation,
            inputs = inputs ?: ExecInputs(KuaMap()),
            code = code ?: ExecCode(),
            invocation = invocation!!
        )

        if (status == ExecStatus.Planned) return plannedExec

        val scheduledExec = ScheduledExec(cmdId, id, recordedAt.toUpdatedAt(), plannedExec, ExecScheduledAt.now())
        if (status == ExecStatus.Scheduled) return scheduledExec

        val queuedExec = QueuedExec(cmdId, id, recordedAt.toUpdatedAt(), scheduledExec, ExecQueuedAt.now())
        if (status == ExecStatus.Queued) return queuedExec

        val startedExec = StartedExec(cmdId, id, recordedAt.toUpdatedAt(), queuedExec)
        if (status == ExecStatus.Started) return startedExec

        return when (status) {
            ExecStatus.Completed -> CompletedExec(
                cmdId,
                id,
                recordedAt.toUpdatedAt(),
                startedExec,
                ExecCompletedAt.now(),
                result!!,
                state!!
            )

            ExecStatus.Failed -> FailedExec(
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
    check(firstRecord is ExecPlannedRecord)

    var result = ExecEntity(
        id = firstRecord.entityId,
        flowId = firstRecord.flowId,
        groupId = firstRecord.groupId,
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