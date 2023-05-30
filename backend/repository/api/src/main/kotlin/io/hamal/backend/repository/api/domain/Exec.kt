package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Exec : DomainObject<ExecId>() {
    abstract val status: ExecStatus
    abstract val reqId: ReqId

    abstract val correlation: Correlation?
    abstract val inputs: ExecInputs
    abstract val secrets: ExecSecrets
    abstract val code: Code
    abstract val invocation: Invocation
}

@Serializable
class PlannedExec(
    override val id: ExecId,
    override val reqId: ReqId,
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
    override val id: ExecId,
    override val reqId: ReqId,
    val plannedExec: PlannedExec,
    val scheduledAt: ScheduledAt
) : Exec() {
    override val status = ExecStatus.Scheduled
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
    override val id: ExecId,
    override val reqId: ReqId,
    val scheduledExec: ScheduledExec,
    val queuedAt: QueuedAt
) : Exec() {
    override val status = ExecStatus.Queued
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
class InFlightExec(
    override val id: ExecId,
    override val reqId: ReqId,
    val queuedExec: QueuedExec
    //FIXME inflightSince
) : Exec() {
    override val status = ExecStatus.InFlight
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
    override val id: ExecId,
    override val reqId: ReqId,
    val inFlightExec: InFlightExec,
    val completedAt: CompletedAt
) : Exec() {
    override val status = ExecStatus.Completed

    override val correlation get() = inFlightExec.correlation
    override val inputs get() = inFlightExec.inputs
    override val secrets get() = inFlightExec.secrets
    override val code get() = inFlightExec.code
    override val invocation get() = inFlightExec.invocation

    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    val inFlightExec: InFlightExec,
    //FIXME failedAt
) : Exec() {
    override val status = ExecStatus.Failed
    override val correlation get() = inFlightExec.correlation
    override val inputs get() = inFlightExec.inputs
    override val secrets get() = inFlightExec.secrets
    override val code get() = inFlightExec.code
    override val invocation get() = inFlightExec.invocation
    override fun toString(): String {
        return "FailedExec($id)"
    }

}