package io.hamal.backend.core.exec

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.trigger.InvokedTrigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface Exec : DomainObject<ExecId> {
    override val id: ExecId
    val state: ExecutionState
    val func: Func
    val trigger: InvokedTrigger
}

@Serializable
class PlannedExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger
) : Exec {
    override val state = ExecutionState.Planned
    override fun toString(): String {
        return "PlannedExec($id)"
    }

}

@Serializable
class ScheduledExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger,
    val scheduledAt: ScheduledAt
) : Exec {
    override val state = ExecutionState.Scheduled
    override fun toString(): String {
        return "ScheduledExec($id)"
    }

}

@Serializable
class QueuedExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger,
    val queuedAt: QueuedAt
) : Exec {
    override val state = ExecutionState.Queued
    override fun toString(): String {
        return "QueuedExec($id)"
    }
}


@Serializable
class StartedExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger
) : Exec {
    override val state = ExecutionState.Started
    override fun toString(): String {
        return "StartedExec($id)"
    }

}

@Serializable
class CompleteExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger,
    val completedAt: CompletedAt
) : Exec {
    override val state = ExecutionState.Completed
    override fun toString(): String {
        return "CompletedExec($id)"
    }
}

@Serializable
class FailedExecution(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger
) : Exec {
    override val state = ExecutionState.Failed
    override fun toString(): String {
        return "FailedExec($id)"
    }

}

@Serializable
class TerminalFailedExec(
    override val id: ExecId,
    override val func: Func,
    override val trigger: InvokedTrigger
) : Exec {
    override val state = ExecutionState.TerminalFailed
    override fun toString(): String {
        return "TerminalFailedExec($id)"
    }
}
