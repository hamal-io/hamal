package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Exec : DomainObject<ExecId> {
    abstract override val id: ExecId
    abstract val cmdId: CmdId
    abstract val accountId: AccountId
    abstract val status: ExecStatus

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val secrets: ExecSecrets
    abstract val code: Code
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
    override val accountId: AccountId,

    override val id: ExecId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
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
    override val accountId get() = plannedExec.accountId
    override val correlation get() = plannedExec.correlation
    override val inputs get() = plannedExec.inputs
    override val secrets get() = plannedExec.secrets
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
    override val accountId get() = scheduledExec.accountId
    override val correlation get() = scheduledExec.correlation
    override val inputs get() = scheduledExec.inputs
    override val secrets get() = scheduledExec.secrets
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
    //FIXME inflightSince
) : Exec() {
    override val status = ExecStatus.Started
    override val accountId get() = queuedExec.accountId
    override val correlation get() = queuedExec.correlation
    override val inputs get() = queuedExec.inputs
    override val secrets get() = queuedExec.secrets
    override val code get() = queuedExec.code
    override val invocation get() = queuedExec.invocation
    override fun toString(): String {
        return "InFlightExec($id)"
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

    override val accountId get() = startedExec.accountId
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val secrets get() = startedExec.secrets
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
) : Exec() {
    override val status = ExecStatus.Failed
    override val accountId get() = startedExec.accountId
    override val correlation get() = startedExec.correlation
    override val inputs get() = startedExec.inputs
    override val secrets get() = startedExec.secrets
    override val code get() = startedExec.code
    override val invocation get() = startedExec.invocation
    override fun toString(): String {
        return "FailedExec($id)"
    }

}