package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.Func
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import kotlinx.serialization.Serializable

//each invocation should produce an event

//FIXME
//@Serializable
//@DomainNotificationTopic("invocation::adhoc")
//data class AdhocInvocationEvent(
//    override val shard: Shard,
//) : Event()

@Serializable
@SystemEventTopic("invocation::adhoc")
data class AdhocInvocationEvent(
    override val shard: Shard,
    val reqId: ReqId,
    val code: Code
) : Event()

@Serializable
@SystemEventTopic("invocation::one_shot")
data class OneshotInvocationEvent(
    override val shard: Shard,
    val reqId: ReqId,
    val func: Func
) : Event()


@Serializable
@SystemEventTopic("invocation::fixed_delay")
data class FixedDelayInvocationEvent(
    override val shard: Shard,
    val func: Func,
    val trigger: Trigger
) : Event()

@Serializable
@SystemEventTopic("invocation::event")
data class EventInvocationEvent(
    override val shard: Shard,
    val func: Func,
    val trigger: EventTrigger,
    val events: List<Event>
) : Event()
