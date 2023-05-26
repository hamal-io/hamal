package io.hamal.backend.event

import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.Shard
import kotlinx.serialization.Serializable

//each invocation should produce an event

//FIXME
//@Serializable
//@DomainNotificationTopic("invocation::adhoc")
//data class AdhocInvocationEvent(
//    override val shard: Shard,
//) : Event()

@Serializable
@DomainNotificationTopic("invocation::trigger")
data class TriggerInvocationEvent(
    override val shard: Shard,
    val func: Func,
    val trigger: Trigger
) : Event()
