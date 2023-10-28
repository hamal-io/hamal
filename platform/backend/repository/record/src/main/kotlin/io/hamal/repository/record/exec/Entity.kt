package io.hamal.repository.api.record.exec

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.Event
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.repository.api.*
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.exec.*
import java.time.Instant

data class ExecEntity(
    override val cmdId: CmdId,
    override val id: ExecId,
    val namespaceId: NamespaceId,
    val groupId: GroupId,
    override val sequence: RecordSequence,


    var status: ExecStatus? = null,
    var correlation: Correlation? = null,
    var inputs: ExecInputs? = null,
    var code: ExecCode? = null,
    var plannedAt: Instant? = null,
    var scheduledAt: Instant? = null,
    var events: List<Event>? = null,
    var result: ExecResult? = null,
    var state: ExecState? = null

) : RecordEntity<ExecId, ExecRecord, Exec> {

    override fun apply(rec: ExecRecord): ExecEntity {
        return when (rec) {
            is ExecPlannedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                namespaceId = rec.namespaceId,
                groupId = rec.groupId,
                sequence = rec.sequence(),
                status = ExecStatus.Planned,
                correlation = rec.correlation,
                inputs = rec.inputs,
                code = rec.code,
                events = rec.events,
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
                //picked by :platform:runner id..
            )

            is ExecCompletedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Completed,
//                enqueuedAt = Instant.now() // FIXME
                result = rec.result,
                state = rec.state
            )

            is ExecFailedRecord -> copy(
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                status = ExecStatus.Failed,
                result = rec.result
            )

            else -> TODO()
        }
    }

    override fun toDomainObject(): Exec {

        val plannedExec = PlannedExec(
            cmdId = cmdId,
            id = id,
            namespaceId = namespaceId,
            groupId = groupId,
            correlation = correlation,
            inputs = inputs ?: ExecInputs(MapType()),
            code = code ?: ExecCode(),
            events = events ?: listOf()
        )

        if (status == ExecStatus.Planned) return plannedExec

        val scheduledExec = ScheduledExec(cmdId, id, plannedExec, ScheduledAt.now())
        if (status == ExecStatus.Scheduled) return scheduledExec

        val queuedExec = QueuedExec(cmdId, id, scheduledExec, QueuedAt.now())
        if (status == ExecStatus.Queued) return queuedExec

        val startedExec = StartedExec(cmdId, id, queuedExec)
        if (status == ExecStatus.Started) return startedExec

        return when (status) {
            ExecStatus.Completed -> CompletedExec(cmdId, id, startedExec, CompletedAt.now(), result!!, state!!)
            ExecStatus.Failed -> FailedExec(cmdId, id, startedExec, FailedAt.now(), result!!)
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
        namespaceId = firstRecord.namespaceId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence()
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