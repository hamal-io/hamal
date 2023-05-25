package io.hamal.backend.repository.api.domain.exec

import io.hamal.backend.repository.api.domain.trigger.InvokedTrigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface Exec : DomainObject<ExecId> {
    override val id: ExecId
    val state: ExecState
    val code: Code
    val invokedTrigger: InvokedTrigger
}

@Serializable
class PlannedExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger
) : Exec {
    override val state = ExecState.Planned
    override fun toString(): String {
        return "PlannedExec($id)"
    }

}

@Serializable
class ScheduledExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger,
    val scheduledAt: ScheduledAt
) : Exec {
    override val state = ExecState.Scheduled
    override fun toString(): String {
        return "ScheduledExec($id)"
    }

}

@Serializable
class QueuedExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger,
    val queuedAt: QueuedAt
) : Exec {
    override val state = ExecState.Queued
    override fun toString(): String {
        return "QueuedExec($id)"
    }
}


@Serializable
class StartedExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger
) : Exec {
    override val state = ExecState.Started
    override fun toString(): String {
        return "StartedExec($id)"
    }

}

@Serializable
class CompletedExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger,
    val completedAt: CompletedAt
) : Exec {
    override val state = ExecState.Completed
    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExecution(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger
) : Exec {
    override val state = ExecState.Failed
    override fun toString(): String {
        return "FailedExec($id)"
    }

}

@Serializable
class TerminalFailedExec(
    override val id: ExecId,
    override val code: Code,
    override val invokedTrigger: InvokedTrigger
) : Exec {
    override val state = ExecState.TerminalFailed
    override fun toString(): String {
        return "TerminalFailedExec($id)"
    }
}
