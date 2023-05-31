package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.*
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.ExecId
import kotlinx.serialization.Serializable


@Serializable
@EventTopic("exec::planned")
data class ExecPlannedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val plannedExec: PlannedExec
) : Event()

@Serializable
@EventTopic("exec::scheduled")
data class ExecScheduledEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val scheduledExec: ScheduledExec
) : Event()

@Serializable
@EventTopic("exec::queued")
data class ExecutionQueuedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val queuedExec: QueuedExec
) : Event()

@Serializable
@EventTopic("exec::started")
data class ExecutionStartedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val inFlightExec: InFlightExec
) : Event()

@Serializable
@EventTopic("exec::completed")
data class ExecutionCompletedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val completedExec: CompletedExec
) : Event()

@Serializable
@EventTopic("exec::failed")
data class ExecutionFailedEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val failedExec: FailedExec
) : Event()


@Serializable
@EventTopic("exec::completion_requested")
data class ExecCompletionRequestedEvent(
    override val shard: Shard,
//    override val computeId: ComputeId,
    val execId: ExecId,
    val statePayload: StatePayload
) : Event()
