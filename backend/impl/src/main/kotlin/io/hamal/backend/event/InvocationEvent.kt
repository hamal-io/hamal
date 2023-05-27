package io.hamal.backend.event

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
@DomainNotificationTopic("invocation::adhoc")
data class AdhocInvocationEvent(
    override val shard: Shard,
    val reqId: ReqId,
    val code: Code
) : Event()

@Serializable
@DomainNotificationTopic("invocation::http")
data class ApiInvocationEvent(
    override val shard: Shard,
    val func: Func
) : Event()


@Serializable
@DomainNotificationTopic("invocation::trigger")
data class TriggerInvocationEvent(
    override val shard: Shard,
    val func: Func,
    val trigger: Trigger
) : Event()
