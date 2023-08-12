package io.hamal.backend.repository.api.record.exec

import io.hamal.backend.repository.api.*
import io.hamal.backend.repository.record.RecordEntity
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.backend.repository.record.exec.*
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.TableType
import java.time.Instant

data class Entity(
    override val id: ExecId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,


    var status: ExecStatus? = null,
    var correlation: Correlation? = null,
    var inputs: ExecInputs? = null,
    var code: CodeType? = null,
    var plannedAt: Instant? = null,
    var scheduledAt: Instant? = null,
    var invocation: Invocation? = null,
    var cause: ErrorType? = null

) : RecordEntity<ExecId, ExecRecord, Exec> {

    override fun apply(rec: ExecRecord): Entity {
        return when (rec) {
            is ExecPlannedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Planned,
                correlation = rec.correlation,
                inputs = rec.inputs,
                code = rec.code,
                invocation = rec.invocation,
                plannedAt = Instant.now(), // FIXME
            )

            is ExecScheduledRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Scheduled,
                scheduledAt = Instant.now() // FIXME

            )

            is ExecQueuedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Queued,
//                enqueuedAt = Instant.now() // FIXME
            )

            is ExecStartedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Started,
//                startedAt = Instant.now() // FIXME
                //picked by :faas:runner id..
            )

            is ExecCompletedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Completed,
//                enqueuedAt = Instant.now() // FIXME
            )

            is ExecFailedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Failed,
                cause = rec.cause
            )

            else -> TODO()
        }
    }

    override fun toDomainObject(): Exec {

        val plannedExec = PlannedExec(
            cmdId = cmdId,
            id = id,
            correlation = correlation,
            inputs = inputs ?: ExecInputs(TableType()),
            code = code!!,
            invocation = invocation!!
        )

        if (status == ExecStatus.Planned) return plannedExec

        val scheduledExec = ScheduledExec(cmdId, id, plannedExec, ScheduledAt.now())
        if (status == ExecStatus.Scheduled) return scheduledExec

        val queuedExec = QueuedExec(cmdId, id, scheduledExec, QueuedAt.now())
        if (status == ExecStatus.Queued) return queuedExec

        val startedExec = StartedExec(cmdId, id, queuedExec)
        if (status == ExecStatus.Started) return startedExec

        return when (status) {
            ExecStatus.Completed -> CompletedExec(cmdId, id, startedExec, CompletedAt.now())
            ExecStatus.Failed -> FailedExec(cmdId, id, startedExec, FailedAt.now(), cause!!)
            else -> TODO()
        }
    }
}

fun List<ExecRecord>.createEntity(): Entity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is ExecPlannedRecord)

    var result = Entity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
    )

    forEach { record ->
        result = result.apply(record)
    }


    return result
}