package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Exec : DomainObject<ExecId>() {
    abstract val state: ExecState
    abstract val reqId: ReqId
    abstract val correlation: Correlation?
    abstract val code: Code
    abstract val invocation: Invocation
}

@Serializable
class PlannedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation
) : Exec() {
    override val state = ExecState.Planned
    override fun toString(): String {
        return "PlannedExec($id)"
    }

}

@Serializable
class ScheduledExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation,
    val scheduledAt: ScheduledAt
) : Exec() {
    override val state = ExecState.Scheduled
    override fun toString(): String {
        return "ScheduledExec($id)"
    }

}

@Serializable
class QueuedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation,
    val queuedAt: QueuedAt
) : Exec() {
    override val state = ExecState.Queued
    override fun toString(): String {
        return "QueuedExec($id)"
    }
}


@Serializable
class StartedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation
) : Exec() {
    override val state = ExecState.Started
    override fun toString(): String {
        return "StartedExec($id)"
    }

}

@Serializable
class CompletedExec(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation,
    val completedAt: CompletedAt
) : Exec() {
    override val state = ExecState.Completed
    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExecution(
    override val id: ExecId,
    override val reqId: ReqId,
    override val correlation: Correlation?,
    override val code: Code,
    override val invocation: Invocation
) : Exec() {
    override val state = ExecState.Failed
    override fun toString(): String {
        return "FailedExec($id)"
    }

}