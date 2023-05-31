package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.InvocationSecrets
import kotlinx.serialization.Serializable

@Serializable
@SystemEventTopic("invocation::adhoc")
data class AdhocInvocationEvent(
//    override val reqId: ReqId,
    override val shard: Shard,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val code: Code
) : Event()

@Serializable
@SystemEventTopic("invocation::one_shot")
data class OneshotInvocationEvent(
//    override val reqId: ReqId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func
) : Event()


@Serializable
@SystemEventTopic("invocation::fixed_delay")
data class FixedDelayInvocationEvent(
//    override val reqId: ReqId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func,
    val trigger: Trigger
) : Event()

@Serializable
@SystemEventTopic("invocation::event")
data class EventInvocationEvent(
//    override val reqId: ReqId,
    override val shard: Shard,
    val correlationId: CorrelationId,
    val inputs: InvocationInputs,
    val secrets: InvocationSecrets,
    val func: Func,
    val trigger: EventTrigger,
    val events: List<Event>
) : Event()
