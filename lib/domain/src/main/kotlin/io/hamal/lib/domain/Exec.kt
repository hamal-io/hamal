package io.hamal.lib.domain

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.DomainObject
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.value.CodeValue
import kotlinx.serialization.Serializable

@Serializable
sealed class Exec : DomainObject<ExecId> {
    abstract override val id: ExecId
    abstract val cmdId: CmdId
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val code: CodeValue

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
    override val code: CodeValue,
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
    val failedAt: FailedAt
) : Exec() {
    override val status = ExecStatus.Failed
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val code get() = startedExec.code
    override fun toString(): String {
        return "FailedExec($id)"
    }

}