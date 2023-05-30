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
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
    override val invocation: Invocation,
    val scheduledAt: ScheduledAt
) : Exec() {
    override val status = ExecStatus.Scheduled
    override fun toString(): String {
        return "ScheduledExec($id)"
    }

}

@Serializable
class QueuedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
    override val invocation: Invocation,
    val queuedAt: QueuedAt
) : Exec() {
    override val status = ExecStatus.Queued
    override fun toString(): String {
        return "QueuedExec($id)"
    }
}


@Serializable
class InFlightExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
    override val invocation: Invocation
) : Exec() {
    override val status = ExecStatus.InFlight
    override fun toString(): String {
        return "InFlightExec($id)"
    }
}

@Serializable
class CompletedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
    override val invocation: Invocation,
    val completedAt: CompletedAt
) : Exec() {
    override val status = ExecStatus.Completed
    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val inputs: ExecInputs,
    override val secrets: ExecSecrets,
    override val code: Code,
    override val invocation: Invocation
) : Exec() {
    override val status = ExecStatus.Failed
    override fun toString(): String {
        return "FailedExec($id)"
    }

}