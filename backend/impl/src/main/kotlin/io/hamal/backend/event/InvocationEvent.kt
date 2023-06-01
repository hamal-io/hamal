package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
@EventTopic("invocation::adhoc")
data class AdhocInvocationEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val execId: ExecId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
) : Event()

@Serializable
@EventTopic("invocation::one_shot")
data class OneshotInvocationEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func
) : Event()


@Serializable
@EventTopic("invocation::fixed_delay")
data class FixedDelayInvocationEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func,
    val trigger: Trigger
) : Event()

@Serializable
@EventTopic("invocation::event")
data class EventInvocationEvent(
//    override val computeId: ComputeId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func,
    val trigger: EventTrigger,
    val events: List<Event>
) : Event()
